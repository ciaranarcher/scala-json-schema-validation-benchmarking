package cat.dvmlls

import java.util.Random

trait HasIndexes[K] { def indexes:Seq[K] }

abstract class Tester[T,K](create:() => Impl[T, K])(implicit r:Random) extends HasIndexes[K] {
  val empty:Impl[T, K] = create()
  val full:Impl[T, K] = {
    val result = create()
    indexes.foreach(k => result.put(k, r.nextDouble()))
    result
  }
  val full2:Impl[T, K] = {
    val result = create()
    indexes.foreach(k => result.put(k, r.nextDouble()))
    result
  }

  def merge() = full2.merge(full.map)
  def insert() = indexes.map(k => empty.put(k, r.nextDouble()))
  def get() = indexes.map(full.get)
  def delete() = indexes.map(full.remove)
  def update() = indexes.map(k => full.put(k, r.nextDouble()))
}