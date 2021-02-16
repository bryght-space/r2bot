package com.bryghts

package object r2bot {

  case class ReportableError(msg: String) extends Exception(msg)
                                             with sbt.FeedbackProvidedException

  implicit class LoggerExtension(val logger: sbt.util.Logger) extends AnyVal {
    def reportFailure(msg: String): Nothing = {
      logger.error(msg)
      throw ReportableError(msg)
    }
  }

}
