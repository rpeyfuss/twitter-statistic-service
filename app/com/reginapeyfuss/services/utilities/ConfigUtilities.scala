package com.reginapeyfuss.services.utilities

import com.typesafe.config.{Config, ConfigFactory}
import play.api.Logger

object ConfigUtilities {

    private def getOptionalConfig(path: String, default: String): String = {
        if(config.hasPath(path))
            config.getString(path)
        else
            default
    }

    private val config: Config = ConfigFactory.load()
    private val log: Logger = Logger(this.getClass)

    val dbName: String = getOptionalConfig("db.name", "twitterDB")
    val dbCollection: String = getOptionalConfig("db.collection", "twitterCollection")
    val dbHostname: String = getOptionalConfig("db.hostname", "localhost")
    val dbAuthDb: String = getOptionalConfig("db.authDb", "")
    val dbUsername: String = getOptionalConfig("db.username", "")
    val dbPassword: String = getOptionalConfig("db.password", "")

}
