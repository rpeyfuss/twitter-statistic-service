
import brave.play.filter.ZipkinTraceFilter
import com.reginapeyfuss.services.metrics.PrometheusHttpFilter
import javax.inject.Inject
import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter
import play.filters.gzip.GzipFilter

/*
  * this class overrides the built in Play filter; it includes Prometheus for observability/metrics and Zipkin for tracing
  * @param corsFilter
  * @param gzipFilter
  * @param prometheusHttpFilter
  * @param traceFilter
  */
class Filters @Inject() (corsFilter: CORSFilter,
                         gzipFilter: GzipFilter,
                         prometheusHttpFilter: PrometheusHttpFilter,
                         traceFilter: ZipkinTraceFilter)
    extends DefaultHttpFilters(corsFilter, gzipFilter, prometheusHttpFilter, traceFilter)
