
package com.reginapeyfuss.services.honeycomb

import com.reginapeyfuss.services.message.LogMessage
import io.honeycomb.libhoney.{HoneyClient, LibHoney}
import javax.inject.{Inject, Singleton}
import play.api.Configuration

case class HoneycombWriter(honeycombClient: HoneyCombClient, logMessage: LogMessage)


@Singleton
class HoneyCombClient @Inject()(config: Configuration) {
    val honeycombKey: String = config.getOptional[String]("honeycomb.writeKey").getOrElse("")
    val dataset: String = config.getOptional[String]("honeycomb.dataset").getOrElse("default")

    val honeyCombClient: HoneyClient = LibHoney.create(
        LibHoney.options
            .setWriteKey(honeycombKey)
            .setDataset(dataset)
            .setSampleRate(2)
            .build()
    )

}
