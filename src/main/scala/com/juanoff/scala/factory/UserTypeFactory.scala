package com.juanoff.scala.factory

import com.juanoff.scala.types.UserType

import scala.jdk.CollectionConverters.*

class UserTypeFactory {
  private val types = scala.collection.mutable.Map[String, UserType]()

  def register(typeProto: UserType): Unit = {
    types.put(typeProto.typeName(), typeProto)
  }

  def getTypeNameList: java.util.List[String] = {
    types.keys.toList.asJava
  }

  def getBuilderByName(name: String): UserType = {
    types.get(name).orNull
  }
}
