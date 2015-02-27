package com.example.thrift_helper

import java.io._
import java.nio.file.{Paths, Files}
import com.twitter.bijection.scrooge.BinaryScalaCodec
import com.twitter.scrooge.{ThriftStruct, ThriftStructCodec}

object Reader {

  def apply[T <: ThriftStruct](file: File, struct: ThriftStructCodec[T]) = new Reader(file, struct)
  
}

class Reader[T <: ThriftStruct](file: File, struct: ThriftStructCodec[T]) {

  def read = {
    val bytes = Files.readAllBytes( Paths.get(file.getAbsolutePath) )
    BinaryScalaCodec(struct).invert(bytes)
  }
  
}
