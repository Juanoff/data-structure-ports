package com.juanoff.kotlin.model

import com.juanoff.kotlin.types.Comparator

class MultiList(private val chunkSize: Int = 10) : DataStructure {
    private var chunks: MutableList<MutableList<Any>> = mutableListOf()
    private var totalSize: Int = 0

    override fun size(): Int = totalSize

    override fun clear() {
        chunks.clear()
        totalSize = 0
    }

    override fun add(value: Any) {
        if (chunks.isEmpty() || chunks.last().size >= chunkSize) {
            chunks.add(mutableListOf())
        }
        chunks.last().add(value)
        totalSize++
    }

    override fun get(index: Int): Any {
        require(index in 0..totalSize) { "Index out of bounds: $index" }
        val (chunkIdx, elemIdx) = index.divRem(chunkSize)
        return chunks[chunkIdx][elemIdx]
    }

    override fun insert(index: Int, value: Any) {
        require(index in 0..totalSize) { "Index out of bounds: $index" }
        val (chunkIdx, elemIdx) = index.divRem(chunkSize)
        chunks[chunkIdx].add(elemIdx, value)
        totalSize++
        if (chunks[chunkIdx].size > chunkSize) balance()
    }

    override fun remove(index: Int) {
        require(index in 0..totalSize) { "Index out of bounds: $index" }
        val (chunkIdx, elemIdx) = index.divRem(chunkSize)
        chunks[chunkIdx].removeAt(elemIdx)
        totalSize--
        if (chunks[chunkIdx].isEmpty()) chunks.removeAt(chunkIdx)
    }

    override fun forEach(action: DoWith) {
        for (chunk in chunks) {
            for (item in chunk) action.doWith(item)
        }
    }

    override fun firstThat(predicate: TestIt): Any? {
        for (chunk in chunks) {
            for (item in chunk) {
                if (predicate.testIt(item)) return item
            }
        }
        return null
    }

    override fun sort(comp: Comparator) {
        mergeSortFunctional(comp)
    }

    fun sortImperative(comp: Comparator) {
        val arr = chunks.flatten().toTypedArray()
        val n = arr.size
        if (n <= 1) return

        val temp = arrayOfNulls<Any>(n)
        var width = 1
        while (width < n) {
            var i = 0
            while (i < n) {
                val left = i
                val mid = minOf(i + width, n)
                val right = minOf(i + 2 * width, n)

                var l = left
                var r = mid
                var k = left
                while (l < mid && r < right) {
                    if (comp.compare(arr[l], arr[r]) <= 0) temp[k++] = arr[l++]
                    else temp[k++] = arr[r++]
                }
                while (l < mid) temp[k++] = arr[l++]
                while (r < right) temp[k++] = arr[r++]

                var c = left
                while (c < right) arr[c++] = temp[c]!!
                i += 2 * width
            }
            width *= 2
        }
        rebuildFromFlat(arr.toList())
    }

    override fun balance(): DataStructure {
        rebuildFromFlat(chunks.flatten())
        return this
    }

    private fun rebuildFromFlat(flat: List<Any>) {
        chunks.clear()
        chunks.addAll(flat.chunked(chunkSize).map { it.toMutableList() })
        totalSize = flat.size
    }

    private fun mergeSortFunctional(comp: Comparator) {
        val sorted = mergeRec(chunks.flatten(), comp)
        rebuildFromFlat(sorted)
    }

    private fun mergeRec(list: List<Any>, comp: Comparator): List<Any> {
        if (list.size <= 1) return list
        val mid = list.size / 2
        val left = mergeRec(list.subList(0, mid), comp)
        val right = mergeRec(list.subList(mid, list.size), comp)
        return merge(left, right, comp)
    }

    private fun merge(left: List<Any>, right: List<Any>, comp: Comparator): List<Any> = when {
        left.isEmpty() -> right
        right.isEmpty() -> left
        comp.compare(left.first(), right.first()) <= 0 -> listOf(left.first()) + merge(left.drop(1), right, comp)
        else -> listOf(right.first()) + merge(left, right.drop(1), comp)
    }

    private fun Int.divRem(divisor: Int): Pair<Int, Int> = this / divisor to this % divisor
}
