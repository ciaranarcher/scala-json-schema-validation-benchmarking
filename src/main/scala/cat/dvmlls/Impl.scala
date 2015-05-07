package cat.dvmlls

import java.util
import scala.collection._

trait Impl[T, K] {
  def map:T
  def put(n:K, d:Double):K
  def remove(n:K):K
  def get(n:K):Double
  def merge(m:T):Int
  def create(capacity:Int):T
  def capacity:Int
}

trait MutableImpl[T, K] extends Impl[T, K] {
  lazy val map = create(capacity)
}

trait JU[T <: util.Map[Int,Double]] extends MutableImpl[T, Int] {
  override def put(n: Int, d:Double): Int = { map.put(n,d) ; n }
  override def remove(n: Int): Int = { map.remove(n) ; n }
  override def get(n:Int):Double = map.get(n)
  override def merge(m:T):Int = { map.putAll(m); map.size }
}

class JU_HM (val capacity:Int) extends JU [util.HashMap[Int,Double]] { override def create(capacity:Int) = new util.HashMap[Int,Double](capacity) }
class JU_TM (val capacity:Int)extends JU [util.TreeMap[Int,Double]] { override def create(capacity:Int) = new util.TreeMap[Int,Double]() }
class JU_C_HM (val capacity:Int)extends JU [util.concurrent.ConcurrentHashMap[Int,Double]] { override def create(capacity:Int) = new util.concurrent.ConcurrentHashMap[Int,Double](capacity) }

trait SC_M[T <: mutable.Map[K,Double], K] extends MutableImpl[T, K] {
  override def put(n:K, d:Double):K = { map.put(n,d) ; n }
  override def remove(n:K):K = { map.remove(n) ; n }
  override def get(n:K):Double = map.get(n).get
  override def merge(m:T):Int = { m.foreach { case (k,v) => map.put(k,v) }; map.size }
}

class SC_M_HM (val capacity:Int) extends SC_M [mutable.HashMap[Int,Double], Int] { override def create(capacity:Int) = new mutable.HashMap[Int,Double]() }
class SC_M_OHM (val capacity:Int) extends SC_M [mutable.OpenHashMap[Int,Double], Int] { override def create(capacity:Int) = new mutable.OpenHashMap[Int,Double](capacity) }
class SC_M_LHM (val capacity:Int) extends SC_M [mutable.LinkedHashMap[Int,Double], Int] { override def create(capacity:Int) = new mutable.LinkedHashMap[Int,Double]() }
class SC_M_LsM (val capacity:Int) extends SC_M [mutable.ListMap[Int,Double], Int] { override def create(capacity:Int) = new mutable.ListMap[Int,Double]() }
class SC_M_LoM (val capacity:Int) extends SC_M [mutable.LongMap[Double], Long] { override def create(capacity:Int) = new mutable.LongMap[Double](capacity) }

trait SC_I[T <: immutable.Map[K,Double], K] extends Impl[T, K] {
  var map = create(capacity)
  override def get(n:K):Double = map.get(n).get
}

class SC_I_TM (val capacity:Int) extends SC_I [immutable.TreeMap[Int,Double], Int] {
  override def create(capacity:Int) = immutable.TreeMap[Int,Double]()
  override def put(n:Int, d:Double):Int = { map += n -> d ; n }
  override def remove(n:Int):Int = { map -= n ; n }
  override def merge(m:immutable.TreeMap[Int,Double]):Int = { map ++= m; map.size }
}

class SC_I_HM (val capacity:Int) extends SC_I [immutable.HashMap[Int,Double], Int] {
  override def create(capacity:Int) = immutable.HashMap[Int,Double]()
  override def put(n:Int, d:Double):Int = { map += n -> d ; n }
  override def remove(n:Int):Int = { map -= n ; n }
  override def merge(m:immutable.HashMap[Int,Double]):Int = { map ++= m; map.size }
}

class SC_I_IM (val capacity:Int) extends SC_I [immutable.IntMap[Double], Int] {
  override def create(capacity:Int) = immutable.IntMap[Double]()
  override def put(n:Int, d:Double):Int = { map += n -> d ; n }
  override def remove(n:Int):Int = { map -= n ; n }
  override def merge(m:immutable.IntMap[Double]):Int = { map ++= m; map.size }
}

class SC_I_LoM (val capacity:Int) extends SC_I [immutable.LongMap[Double], Long] {
  override def create(capacity:Int) = immutable.LongMap[Double]()
  override def put(n: Long, d:Double):Long = { map += n -> d ; n }
  override def remove(n:Long):Long = { map -= n ; n }
  override def merge(m:immutable.LongMap[Double]):Int = { map ++= m; map.size }
}
