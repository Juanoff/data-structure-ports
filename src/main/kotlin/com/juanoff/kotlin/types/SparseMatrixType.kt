package com.juanoff.kotlin.types

import java.io.InputStreamReader

class SparseMatrixType : UserType {
    override fun typeName(): String = "SparseMatrix"
    override fun create(): Any = SparseMatrix()

    override fun clone(obj: Any): Any {
        require(obj is SparseMatrix) { "Expected SparseMatrix, got ${obj::class.simpleName}" }
        return obj.copy()
    }

    override fun readValue(reader: InputStreamReader): Any {
        return reader.buffered().use { reader ->
            parseValue(reader.readLine())
        }
    }

    override fun parseValue(ss: String): Any {
        require(ss.isNotBlank()) { "Enter matrix parameters" }

        val matrix = SparseMatrix()
        val parts = ss.split(";")

        parts.forEach { part ->
            val trimmed = part.trim()
            if (trimmed.isNotEmpty()) {
                val vals = trimmed.split(",")
                require(vals.size == 3) { "Invalid matrix format in part: $trimmed" }

                try {
                    val x = vals[0].trim().toInt()
                    val y = vals[1].trim().toInt()
                    val v = vals[2].trim().toDouble()
                    matrix[x, y] = v
                } catch (e: NumberFormatException) {
                    throw IllegalArgumentException("Invalid number format in part: $trimmed", e)
                }
            }
        }

        return matrix
    }

    override fun getTypeComparator(): Comparator = Comparator { o1, o2 ->
        when {
            o1 is SparseMatrix && o2 is SparseMatrix -> {
                val sizeCmp = o1.size().compareTo(o2.size())
                if (sizeCmp != 0) sizeCmp else o1.sum().compareTo(o2.sum())
            }

            else -> 0
        }
    }

    override fun serialize(obj: Any): String {
        require(obj is SparseMatrix) { "Expected SparseMatrix" }
        return obj.getEntries().joinToString(separator = ";") { "${it.x},${it.y},${it.value}" }
    }

    override fun deserialize(s: String): Any = parseValue(s)
}