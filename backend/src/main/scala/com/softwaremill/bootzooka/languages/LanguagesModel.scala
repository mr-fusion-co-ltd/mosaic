package com.softwaremill.bootzooka.languages

import java.time.Instant

import com.softwaremill.bootzooka.infrastructure.Doobie._
import com.softwaremill.bootzooka.util.Id
import com.softwaremill.tagging.@@

import cats.syntax.all._

class LanguagesModel {

  def insert(l: Language): ConnectionIO[Unit] = {
    sql"""INSERT INTO languages(id, name, created_on, votes)
         |VALUES (${l.id}, ${l.name}, ${l.createdOn}, ${l.votes})""".stripMargin.update.run.void
  }

  def increaseVotes(id: Id @@ Language): ConnectionIO[Unit] = {
    sql"""UPDATE languages SET votes = votes+1 WHERE id=$id""".update.run.void
  }

  def find(name: String): ConnectionIO[Option[Language]] = {
    sql"""SELECT id, name, votes, created_on FROM languages WHERE name=$name"""
      .query[Language]
      .option
  }
}

case class Language(id: Id @@ Language, name: String, votes: Int, createdOn: Instant)
