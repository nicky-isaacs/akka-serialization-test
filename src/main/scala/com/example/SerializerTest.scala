package com.example

import java.io.File
import akka.actor.{PoisonPill, ActorRef, Props, ActorSystem}
import akka.persistence.Recover
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import com.example.actors.{PersistentSerializingActor, SerializationValidationActor}
import com.typesafe.config.{ConfigFactory, Config}
import victorops.thrift.scala.{Color, NastyCaseClass, FeetSize}
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.util.Timeout
import akka.pattern.ask
import scala.util.{Failure, Success}

object SerializerTest {
  
  // Timeout for actor asks
  implicit val timeout = Timeout(5 seconds)

  val outputFileLocation = System.getProperty("user.home") + File.separator + "test.txt"

  val blue = Color("blue",  "AAF3A1")
  
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
    val validationActors = (1 until 5).map(_ => buildValidationActor(serializationActorSystem))
    val persistentActors = Seq(buildPersistentActor(serializationActorSystem))
    
    for {
      _ <- runValidationActors(validationActors)
      _ <- runPersistentActors(persistentActors)
    } yield cleanup()
  }
  
  def runValidationActors(actors: Seq[ActorRef]): Future[Any] = {
    val futures = actors.map{ ref => ref ? testObject }

    val eventualSeq: Future[Seq[Any]] = Future.sequence(futures)
    eventualSeq.onComplete{
      case Success(a) =>
        a.map(handleSuccessfulActorResponse)

      case Failure(t) =>
        handleFailedActorResponse(t)
    }
    eventualSeq
  }
  
  def runPersistentActors(actors: Seq[ActorRef]): Future[Seq[Boolean]] = {
    
    // Send lots of tastey messages to the actors and snapshot them
    val futures = for (a <- actors) yield {
      val objResponse   = a ! testObject
      val colorResponse = a ! blue
      val snapResponse  = a ! "snap"
      
      // Kill the actor, and create a new one with a recover() signal
      a ! PoisonPill
      val newActor = buildPersistentActor(serializationActorSystem)
      newActor ! Recover()
      
      // Ask for the state, validate we get back what we asked to be snapshot last time
      (newActor ? "state").collect{
        case (Some(
        NastyCaseClass(
        "nick",
        "isaacs",
        true,
        Some(blue),
        FeetSize(10.5, 10),
        Seq("jeff", "andrew", "dan"),
        true
        )
        ),
        Some(
          Color("blue", "AAF3A1")
        )) =>
          println(s"Succeeded in persisting")
          true

        case _ =>
          println(s"Succeeded in persisting")
          false
      }
    }
    
    Future.sequence(futures)
  }

  def buildValidationActor(system: ActorSystem) = system.actorOf(Props[SerializationValidationActor])

  def buildPersistentActor(system: ActorSystem) = system.actorOf(Props[PersistentSerializingActor])
  
  def handleSuccessfulActorResponse(a: Any) = println(s"Successful actor response: $a\n")
  
  def handleFailedActorResponse(a: Any) = println(s"Failed actor response: $a\n")
}
