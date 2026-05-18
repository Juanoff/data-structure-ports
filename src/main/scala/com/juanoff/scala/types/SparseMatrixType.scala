package com.juanoff.scala.types

import java.io.{BufferedReader, InputStreamReader}
import scala.jdk.CollectionConverters.*

class SparseMatrixType extends UserType {
  override def typeName(): String = "SparseMatrix"

  override def create(): Any = new SparseMatrix()

  override def clone(obj: Any): Any = {
    obj match {
      case matrix: SparseMatrix => matrix.copy()
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
      throw new IllegalArgumentException("Enter matrix parameters")
    }

    val matrix = new SparseMatrix()
    val parts = ss.split(";")
    parts.foreach { p =>
      val trimmed = p.trim
      if (trimmed.nonEmpty) {
        val vals = trimmed.split(",")
        if (vals.length != 3) {
          throw new IllegalArgumentException(s"Invalid matrix format in part: $trimmed")
        }

        try {
          val x = vals(0).trim.toInt
          val y = vals(1).trim.toInt
          val v = vals(2).trim.toDouble
          matrix.set(x, y, v)
        } catch {
          case _: NumberFormatException =>
            throw new IllegalArgumentException(s"Invalid number format in part: $trimmed")
        }
      }
    }

    matrix
  }

  override def getTypeComparator: Comparator = {
    (o1: Any, o2: Any) => {
      (o1, o2) match {
        case (m1: SparseMatrix, m2: SparseMatrix) =>
          val sizeCmp = Integer.compare(m1.size(), m2.size())
          if (sizeCmp != 0) sizeCmp else m1.sum().compareTo(m2.sum())
        case _ => 0
      }
    }
  }

  override def serialize(obj: Any): String = {
    obj match {
      case matrix: SparseMatrix =>
        val entries = matrix.getEntries
        entries.asScala.map { e => s"${e.x},${e.y},${e.value}" }.mkString(";")
      case _ => ""
    }
  }

  override def deserialize(s: String): Any = parseValue(s)
}
