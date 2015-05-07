import cat.dvmlls._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import java.util.Random
import org.scalacheck._

@RunWith(classOf[JUnitRunner])
class Tests extends FunSuite with ShouldMatchers {
  implicit val r = new Random()
  val n = 100

  def ints = (0 until n).map(_ => r.nextInt()).distinct
  implicit lazy val indexes = ints

  test("scala mutable get") {
    val tester = new Tester(() => new SC_M_HM())
    tester.get()
  }

  test("scala immutable get") {
    val tester = new Tester(() => new SC_I_HM())
    tester.get()
  }

  test("java mutable get") {
    val tester = new Tester(() => new JU_HM(n))
    tester.get()
  }
}

object MapSpec extends Properties("Maps") {
  val length = Gen.oneOf(100, 1000, 10000)

}
