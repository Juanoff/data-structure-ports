package com.juanoff.scala.types

case class MatrixElement(x: Int, y: Int, value: Double) {
  override def toString: String = String.format("(%d, %d) = %.2f", x, y, value)
}
