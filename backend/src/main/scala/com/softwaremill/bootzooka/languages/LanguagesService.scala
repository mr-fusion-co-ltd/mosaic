package com.softwaremill.bootzooka.languages

import java.time.Clock

import com.softwaremill.bootzooka.email.EmailData
import com.softwaremill.bootzooka.email.sender.EmailSender
import com.softwaremill.bootzooka.infrastructure.Doobie._
import com.softwaremill.bootzooka.util.IdGenerator
import monix.eval.Task

class LanguagesService(
    languagesModel: LanguagesModel,
    idGenerator: IdGenerator,
    clock: Clock,
    xa: Transactor[Task],
    emailSender: EmailSender
) {

  // Future: eagerly evaluating operation. Future[String]
  // Task/IO/ZIO: description of a lazily evaluated operation. Task[String]

  def vote(name: String): Task[Unit] = {
    val dbOperations: ConnectionIO[Unit] = languagesModel.find(name).flatMap {
      case None       => languagesModel.insert(Language(idGenerator.nextId(), name, 1, clock.instant()))
      case Some(lang) => languagesModel.increaseVotes(lang.id)
    }

    val dbTx: Task[Unit] = dbOperations.transact(xa)

    val sendEmail: Task[Unit] = emailSender(EmailData("admin@example.org", "New vote!", s"Vote for: $name"))

    sendEmail >> dbTx
  }

  def getVotes(name: String): Task[Int] = {
    languagesModel.find(name).transact(xa).map { optionalLanguage =>
      optionalLanguage.map(_.votes).getOrElse(0)
    }
  }
}
