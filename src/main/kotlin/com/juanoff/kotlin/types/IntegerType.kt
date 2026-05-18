package com.juanoff.kotlin.types

import java.io.InputStreamReader

class IntegerType : UserType {
    override fun typeName(): String = "Integer"
    override fun create(): Any = 0
    override fun clone(obj: Any): Any = obj

    override fun readValue(reader: InputStreamReader): Any {
        return reader.buffered().use { reader ->
            parseValue(reader.readLine())
        }
    }

    override fun parseValue(ss: String): Any {
        require(ss.isNotBlank()) { "Enter value" }
        return ss.trim().toIntOrNull() ?: throw IllegalArgumentException("Invalid integer format: '${ss.trim()}'")
    }

    override fun getTypeComparator(): Comparator = Comparator { o1, o2 ->
        when (o1) {
            is Int if o2 is Int -> o1.compareTo(o2)
            else -> 0
        }
    }

    override fun serialize(obj: Any): String = obj.toString()
    override fun deserialize(s: String): Any = parseValue(s)
}
