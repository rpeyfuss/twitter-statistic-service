package com.reginapeyfuss.services.metrics
import akka.stream.Materializer
import javax.inject.{Inject, Singleton}
import org.lyranthe.prometheus.client.{LabelledHistogram, Timer}
import play.api.Logger
import play.api.mvc.{Filter, RequestHeader, Result}
import play.api.routing.Router.Attrs.HandlerDef

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object PrometheusHttpFilter{
    val log: Logger = Logger(this.getClass)
}


@Singleton
class PrometheusHttpFilter @Inject ()(implicit
                                      val mat: Materializer,
                                      builder: PrometheusMetricBuilder,
                                      executionContext: ExecutionContext
                                     ) extends Filter {

    private final val ServerErrorClass = "5xx"

    def apply(nextFilter: RequestHeader => Future[Result])(
        requestHeader: RequestHeader): Future[Result] = {
        val timer = Timer()
        val future = nextFilter(requestHeader)

        getRouteDetails(requestHeader) match {
            case Some(details) =>
                future.onComplete {
                    time(timer) { statusCode =>
                        builder.httpRequestLatency.labelValues(
                            "widgetService",
                            details.method,
                            details.route,
                            statusCode,
                            details.httpType)
                    }
                }

            case None =>
        }
        future
    }

    private def getRouteDetails(requestHeader: RequestHeader): Option[RouteDetails] = {
        for {
            handlerDef    <- requestHeader.attrs.get(HandlerDef)
            method        = handlerDef.method
            routePattern  = handlerDef.path
            httpType      = handlerDef.verb

            route         =  routePattern.replaceAll("<.*?>", "").replaceAll("\\$", ":")
        } yield RouteDetails(method, route, httpType)
    }

    private def statusCodeLabel(result: Result): String = result.header.status + ""

    private def time(timer: Timer)(
        templatedHistogram: String => LabelledHistogram): Try[Result] => Unit = {
        case Success(result) =>
            templatedHistogram(statusCodeLabel(result)).observeDuration(timer)
        case Failure(_) =>
            templatedHistogram(ServerErrorClass).observeDuration(timer)
    }


}
