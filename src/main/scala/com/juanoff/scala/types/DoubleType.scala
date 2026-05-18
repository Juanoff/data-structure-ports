package com.juanoff.scala.types

import java.io.{BufferedReader, InputStreamReader}

class DoubleType extends UserType {
  override def typeName(): String = "Double"

  override def create(): Any = 0.0

  override def clone(obj: Any): Any = {
    obj match {
      case obj: Double => obj
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
      val value = ss.trim.toDouble
      if (value.isNaN || value.isInfinite) {
        throw new IllegalArgumentException("Value cannot be NaN or Infinity")
      }
      value
    } catch {
      case _: NumberFormatException => throw new IllegalArgumentException(s"Invalid double format: '${ss.trim}'")
    }
  }

  override def getTypeComparator: Comparator = {
    (o1: Any, o2: Any) => {
      (o1, o2) match {
        case (d1: java.lang.Double, d2: java.lang.Double) => d1.compareTo(d2)
        case _ => 0
      }
    }
  }

  override def serialize(obj: Any): String = {
    obj match {
      case d: java.lang.Double => d.toString
      case _ => ""
    }
  }

  override def deserialize(s: String): Any = parseValue(s)
}