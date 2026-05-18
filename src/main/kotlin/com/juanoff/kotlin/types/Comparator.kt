package com.juanoff.kotlin.types

fun interface Comparator {
    fun compare(o1: Any, o2: Any): Int
}