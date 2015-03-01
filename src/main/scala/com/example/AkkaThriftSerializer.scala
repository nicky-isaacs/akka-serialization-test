package com.example

import com.twitter.bijection.scrooge.BinaryScalaCodec
import akka.serialization.Serializer
import com.twitter.scrooge.{ThriftStructCodec, ThriftStruct}
import victorops.thrift.scala.{FeetSize, Color, NastyCaseClass}


class AkkaThriftSerializer extends Serializer {
  def includeManifest: Boolean = true
  def identifier = 34543

  def toBinary(obj: AnyRef): Array[Byte] = {
    println("Doing a custom serialization")
    obj match {
      case n: NastyCaseClass => BinaryScalaCodec(NastyCaseClass)(n)
      case c: Color          => BinaryScalaCodec(Color)(c)
      case f: FeetSize       => BinaryScalaCodec(FeetSize)(f)
      case m => throw new IllegalArgumentException("Cannot serialize object: " + m)
    }
  }

  def fromBinary(bytes: Array[Byte], clazz: Option[Class[_]]): AnyRef = {
    println("Doing a custom de-serialization")
    clazz match {
      case Some(c) if c == classOf[NastyCaseClass.Immutable] => BinaryScalaCodec(NastyCaseClass).invert(bytes).get
      case Some(c) if c == classOf[Color.Immutable]          => BinaryScalaCodec(Color).invert(bytes).get
      case Some(c) if c == classOf[FeetSize.Immutable]       => BinaryScalaCodec(FeetSize).invert(bytes).get

      case Some(c)                                           =>
        throw new IllegalArgumentException("Cannot deserialize class: " + c.getCanonicalName)

      case None                                              =>
        throw new IllegalArgumentException("No class found in EventSerializer when deserializing array: " + bytes.mkString(""))
    }
  }
}