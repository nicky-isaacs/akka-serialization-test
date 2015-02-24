package com.example

import java.io.{File, FileOutputStream, FileInputStream}

import com.esotericsoftware.kryo._
import com.esotericsoftware.kryo.io.{Output, Input}
import com.example.serializers.NastyCaseClassSerializer


object Color {

  def apply(name: String): Color = name match {
    case "blue" => Blue()
    case "green" => Green()
    case "yellow" => Yellow()
  }

}

trait Color {
  def name: String
  def hexVal: String
}
case class Blue(name: String = "blue", hexVal: String = "AAF3A1") extends Color
case class Green(name: String = "green", hexVal: String = "0732A1") extends Color
case class Yellow(name: String = "yellow", hexVal: String = "CC1433") extends Color

case class FeetSize(leftFoot: Float, rightFoot: Float)

object KryoTest {

  val outputFileLocation = System.getProperty("user.home") + File.separator + "test.txt"

  val testObject = NastyCaseClass(
    firstName = "nick",
    lastName = "isaacs",
    hasMustache = true,
    mustacheColor = Some(Blue("blue", "AAF3A1")),
    footSizes = FeetSize(10.5f, 10f)
  )

  def main(args: Array[String]) {
    if (args.contains("read")) {
      testRead
    } else if (args.contains("write")) {
      testWrite
    } else testReadAndWrite
  }
  
  def readDidPass(): Unit = println("Successfully read data back in")

  def readDidFail(): Unit = println("Failed to read data back in")

  def writeDidPass(): Unit = println("Successfully wrote out data")

  def writeDidFail(): Unit = println("Failed to write out data")

  def testRead(): Boolean = readInObjectWithKryo match {
    case NastyCaseClass(
      "nick",
      "isaacs",
      true,
      Some(Blue("blue", "AAF3A1")),
      FeetSize(10.5, 10f)
    ) =>
      readDidPass
      true
    case x =>
      println(s"Read back in: $x")
      readDidFail
      false
  }


  def testWrite(): Boolean = writeOutObjectWithKryo(testObject) match {
    case _ =>
      writeDidPass
      true
  }

  def testReadAndWrite(): Unit = {
    if (testWrite) testRead
  }

  def writeOutObjectWithKryo(value: Any): Unit = {
    rmOutputFile
    val output = testKryoOutput
    kryo.writeObject(output, testObject, new NastyCaseClassSerializer)
    output.close
  }

  def readInObjectWithKryo: Any = {
    val input = testKryoInput
    val someObject = kryo.readObject(input, classOf[NastyCaseClass], new NastyCaseClassSerializer)
    input.close
    someObject
  }

  def testKryoInput: Input = new Input(testFileInputStream)

  def testKryoOutput: Output = new Output(testFileOutputStream)

  def testFileInputStream: FileInputStream = {
    ensureOutputFilePresent
    new FileInputStream(outputFileLocation)
  }
  
  def testFileOutputStream: FileOutputStream = {
    ensureOutputFilePresent
    new FileOutputStream(outputFileLocation)
  }
  
  def rmOutputFile = new File(outputFileLocation).delete()
  
  def ensureOutputFilePresent: Boolean = new File(outputFileLocation).createNewFile
  
  def kryo: Kryo = {
    val k = new Kryo
    k.register(NastyCaseClass.getClass, new NastyCaseClassSerializer)
    k
  }
}
