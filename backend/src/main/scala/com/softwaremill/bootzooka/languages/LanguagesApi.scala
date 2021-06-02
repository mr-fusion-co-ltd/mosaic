package com.softwaremill.bootzooka.languages

import cats.data.NonEmptyList
import com.softwaremill.bootzooka.Fail
import com.softwaremill.bootzooka.http.Http
import com.softwaremill.bootzooka.infrastructure.Json._
import com.softwaremill.bootzooka.languages.LanguagesApi.{GetVotes_OUT, Vote_IN}
import com.softwaremill.bootzooka.util.ServerEndpoints
import monix.eval.Task

class LanguagesApi(http: Http, languagesService: LanguagesService) {
  import http._

  private val voteEndpoint = baseEndpoint.post
    .in("languages" / "vote")
    .in(jsonBody[Vote_IN])
    .serverLogic { data =>
      (if (data.name == data.name.toLowerCase) {
         languagesService.vote(data.name)
       } else {
         Task.raiseError(Fail.IncorrectInput("Name must be lower case"))
       }).toOut
    }

  private val getVotesEndpoint = baseEndpoint.get
    .in("languages" / "vote")
    .in(query[String]("name"))
    .out(jsonBody[GetVotes_OUT])
    .serverLogic { name =>
      languagesService.getVotes(name).map(votes => GetVotes_OUT(votes)).toOut
    }

  val endpoints: ServerEndpoints = NonEmptyList
    .of(voteEndpoint, getVotesEndpoint)
    .map(_.tag("languages"))
}

object LanguagesApi {
  case class Vote_IN(name: String)
  case class GetVotes_OUT(votes: Int)
}
