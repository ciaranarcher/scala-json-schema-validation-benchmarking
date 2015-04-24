package cat.dvmlls

import java.util
import scala.collection._

trait Implementation {
  def put(n:Int, d:Double):Int
  def remove(n:Int):Int
  def get(n:Int):Double
}

class JU_HM extends Implementation {
  val map:util.HashMap[Int,Double] = new util.HashMap[Int,Double]()

  override def put(n: Int, d:Double): Int = { map.put(n,d) ; n }
  override def remove(n: Int): Int = { map.remove(n) ; n }
  override def get(n:Int):Double = map.get(n)
}

class JU_C_HM extends Implementation {
  val map:util.concurrent.ConcurrentHashMap[Int,Double] = new util.concurrent.ConcurrentHashMap[Int,Double]()

  override def put(n: Int, d:Double): Int = { map.put(n,d) ; n }
  override def remove(n: Int): Int = { map.remove(n) ; n }
  override def get(n:Int):Double = map.get(n) 
}

class SC_M_HM extends Implementation {
  val map:mutable.HashMap[Int,Double] = new mutable.HashMap[Int,Double]()

  override def put(n: Int, d:Double): Int = { map.put(n,d) ; n }
  override def remove(n: Int): Int = { map.remove(n) ; n }
  override def get(n:Int):Double = map.get(n).get
}

class SC_M_LM extends Implementation {
  val map:mutable.LongMap[Double] = new mutable.LongMap[Double]()

  override def put(n: Int, d:Double): Int = { map.put(n,d) ; n }
  override def remove(n: Int): Int = { map.remove(n) ; n }
  override def get(n:Int):Double = map.get(n).get
}

class SC_I_HM extends Implementation {
  var map:immutable.HashMap[Int,Double] = new immutable.HashMap[Int,Double]()

  override def put(n: Int, d:Double): Int = { map += n -> d ; n }
  override def remove(n: Int): Int = { map -= n ; n }
  override def get(n:Int):Double = map.get(n).get
}

class SC_I_LM extends Implementation {
  var map:immutable.LongMap[Double] = immutable.LongMap[Double]()

  override def put(n: Int, d:Double): Int = { map += n.toLong -> d ; n }
  override def remove(n: Int): Int = { map -= n ; n }
  override def get(n:Int):Double = map.get(n).get
}