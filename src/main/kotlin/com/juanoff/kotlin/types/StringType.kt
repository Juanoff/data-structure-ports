package com.juanoff.kotlin.types

import java.io.InputStreamReader

class StringType : UserType {
    override fun typeName(): String = "String"
    override fun create(): Any = ""
    override fun clone(obj: Any): Any = obj

    override fun readValue(reader: InputStreamReader): Any {
        return reader.buffered().use { reader ->
            parseValue(reader.readLine())
        }
    }

    override fun parseValue(ss: String): Any {
        require(ss.isNotBlank()) { "Enter value" }
        return ss.trim()
    }

    override fun getTypeComparator(): Comparator = Comparator { o1, o2 ->
        when {
            o1 is String && o2 is String -> o1.compareTo(o2)
            else -> 0
        }
    }

    override fun serialize(obj: Any): String = obj.toString()
    override fun deserialize(s: String): Any = parseValue(s)
}