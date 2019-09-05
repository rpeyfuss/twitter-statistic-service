package com.reginapeyfuss.services.message

case class LogMessage(requestId: String, userId: String, message: String, createTime: Long)
