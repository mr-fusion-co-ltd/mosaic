package com.softwaremill.bootzooka.languages

import com.softwaremill.bootzooka.email.sender.EmailSender
import com.softwaremill.bootzooka.http.Http
import com.softwaremill.bootzooka.infrastructure.Doobie._
import com.softwaremill.bootzooka.util.BaseModule
import monix.eval.Task

trait LanguagesModule extends BaseModule {
  lazy val languagesModel = new LanguagesModel
  lazy val languagesService = new LanguagesService(languagesModel, idGenerator, clock, xa, emailSender)
  lazy val languagesApi = new LanguagesApi(http, languagesService)

  def xa: Transactor[Task]
  def http: Http
  def emailSender: EmailSender
}
