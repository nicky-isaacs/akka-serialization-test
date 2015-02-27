package com.example.thrift_helper

import java.io.{FileOutputStream, File}
import com.twitter.bijection.scrooge.BinaryScalaCodec
import com.twitter.scrooge.{ThriftStructCodec, ThriftStruct}
import scala.util.Try

object Writer {
  
  def apply[T <: ThriftStruct](file: File, struct: ThriftStructCodec[T]) = new Writer(file, struct)
  
}

class Writer[T <: ThriftStruct](file: File, struct: ThriftStructCodec[T]) {

  def write(t: T) = {
    val arr = BinaryScalaCodec[T](struct).apply(t)
    Try {
      val out = new FileOutputStream(file)
      out.write(arr)
      out.close()
    }
  }
  
}
