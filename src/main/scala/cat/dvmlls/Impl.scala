package cat.dvmlls

import java.util
import scala.collection._

trait Impl[T, K] {
  def map:T
  def put(n:K, d:Double):K
  def remove(n:K):K
  def get(n:K):Double
  def merge(m:T):Int
  def create:() => T
}

trait MutableImpl[T, K] extends Impl[T, K] {
  lazy val map = create()
}

trait JU[T <: util.Map[Int,Double]] extends MutableImpl[T, Int] {
  override def put(n: Int, d:Double): Int = { map.put(n,d) ; n }
  override def remove(n: Int): Int = { map.remove(n) ; n }
  override def get(n:Int):Double = map.get(n)
  override def merge(m:T):Int = { map.putAll(m); map.size }
}

trait JU_HM extends JU [util.HashMap[Int,Double]] { override def create = () => new util.HashMap[Int,Double]() }
trait JU_TM extends JU [util.TreeMap[Int,Double]] { override def create = () => new util.TreeMap[Int,Double]() }
trait JU_C_HM extends JU [util.concurrent.ConcurrentHashMap[Int,Double]] { override def create = () => new util.concurrent.ConcurrentHashMap[Int,Double]() }

trait SC_M[T <: mutable.Map[K,Double], K] extends MutableImpl[T, K] {
  override def put(n:K, d:Double):K = { map.put(n,d) ; n }
  override def remove(n:K):K = { map.remove(n) ; n }
  override def get(n:K):Double = map.get(n).get
  override def merge(m:T):Int = { m.foreach { case (k,v) => map.put(k,v) }; map.size }
}

trait SC_M_HM extends SC_M [mutable.HashMap[Int,Double], Int] { override def create = () => new mutable.HashMap[Int,Double]() }
trait SC_M_LHM extends SC_M [mutable.LinkedHashMap[Int,Double], Int] { override def create = () => new mutable.LinkedHashMap[Int,Double]() }
trait SC_M_LsM extends SC_M [mutable.ListMap[Int,Double], Int] { override def create = () => new mutable.ListMap[Int,Double]() }
trait SC_M_LoM extends SC_M [mutable.LongMap[Double], Long] { override def create = () => new mutable.LongMap[Double]() }

trait SC_I[T <: immutable.Map[K,Double], K] extends Impl[T, K] {
  var map = create()
  override def get(n:K):Double = map.get(n).get
}

trait SC_I_TM extends SC_I [immutable.TreeMap[Int,Double], Int] {
  override def create = () => immutable.TreeMap[Int,Double]()
  override def put(n:Int, d:Double):Int = { map += n -> d ; n }
  override def remove(n:Int):Int = { map -= n ; n }
  override def merge(m:immutable.TreeMap[Int,Double]):Int = { map ++= m; map.size }
}

trait SC_I_HM extends SC_I [immutable.HashMap[Int,Double], Int] {
  override def create = () => immutable.HashMap[Int,Double]()
  override def put(n:Int, d:Double):Int = { map += n -> d ; n }
  override def remove(n:Int):Int = { map -= n ; n }
  override def merge(m:immutable.HashMap[Int,Double]):Int = { map ++= m; map.size }
}

trait SC_I_IM extends SC_I [immutable.IntMap[Double], Int] {
  override def create = () => immutable.IntMap[Double]()
  override def put(n:Int, d:Double):Int = { map += n -> d ; n }
  override def remove(n:Int):Int = { map -= n ; n }
  override def merge(m:immutable.IntMap[Double]):Int = { map ++= m; map.size }
}

trait SC_I_LoM extends SC_I [immutable.LongMap[Double], Long] {
  override def create = () => immutable.LongMap[Double]()
  override def put(n: Long, d:Double):Long = { map += n -> d ; n }
  override def remove(n:Long):Long = { map -= n ; n }
  override def merge(m:immutable.LongMap[Double]):Int = { map ++= m; map.size }
}
