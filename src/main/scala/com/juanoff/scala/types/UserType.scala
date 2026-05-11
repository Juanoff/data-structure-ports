package com.juanoff.scala.types

import java.io.InputStreamReader

trait UserType {
  def typeName(): String

  def create(): Any

  def clone(obj: Any): Any

  def readValue(in: InputStreamReader): Any

  def parseValue(ss: String): Any

  def getTypeComparator: Comparator

  def serialize(obj: Any): String

  def deserialize(s: String): Any
}
