package com.reginapeyfuss.services.storage

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.ConnectionException
import com.reginapeyfuss.services.utilities.ConfigUtilities
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api._
import com.reginapeyfuss.services.storage.StorageProvider._

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.FiniteDuration

class StorageProvider extends Actor with ActorLogging {
    implicit val executionContext: ExecutionContextExecutor = context.dispatcher
    implicit val timeout: FiniteDuration = scala.concurrent.duration.Duration.fromNanos(2L)

    val driver: MongoDriver = new MongoDriver()
    var connection: MongoConnection = _
    var db: DefaultDB = _
    var twitterCollection: BSONCollection =_

    obtainConnection()

    def receive = {
        case _ => log.info("receiving message in StorageProvider")
    }

    override def postRestart(reason: Throwable): Unit = {
        reason match {
            case ce: ConnectionException =>
                //try to obtain a brand new connection
                obtainConnection()
        }
        super.postRestart(reason)
    }

    override def postStop(): Unit = {
        connection.askClose()
        driver.close()
    }

    private def obtainConnection(): Unit = {
        connection = driver.connection(List(hostname), options)
        db = connection(ConfigUtilities.dbName)
        twitterCollection = getCollection(ConfigUtilities.dbCollection)
    }

    private def getCollection(name: String): BSONCollection = {
        db.connection[BSONCollection](name)
    }
}

object StorageProvider {
    def props: Props = Props[StorageProvider]

    var hostname: String = ConfigUtilities.dbHostname
    var options: MongoConnectionOptions = if (ConfigUtilities.dbAuthDb.isEmpty) {
        MongoConnectionOptions.default.copy(
            sslEnabled = false,
            readPreference = ReadPreference.secondaryPreferred,
            failoverStrategy = FailoverStrategy.default,
            connectTimeoutMS = 5000
        )
    } else {
        MongoConnectionOptions.default.copy(
            sslEnabled = false,
            authenticationDatabase = Some(ConfigUtilities.dbAuthDb),
            credentials = Map(
                ConfigUtilities.dbAuthDb -> MongoConnectionOptions.Credential(ConfigUtilities.dbUsername,
                    Some(ConfigUtilities.dbPassword))
            ),
            readPreference = ReadPreference.secondaryPreferred,
            failoverStrategy = FailoverStrategy.default,
            connectTimeoutMS = 5000
        )
    }
}
