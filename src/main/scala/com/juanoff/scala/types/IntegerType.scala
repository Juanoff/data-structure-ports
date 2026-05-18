package com.juanoff.scala.types

import java.io.{BufferedReader, InputStreamReader}

class IntegerType extends UserType {
  override def typeName(): String = "Integer"

  override def create(): Any = 0

  override def clone(obj: Any): Any = {
    obj match {
      case obj: Integer => obj
      case _ => throw new IllegalArgumentException(s"Expected ${typeName()}")
    }
  }

  override def readValue(in: InputStreamReader): Any = {
    val reader = new BufferedReader(in)
    val line = reader.readLine()
    parseValue(line)
  }

  override def parseValue(ss: String): Any = {
    if (ss == null || ss.trim.isEmpty) {
      throw new IllegalArgumentException("Enter value")
    }

    try {
      ss.trim.toInt
    } catch {
      case _: NumberFormatException => throw new IllegalArgumentException(s"Invalid integer format: '${ss.trim}'")
    }
  }

  override def getTypeComparator: Comparator = {
    (o1: Any, o2: Any) => {
      (o1, o2) match {
        case (i1: java.lang.Integer, i2: java.lang.Integer) => i1.compareTo(i2)
        case _ => 0
      }
    }
  }

  override def serialize(obj: Any): String = {
    obj match {
      case i: java.lang.Integer => i.toString
      case _ => ""
    }
  }

  override def deserialize(s: String): Any = parseValue(s)
}
