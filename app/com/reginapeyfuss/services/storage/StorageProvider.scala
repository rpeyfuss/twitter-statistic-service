package com.reginapeyfuss.services.storage

import akka.actor.{Actor, ActorLogging, Props}

class StorageProvider extends Actor with ActorLogging {

    def receive = {
        case _ => log.info("receiving message in StorageProvider")
    }

}
object StorageProvider {
    def props = Props[StorageProvider]
}
