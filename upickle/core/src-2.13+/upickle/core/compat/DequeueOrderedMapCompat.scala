package upickle.core.compat

import upickle.core.DequeueOrderedMap
import upickle.core.compat.Factory

import scala.collection.mutable

trait DequeueOrderedMapCompat[K, V]
object DequeueOrderedMapCompat {
  def factory[K, V]: Factory[(K, V), DequeueOrderedMap[K, V]] =
    new Factory[(K, V), DequeueOrderedMap[K, V]] {
      def fromSpecific(it: IterableOnce[(K, V)]): DequeueOrderedMap[K, V] =
        DequeueOrderedMap(it)

      def newBuilder: mutable.Builder[(K, V), DequeueOrderedMap[K, V]] =
        new mutable.Builder[(K, V), DequeueOrderedMap[K, V]] {
          private val map = DequeueOrderedMap[K, V]()

          def addOne(elem: (K, V)): this.type = {
            map.addOne(elem)
            this
          }
          def clear(): Unit = map.clear()
          def result(): DequeueOrderedMap[K, V] = map
        }
    }
}
