package com.softwaremill.bootzooka

import com.example.account.api.{Account, AccountCan, AccountRef, AccountServiceFs2Grpc, FetchAccountResponse, FetchAccountsRequest}
import io.grpc.{Metadata, ServerBuilder, ServerServiceDefinition}
import monix.eval.Task
import cats.implicits._
import monix.execution.Scheduler.Implicits.global

class Fs2GrpcDemo {
  import org.lyranthe.fs2_grpc.java_runtime.implicits._

  val helloService: ServerServiceDefinition = AccountServiceFs2Grpc.bindService(new AccountServiceFs2Grpc[Task, Metadata] {
    override def canonicalizeRef(request: AccountRef, ctx: Metadata): Task[AccountCan] = ???

    override def getAccount(request: AccountRef, ctx: Metadata): Task[Account] = ???

    override def fetchAccounts(request: FetchAccountsRequest, ctx: Metadata): Task[FetchAccountResponse] = ???
  })

  ServerBuilder
    .forPort(9999)
    .addService(helloService)
    .stream[Task] // or for any F: Sync
    .evalMap(server => Task(server.start())) // start server
    .evalMap(_ => Task.never) // server now running
}
