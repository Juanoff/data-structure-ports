package com.juanoff.kotlin.types

data class MatrixElement(
    val x: Int,
    val y: Int,
    val value: Double
) {
    override fun toString(): String = String.format("(%d, %d) = %.2f", x, y, value)
}