package com.example

import java.io.{File, FileOutputStream, FileInputStream}
import com.example.thrift_helper.{Writer, Reader}
import com.twitter.bijection.scrooge.BinaryScalaCodec
import com.twitter.chill._
import victorops.thrift.scala._

import scala.util.Success

case class Blue(name: String = "blue", hexVal: String = "AAF3A1") extends Color
case class Green(name: String = "green", hexVal: String = "0732A1") extends Color
case class Yellow(name: String = "yellow", hexVal: String = "CC1433") extends Color

object SerializerTest {

  val outputFileLocation = System.getProperty("user.home") + File.separator + "test.txt"

  val testObject = NastyCaseClass(
    firstName = "nick",
    lastName = "isaacs",
    hasMustache = true,
    None,
    FeetSize(10.5, 10),
    Seq("jeff", "andrew", "dan"),
    true
  )

  def main(args: Array[String]) {
    if (args.contains("read")) {
      testRead()
    } else if (args.contains("write")) {
      testWrite()
    } else testReadAndWrite()
  }
  
  def readDidPass(): Unit = println("Successfully read data back in")

  def readDidFail(): Unit = println("Failed to read data back in")

  def writeDidPass(): Unit = println("Successfully wrote out data")

  def writeDidFail(): Unit = println("Failed to write out data")

  def testRead(): Boolean = readInTestObjectWithThrift match {
    case Success(a@NastyCaseClass(
      "nick",
      "isaacs",
      true,
      None,
      FeetSize(10.5, 10f),
      friends: Seq[String],
      _
    )) =>
      println(a)
      readDidPass()
      true
    case x =>
      println(s"Read back in: $x")
      readDidFail()
      false
  }


  def testWrite(): Boolean = writeOutObjectWithThrift.map{ case _ =>
    writeDidPass()
  }.isSuccess
  

  def testReadAndWrite(): Unit = {
    if (testWrite) testRead
  }

  def writeOutObjectWithThrift = {
    val codec = BinaryScalaCodec(NastyCaseClass)
    Writer(new File(outputFileLocation), NastyCaseClass).write(testObject)
  }

  def readInTestObjectWithThrift = {
    Reader(new File(outputFileLocation), NastyCaseClass).read
  }

  def writeOutObjectWithKryo(value: Any): Unit = {
    rmOutputFile
    val output = testOutput
    kryo.writeObject(output, testObject)
    output.close()
  }

  def readInObjectWithKryo: Any = {
    val input = testInput
    val someObject = kryo.readObject(input, classOf[NastyCaseClass])
    input.close()
    someObject
  }

  def testInput: Input = new Input(testFileInputStream)

  def testOutput: Output = new Output(testFileOutputStream)

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
    (new ScalaKryoInstantiator).newKryo()
  }
}
