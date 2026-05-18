package com.juanoff.scala.types

import scala.collection.mutable.HashMap as MHashMap
import scala.jdk.CollectionConverters.*

class SparseMatrix {
  private val data = new MHashMap[String, Double]()

  def set(x: Int, y: Int, value: Double): Unit = {
    val k = key(x, y)
    if (value == 0.0) {
      data.remove(k)
    } else {
      data.put(k, value)
    }
  }

  def get(x: Int, y: Int): Double = data.getOrElse(key(x, y), 0.0)

  def size(): Int = data.size

  def add(other: SparseMatrix): SparseMatrix = {
    val result = this.copy()
    other.getEntries.forEach(e => {
      val current = result.get(e.x, e.y)
      result.set(e.x, e.y, current + e.value)
    })
    result
  }

  def copy(): SparseMatrix = {
    val m = new SparseMatrix()
    m.data ++= this.data
    m
  }

  def sum(): Double = data.values.sum

  def getEntries: java.util.List[MatrixElement] = {
    data.map { case (key, value) =>
      val parts = key.split(",")
      val x = parts(0).toInt
      val y = parts(1).toInt
      new MatrixElement(x, y, value)
    }.toList.asJava
  }

  override def toString: String = {
    if (data.isEmpty)
      "Empty Matrix"
    else {
      data.map { case (key, value) =>
        val parts = key.split(",")
        val x = parts(0)
        val y = parts(1)
        s"($x,$y)=$value"
      }.mkString("[", "; ", "]")
    }
  }

  def key(x: Int, y: Int): String = s"$x,$y"
}
