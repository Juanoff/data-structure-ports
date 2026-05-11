package com.juanoff.scala.model

//import com.juanoff.model.{DataStructure, DoWith, TestIt}
//import com.juanoff.types.Comparator

import com.juanoff.scala.types.Comparator

import scala.annotation.tailrec

class MultiList(val chunkSize: Int = 10) extends DataStructure {
  private var data: Vector[Vector[Any]] = Vector()
  private var totalSize: Int = 0

  override def add(value: Any): Unit = {
    if (data.isEmpty || data.last.size >= chunkSize) {
      data = data :+ Vector(value)
    } else {
      val lastChunk = data.last
      data = data.updated(data.size - 1, lastChunk :+ value)
    }
    totalSize += 1
  }

  override def get(index: Int): Any = {
    checkIndex(index)

    @tailrec
    def loop(chunks: Seq[Vector[Any]], currentIdx: Int): Any = {
      val head = chunks.head
      if (index < currentIdx + head.size) {
        head(index - currentIdx)
      } else {
        loop(chunks.tail, currentIdx + head.size)
      }
    }

    loop(data, 0)
  }

  override def insert(index: Int, value: Any): Unit = {
    checkIndexForInsert(index)

    if (index == totalSize) {
      add(value)
    } else {
      @tailrec
      def loop(i: Int, currentIdx: Int): Unit = {
        val chunk = data(i)
        if (index <= currentIdx + chunk.size) {
          val updatedChunk = chunk.patch(index - currentIdx, Seq(value), 0)
          data = data.updated(i, updatedChunk)
          totalSize += 1
          rebalanceIfNeeded()
        } else {
          loop(i + 1, currentIdx + chunk.size)
        }
      }

      loop(0, 0)
    }
  }

  override def remove(index: Int): Unit = {
    checkIndex(index)

    @tailrec
    def loop(i: Int, currentIdx: Int): Unit = {
      val chunk = data(i)
      if (index < currentIdx + chunk.size) {
        val updatedChunk = chunk.patch(index - currentIdx, Nil, 1)
        data = data.updated(i, updatedChunk)
        totalSize -= 1
        rebalanceIfNeeded()
      } else {
        loop(i + 1, currentIdx + chunk.size)
      }
    }

    loop(0, 0)
  }

  override def size(): Int = totalSize

  override def forEach(action: DoWith): Unit = data.flatten.foreach(item => action.doWith(item))

  override def firstThat(test: TestIt): Any = data.flatten.find(item => test.testIt(item)).orNull

  override def sort(comparator: Comparator): Unit = {
    val flatList: List[Any] = data.flatten.toList
    val sortedList = mergeSortFunctional(flatList, (o1: Any, o2: Any) => comparator.compare(o1, o2))
    rebuild(sortedList)
  }

  override def balance(): DataStructure = {
    val newList = new MultiList(chunkSize)
    data.flatten.foreach(newList.add)
    newList
  }

  override def clear(): Unit = while(size() > 0) remove(0)

  private def mergeSortFunctional(list: List[Any], comp: (Any, Any) => Int): List[Any] = {
    if (list.length <= 1) {
      list
    } else {
      val (left, right) = list.splitAt(list.length / 2)
      mergeLists(
        mergeSortFunctional(left, comp),
        mergeSortFunctional(right, comp),
        comp
      )
    }
  }

  private def mergeLists(left: List[Any], right: List[Any], comp: (Any, Any) => Int): List[Any] = {
    (left, right) match {
      case (Nil, _) => right
      case (_, Nil) => left
      case (lHead :: lTail, rHead :: rTail) =>
        if (comp(lHead, rHead) <= 0)
          lHead :: mergeLists(lTail, right, comp)
        else
          rHead :: mergeLists(left, rTail, comp)
    }
  }

  private def sortImperative(comp: (Any, Any) => Int): Unit = {
    val arr: Array[Any] = data.flatten.toArray
    val n = arr.length
    if (n <= 1) return

    val temp = new Array[Any](n)
    var width = 1

    while (width < n) {
      var i = 0
      while (i < n) {
        val left = i
        val mid = math.min(i + width, n)
        val right = math.min(i + 2 * width, n)

        var l = left
        var r = mid
        var k = left

        while (l < mid && r < right) {
          if (comp(arr(l), arr(r)) <= 0) {
            temp(k) = arr(l)
            l += 1
          } else {
            temp(k) = arr(r)
            r += 1
          }
          k += 1
        }

        System.arraycopy(arr, l, temp, k, mid - l)
        System.arraycopy(arr, r, temp, k + (mid - l), right - r)
        k = right

        var c = left
        while (c < right) {
          arr(c) = temp(c)
          c += 1
        }

        i += 2 * width
      }
      width *= 2
    }

    rebuild(arr.toSeq)
  }

  private def rebuild(flat: Seq[Any]): Unit = {
    data = Vector()
    totalSize = 0
    flat.foreach(add)
  }

  private def rebalanceIfNeeded(): Unit = {
    if (data.exists(_.isEmpty)) {
      rebuild(data.flatten.filter(_ != null))
    }
  }

  private def checkIndex(index: Int): Unit = {
    if (index < 0 || index >= totalSize) throw new IndexOutOfBoundsException(s"Index out of range: $index")
  }

  private def checkIndexForInsert(index: Int): Unit = {
    if (index < 0 || index > totalSize) throw new IndexOutOfBoundsException(s"Index out of range: $index")
  }
}
