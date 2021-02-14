package com.bryghts

package object r2bot {

  case class ReportableError(msg: String) extends Exception(msg)
                                             with sbt.FeedbackProvidedException

  def reportFailure(msg: String): Nothing =
    throw ReportableError(msg)

}
