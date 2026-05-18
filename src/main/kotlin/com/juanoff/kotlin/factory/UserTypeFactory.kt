package com.juanoff.kotlin.factory

import com.juanoff.kotlin.types.UserType
import java.util.*

class UserTypeFactory {
    private val registry = mutableMapOf<String, UserType>()

    fun register(type: UserType) {
        registry[type.typeName()] = type
    }

    fun getTypeNameList(): List<String> = Collections.unmodifiableList(registry.keys.toList())

    fun getBuilderByName(name: String): UserType? = registry[name]
}
