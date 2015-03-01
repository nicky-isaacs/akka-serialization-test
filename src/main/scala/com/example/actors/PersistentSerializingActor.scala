package com.example.actors

import akka.persistence.{SnapshotOffer, PersistentActor}
import victorops.thrift.scala.{Color, NastyCaseClass}

class PersistentSerializingActor extends PersistentActor {

  // Constant ID means we should always recover from the same snapshot
  override def persistenceId = "sample-id-1"
  
  // Initialize some state
  var LastSeenNastyCaseClass: Option[NastyCaseClass] = None
  var LastSeenColor: Option[Color]                   = None
  
  override def receiveRecover: Receive = {
    case n: NastyCaseClass => LastSeenNastyCaseClass = Some(n)
    case c: Color          => LastSeenColor = Some(c)
    
    case SnapshotOffer(_, (nastyCaseClass: Option[NastyCaseClass], color: Option[Color])) =>
      LastSeenNastyCaseClass = nastyCaseClass
      LastSeenColor          = color
      println(s"I recovered from a snapshot!")
  }

  override def receiveCommand: Receive = {
    case n: NastyCaseClass =>
      handleNastyCaseClass(n)
      sender ! n

    case c: Color =>
      handleColor(c)
      sender ! c

    case a: String if a.startsWith("snap")  => saveSnapshot((LastSeenNastyCaseClass, LastSeenNastyCaseClass))
    case a: String if a.startsWith("state") => (LastSeenNastyCaseClass, LastSeenNastyCaseClass)
  }

  def handleNastyCaseClass(n: NastyCaseClass) = persist(n)( a => LastSeenNastyCaseClass = Some(a) )
  
  def handleColor(c: Color) = persist(c)( a => LastSeenColor = Some(a) )
}
