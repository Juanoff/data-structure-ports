package com.juanoff.kotlin.types

import java.io.InputStreamReader

class DoubleType : UserType {
    override fun typeName(): String = "Double"
    override fun create(): Any = 0.0
    override fun clone(obj: Any): Any = obj

    override fun readValue(reader: InputStreamReader): Any {
        return reader.buffered().use { reader ->
            parseValue(reader.readLine())
        }
    }

    override fun parseValue(ss: String): Any {
        require(ss.isNotBlank()) { "Enter value" }
        val value =
            ss.trim().toDoubleOrNull() ?: throw IllegalArgumentException("Invalid double format: '${ss.trim()}'")

        require(!value.isNaN() && !value.isInfinite()) {
            "Value cannot be NaN or Infinity"
        }
        return value
    }

    override fun getTypeComparator(): Comparator = Comparator { o1, o2 ->
        when {
            o1 is Double && o2 is Double -> o1.compareTo(o2)
            else -> 0
        }
    }

    override fun serialize(obj: Any): String = obj.toString()
    override fun deserialize(s: String): Any = parseValue(s)
}