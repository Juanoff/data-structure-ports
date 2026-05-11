package com.juanoff.scala.types

//import com.juanoff.types.{Comparator, UserType}

import java.io.{BufferedReader, InputStreamReader}

class StringType extends UserType {
  override def typeName(): String = "String"

  override def create(): Any = ""

  override def clone(obj: Any): Any = {
    obj match {
      case obj: String => obj
      case _ => throw new IllegalArgumentException(s"Expected ${typeName()}")
    }
  }

  override def readValue(in: InputStreamReader): Any = {
    val reader = new BufferedReader(in)
    reader.readLine()
  }

  override def parseValue(ss: String): Any = {
    if (ss == null || ss.trim.isEmpty) {
      throw new IllegalArgumentException("Enter value")
    }
    ss.trim
  }

  override def getTypeComparator: Comparator = {
    (o1: Any, o2: Any) => o1.asInstanceOf[String].compareTo(o2.asInstanceOf[String])
  }

  override def serialize(obj: Any): String = obj.asInstanceOf[String]

  override def deserialize(s: String): Any = s
}
