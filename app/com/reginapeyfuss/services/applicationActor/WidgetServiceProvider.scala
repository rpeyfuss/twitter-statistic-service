package com.reginapeyfuss.services.applicationActor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.reginapeyfuss.services.honeycomb.{HoneyCombProvider, HoneycombWriter}

class WidgetServiceProvider extends Actor with ActorLogging {

    var honeycombProvider: ActorRef =_
    var storage: ActorRef = _

    def receive = {
        case hcw: HoneycombWriter =>
            log.info("received message to pass on to honeycomb provider")
            honeycombProvider ! hcw
        case _ => log.info("receiving messages in WigetServiceProvider")
    }

    override def preStart(): Unit = {
        log.info("hello from pre start of widgetServiceProvider")
        honeycombProvider = context.actorOf(Props[HoneyCombProvider], name = "honeycombProvider")
    }

    override def unhandled(message: Any): Unit = {
        log.warning("unhandled message {} - message from {}", message, sender())
        super.unhandled(message)
    }
}

object WidgetServiceProvider {
    def props: Props = Props[WidgetServiceProvider]
}
