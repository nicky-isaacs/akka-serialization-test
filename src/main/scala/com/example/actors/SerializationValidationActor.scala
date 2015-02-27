package com.example.actors

import akka.actor.Actor
import victorops.thrift.scala.{NastyCaseClass, Color}

class SerializationValidationActor extends Actor{
  
  override def receive: Receive = {
    case color: Color =>
      handleReceiveColor(color)
      sender ! color
    case n: NastyCaseClass =>
      handleReceiveNastyCaseClass(n)
      sender ! n
  }
  
  def handleReceiveColor(c: Color) = {
    println(s"I received the color ${c.name}\n")
  }
  
  def handleReceiveNastyCaseClass(n: NastyCaseClass) = {
    println(s"I received the color: $n\n")
  }
  
}