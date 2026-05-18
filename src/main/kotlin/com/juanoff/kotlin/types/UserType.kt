package com.juanoff.kotlin.types

import java.io.InputStreamReader

interface UserType {
    fun typeName(): String

    fun create(): Any

    fun clone(obj: Any): Any

    fun readValue(reader: InputStreamReader): Any

    fun parseValue(ss: String): Any

    fun getTypeComparator(): Comparator

    fun serialize(obj: Any): String

    fun deserialize(s: String): Any
}