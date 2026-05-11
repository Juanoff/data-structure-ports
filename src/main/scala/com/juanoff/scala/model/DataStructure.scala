package com.juanoff.scala.model

import com.juanoff.scala.types.Comparator

trait DataStructure {
  def add(value: Any): Unit

  def get(index: Int): Any

  def insert(index: Int, value: Any): Unit

  def remove(index: Int): Unit

  def size(): Int
  
  def forEach(action: DoWith): Unit

  def firstThat(predicate: TestIt): Any

  def sort(comp: Comparator): Unit

  def balance(): DataStructure

  def clear(): Unit
}
