package com.example

import java.io.File
import akka.actor.{Props, ActorSystem}
import com.example.actors.SerializationValidationActor
import com.typesafe.config.{ConfigFactory, Config}
import victorops.thrift.scala.{Color, NastyCaseClass, FeetSize}
import scala.collection.immutable.IndexedSeq
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.util.Timeout
import akka.pattern.ask

import scala.util.{Failure, Success}

case class Blue(name: String = "blue", hexVal: String = "AAF3A1") extends Color
case class Green(name: String = "green", hexVal: String = "0732A1") extends Color
case class Yellow(name: String = "yellow", hexVal: String = "CC1433") extends Color

object SerializerTest {
  
  // Timeout for actor asks
  implicit val timeout = Timeout(5 seconds)

  val outputFileLocation = System.getProperty("user.home") + File.separator + "test.txt"

  val blue: Blue = Blue()
  
  val testObject = NastyCaseClass(
    firstName = "nick",
    lastName = "isaacs",
    hasMustache = true,
    Some(blue),
    FeetSize(10.5, 10),
    Seq("jeff", "andrew", "dan"),
    true
  )

  private val config: Config = ConfigFactory.defaultReference()

  val serializationActorSystem = ActorSystem("SerializationSystem", config)

  def main(args: Array[String]): Unit = {
    val cleanup = () => serializationActorSystem.terminate()
    val actors = (1 until 5).map(_ => buildValidationActor(serializationActorSystem))
    
    val futures = actors.map{ ref => ref ? testObject }

    val eventualSeq: Future[IndexedSeq[Any]] = Future.sequence(futures)
    eventualSeq.onComplete{
      case Success(a) =>
        a.map(handleSuccessfulActorResponse)

      case Failure(t) =>
        handleFailedActorResponse(t)
    }
    eventualSeq.flatMap( _ => cleanup() )
  }

  def buildValidationActor(system: ActorSystem) = system.actorOf(Props[SerializationValidationActor])
  
  def handleSuccessfulActorResponse(a: Any) = println(s"Successful actor response: $a\n")
  
  def handleFailedActorResponse(a: Any) = println(s"Failed actor response: $a\n")
  
}
