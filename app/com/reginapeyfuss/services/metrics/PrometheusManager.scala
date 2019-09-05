package com.reginapeyfuss.services.metrics

import java.lang.management.{ManagementFactory, MemoryUsage}

import javax.inject.{Inject, Singleton}
import org.lyranthe.prometheus.client.{Counter, Histogram, HistogramBuckets, LabelName, MetricName, MetricType, Registry}
import org.lyranthe.prometheus.client.counter.Counter3
import org.lyranthe.prometheus.client.histogram.Histogram5
import org.lyranthe.prometheus.client.registry.{GaugeMetric, LabelPair, Metric, MetricFamily}
import play.api.inject.ApplicationLifecycle

import scala.collection.JavaConverters._

import org.lyranthe.prometheus.client.registry.{LabelPair, MetricFamily}
import org.lyranthe.prometheus.client.{Histogram, HistogramBuckets, LabelName, MetricName, MetricType, Registry, _}


private case class RouteDetails(method: String, route: String, httpType: String)

@Singleton
class PrometheusMetricBuilder @Inject()(lifecycle: ApplicationLifecycle, implicit val prometheusRegistry: Registry) {

    private val gcBeans = asScalaIteratorConverter(ManagementFactory.getGarbageCollectorMXBeans.iterator()).asScala.toList
    private val memBean     = ManagementFactory.getMemoryMXBean
    private val threadBean  = ManagementFactory.getThreadMXBean
    private val serviceName = "widgetService"

    def buildOutputText: String = prometheusRegistry.outputText // prints all metric values
    // other metrics

    private val httpHistogramBuckets: AnyRef with HistogramBuckets = {
        val buckets = for (p <- Vector[Double](0.0001, 0.001, 0.01, 0.1, 1, 10);
                           s <- Vector(1, 2, 5)) yield p * s
        HistogramBuckets(buckets: _*)
    }

    val exampleCounterRequests: Counter3 = Counter(metric"total_example_counter", "Total example counters:")
        .labels(label"service", label"class", label"method").register

    val httpRequestLatency: Histogram5 =
        Histogram(MetricName("http_request_duration_seconds"),
            "Duration of HTTP request in seconds")(hb = httpHistogramBuckets)
            .labels (LabelName("service"), LabelName("method"), LabelName("path"), LabelName("status"), LabelName("type"))
            .register

    val jvm_memory_usage_bytes: MetricFamily = new MetricFamily {
        override def name: MetricName = metric"jvm_memory_usage_bytes"

        override def help: String        = "JVM Memory Usage Bytes"
        override def escapedHelp: String = help

        override def metricType: MetricType.Gauge.type = MetricType.Gauge

        override def collect(): List[Metric] = {
            def metrics(region: String, memUsage: MemoryUsage): List[GaugeMetric] = {
                def metric(memType: String, memStatistic: Long): GaugeMetric =
                    GaugeMetric(List(
                        LabelPair(label"service", serviceName),
                        LabelPair(label"region", region),
                        LabelPair(label"status", memType)
                    ),
                        memStatistic)

                List(
                    metric("committed", memUsage.getCommitted),
                    metric("init", memUsage.getInit),
                    metric("max", memUsage.getMax),
                    metric("used", memUsage.getUsed)
                )
            }

            metrics("heap", memBean.getHeapMemoryUsage) ::: metrics(
                "non-heap",
                memBean.getNonHeapMemoryUsage)
        }
    }

    jvm_memory_usage_bytes.register

    val jvm_gc_collection_count: MetricFamily = new MetricFamily {
        override def name: MetricName = metric"jvm_gc_collection_count"

        override def help: String        = "JVM Garbage Collector Count"
        override def escapedHelp: String = help

        override def metricType = MetricType.Counter

        override def collect(): List[Metric] = {
            gcBeans flatMap { bean =>
                val nameTuple1 = LabelPair(label"service", serviceName)
                val nameTuple2 = LabelPair(label"type", bean.getName)
                List(
                    GaugeMetric(List(nameTuple1, nameTuple2), bean.getCollectionCount)
                )
            }
        }
    }

    jvm_gc_collection_count.register

    val jvm_gc_duration_seconds = new MetricFamily {
        override def name: MetricName = metric"jvm_gc_duration_seconds"

        override def help: String        = "JVM Cumulative Garbage Collector Duration"
        override def escapedHelp: String = help

        override def metricType: MetricType.Counter.type = MetricType.Counter

        val currentTime: Long = System.currentTimeMillis()

        override def collect(): List[Metric] = {
            gcBeans flatMap { bean =>
                val nameTuple1 = LabelPair(label"service", serviceName)
                val nameTuple2 = LabelPair(label"type", bean.getName)
                val startTime = bean.getCollectionTime / 1e3
                List(GaugeMetric(List(nameTuple1, nameTuple2), startTime))
            }
        }
    }


    jvm_gc_duration_seconds.register

    val jvm_thread_count: MetricFamily = new MetricFamily {
        override def name: MetricName = metric"jvm_thread_count"

        override def help: String        = "JVM Thread Count"
        override def escapedHelp: String = help

        override def metricType: MetricType = MetricType.Gauge

        override def collect(): List[GaugeMetric] = {
            def metric(memType: String, memStatistic: Long): GaugeMetric =
                GaugeMetric(List(
                    LabelPair(label"service", serviceName),
                    LabelPair(label"type", memType)
                ),
                    memStatistic)
            val daemon = threadBean.getDaemonThreadCount
            val all    = threadBean.getThreadCount
            List(
                metric("daemon", daemon),
                metric("non-daemon", all-daemon)
            )
        }
    }

    jvm_thread_count.register
}
