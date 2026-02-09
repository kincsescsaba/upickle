package upickle.core

import upickle.core.compat.*

import scala.collection.mutable
import java.{util as ju}


/** mutable.Map[K, V] implementation wrapping a combination of java.util.HashMap[K, V] and java.util.LinkedList[K]
 * which doesn't allow null as key. Enables prepending while still keeps key-value pairs sorted just as LinkedHashMap does
 */
class DequeueOrderedMap[K, V] private(underlyingMap: ju.HashMap[K, V], underlyingOrder: ju.LinkedList[K])
  extends mutable.Map[K, V] with DequeueOrderedMapCompat {
  private def _putLast(key: K, value: V): V = {
    if (key == null)
      throw new NullPointerException("null keys are not allowed")
    underlyingOrder.add(key)
    underlyingMap.put(key, value)
  }
  private def _putFirst(key: K, value: V): V = {
    if (key == null)
      throw new NullPointerException("null keys are not allowed")
    underlyingOrder.addFirst(key)
    underlyingMap.put(key, value)
  }
  def addOne(elem: (K, V)): this.type = {
    _putLast(elem._1, elem._2)
    this
  }
  override def size: Int = underlyingOrder.size
  def iterator: Iterator[(K, V)] = {
    new Iterator[(K, V)] {
      val it = underlyingOrder.iterator()
      def hasNext: Boolean = it.hasNext()
      def next(): (K, V) = {
        val entryKey = it.next()
        (entryKey, underlyingMap.get(entryKey))
      }
    }
  }
  def get(key: K): Option[V] = Option(underlyingMap.get(key))
  def subtractOne(elem: K): this.type = {
    underlyingOrder.remove(elem)
    underlyingMap.remove(elem)
    this
  }
  def putLast(key: K, value: V): Option[V] = {
    Option(_putLast(key, value))
  }
  def putFirst(key: K, value: V): Option[V] = {
    Option(_putFirst(key, value))
  }
  override def result(): DequeueOrderedMap[K, V] = this
}
object DequeueOrderedMap {

  def apply[K, V](): DequeueOrderedMap[K, V] =
    new DequeueOrderedMap[K, V](new ju.HashMap[K, V], new ju.LinkedList[K])

  def apply[K, V](items: IterableOnce[(K, V)]): DequeueOrderedMap[K, V] = {
    val map = DequeueOrderedMap[K, V]()
    toIterator(items).foreach { case (key, value) =>
      map._putLast(key, value)
    }
    map
  }
  implicit def factory[K, V]: Factory[(K, V), DequeueOrderedMap[K, V]] =
    DequeueOrderedMapCompat.factory[K, V]
}
