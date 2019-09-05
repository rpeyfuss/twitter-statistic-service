package com.reginapeyfuss.services.metrics

import akka.util.ByteString
import com.github.stijndehaes.playprometheusfilters.utils.WriterAdapter
import javax.inject.Inject
import play.api.http.HttpEntity
import play.api.mvc.{AbstractController, ControllerComponents, ResponseHeader, Result}

import scala.concurrent.ExecutionContext

class PrometheusController @Inject()(cc: ControllerComponents,
                                     metricBuilder: PrometheusMetricBuilder )
                                    (implicit ec: ExecutionContext) extends  AbstractController(cc) {

    def metrics = Action {
        val samples = new StringBuilder()
        val writer = new WriterAdapter(samples)
        writer.write(metricBuilder.buildOutputText)
        writer.close()

        Result(
            header = ResponseHeader(200, Map.empty),
            body = HttpEntity.Strict(ByteString(samples.toString), Some("text/plain"))
        )
    }

}
