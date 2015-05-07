import cat.dvmlls._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import java.util.Random

@RunWith(classOf[JUnitRunner])
class Tests extends FunSuite with ShouldMatchers {
  implicit val r = new Random()
  val n = 100
  def s = 0 until n

  def ints = s.map(_ => r.nextInt()).distinct

  trait Integers extends HasIndexes [Int] { override lazy val indexes:Seq[Int] = ints }
  trait Capacity { def capacity:Int = n }

  test("scala mutable get") {
    val tester = new Tester(() => new SC_M_HM with Capacity {}) with Integers
    tester.get()
  }

  test("scala immutable get") {
    val tester = new Tester(() => new SC_I_HM with Capacity {}) with Integers
    tester.get()
  }

  test("java mutable get") {
    val tester = new Tester(() => new JU_HM with Capacity {}) with Integers
    tester.get()
  }
}
