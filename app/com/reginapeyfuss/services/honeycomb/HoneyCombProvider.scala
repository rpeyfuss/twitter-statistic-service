package com.reginapeyfuss.services.honeycomb

import akka.actor.{Actor, ActorLogging, Props}

class HoneyCombProvider  extends Actor with ActorLogging{

    def receive = {
        case msg: HoneycombWriter =>
            msg.honeycombClient.honeyCombClient.createEvent()
                .addField("requestId", msg.logMessage.requestId)
                .addField("userId", msg.logMessage.userId)
                .addField("msg", msg.logMessage.message)
                .setTimestamp(msg.logMessage.createTime)
                .send()
            log.info("in receive of HoneyCombProvider")
        case _ => log.error("received no matching message")
    }

    override def preStart(): Unit = {
        log.info("hello from pre start of honeycomb provider")
    }

    override def unhandled(message: Any): Unit = {
        log.warning("unhandled message {} - message from {}", message, sender())
        super.unhandled(message)
    }

}

object HoneyCombProvider {
    def props: Props = Props[HoneyCombProvider]
}
