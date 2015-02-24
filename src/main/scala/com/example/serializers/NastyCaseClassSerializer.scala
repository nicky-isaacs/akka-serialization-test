package com.example.serializers

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import com.example.{FeetSize, Color, NastyCaseClass}

import scala.util.Try

class NastyCaseClassSerializer extends Serializer[NastyCaseClass] {
  override def write(kryo: Kryo, output: Output, `object`: NastyCaseClass): Unit = {
    output.writeString(`object`.firstName)
    output.writeString(`object`.lastName)
    output.writeBoolean(`object`.hasMustache)
    output.writeString(`object`.mustacheColor.fold("")(_.name))
    output.writeFloat(`object`.footSizes.leftFoot)
    output.writeFloat(`object`.footSizes.rightFoot)
  }

  override def read(kryo: Kryo, input: Input, `type`: Class[NastyCaseClass]): NastyCaseClass = {
    NastyCaseClass(
      input.readString,
      input.readString,
      input.readBoolean,
      Try{ Color(input.readString) }.toOption,
      FeetSize(
        input.readFloat,
        input.readFloat
      )
    )
  }
}

