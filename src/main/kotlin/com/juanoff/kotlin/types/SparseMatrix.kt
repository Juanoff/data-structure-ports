package com.juanoff.kotlin.types

class SparseMatrix {
    private val data = mutableMapOf<String, Double>()

    operator fun get(x: Int, y: Int): Double = data["$x,$y"] ?: 0.0

    operator fun set(x: Int, y: Int, value: Double) {
        val key = "$x,$y"
        if (value == 0.0) {
            data.remove(key)
        } else {
            data[key] = value
        }
    }

    fun copy(): SparseMatrix {
        return SparseMatrix().apply {
            data.putAll(this@SparseMatrix.data)
        }
    }

    fun add(other: SparseMatrix): SparseMatrix {
        val result = this.copy()
        other.data.forEach { (key, value) ->
            val (x, y) = key.split(",").map { it.toInt() }
            val current = result[x, y]
            result[x, y] = current + value
        }
        return result
    }

    operator fun plus(other: SparseMatrix): SparseMatrix {
        return add(other)
    }

    fun size(): Int = data.size

    fun sum(): Double = data.values.sum()

    fun getEntries(): List<MatrixElement> {
        return data.map { (key, value) ->
            val (x, y) = key.split(",").map { it.toInt() }
            MatrixElement(x, y, value)
        }
    }

    override fun toString(): String {
        if (data.isEmpty()) return "Empty Matrix"
        return data.map { (key, value) ->
            val (x, y) = key.split(",").map { it.toInt() }
            "($x,$y)=$value"
        }.joinToString(prefix = "[", separator = "; ", postfix = "]")
    }
}