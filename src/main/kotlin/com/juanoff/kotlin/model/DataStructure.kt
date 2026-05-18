package com.juanoff.kotlin.model

import com.juanoff.kotlin.types.Comparator

interface DataStructure {
    fun add(value: Any)

    fun get(index: Int): Any

    fun insert(index: Int, value: Any)

    fun remove(index: Int)

    fun sort(comp: Comparator)

    fun forEach(action: DoWith)

    fun firstThat(predicate: TestIt): Any?

    fun balance(): DataStructure

    fun size(): Int

    fun clear()
}