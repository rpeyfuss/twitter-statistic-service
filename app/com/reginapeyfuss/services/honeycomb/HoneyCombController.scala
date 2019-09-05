package com.reginapeyfuss.services.honeycomb

import akka.actor.{ActorRef, ActorSystem}
import com.reginapeyfuss.services.message.LogMessage
import javax.inject.{Inject, Named}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.ExecutionContext


class HoneyCombController @Inject()(cc: ControllerComponents,
                                    honeyCombClient: HoneyCombClient,
                                    @Named("widgetServiceProvider") widgetServiceProvider: ActorRef,
                                    system: ActorSystem)
                                   (implicit ec: ExecutionContext) extends AbstractController(cc) {


    def testHoneyComb = Action { implicit request: Request[AnyContent] =>
        widgetServiceProvider ! HoneycombWriter(honeyCombClient,
            LogMessage("requestId", "userid2", "testing honeycomb", System.currentTimeMillis()))
        Ok("test")
    }

}
