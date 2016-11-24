package bench.example

import java.util
import com.carrotsearch.hppc.{IntDoubleMap, IntDoubleScatterMap, IntDoubleHashMap}
import gnu.trove.map.hash.{TIntDoubleHashMap, THashMap}

import scala.collection._

class Impl_PlayValidation(capacity:Int) {
  def validate = { println(s"do validation ${capacity}") }
}

class Impl_OtherValidation(capacity:Int) {
  def validate = { println(s"do validation ${capacity}") }
}
