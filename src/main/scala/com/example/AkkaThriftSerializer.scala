package com.example

import com.twitter.bijection.scrooge.BinaryScalaCodec
import akka.serialization.Serializer
import victorops.thrift.scala.{Color, NastyCaseClass}


class AkkaThriftSerializer extends Serializer {

  def includeManifest: Boolean = true
  def identifier = 34543

  def toBinary(obj: AnyRef): Array[Byte] = obj match {
    case m: NastyCaseClass => BinaryScalaCodec(NastyCaseClass)(m)
    case m: Color => BinaryScalaCodec(Color)(m)
    case m => throw new IllegalArgumentException("Cannot serialize object: " + m)
  }

  def fromBinary(bytes: Array[Byte], clazz: Option[Class[_]]): AnyRef = clazz match {
    case Some(c) if c == classOf[NastyCaseClass.Immutable] => BinaryScalaCodec(NastyCaseClass).invert(bytes).get
    case Some(c) => throw new IllegalArgumentException("Cannot deserialize class: " + c.getCanonicalName)
    case None => throw new IllegalArgumentException("No class found in EventSerializer when deserializing array: " + bytes.mkString(""))
  }
}