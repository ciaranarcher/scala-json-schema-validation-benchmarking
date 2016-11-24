import cat.dvmlls._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import java.util.Random

import bench.example.{JU_HM, Sequences, Tester}
import org.scalacheck._
import org.scalatest.prop.Checkers
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop
import org.scalacheck.Prop._

@RunWith(classOf[JUnitRunner])
class Tests extends FunSuite {

  test("map specification") {
    MapSpec.check
  }

}

object MapSpec extends Properties("Maps") {
  lazy val length = Gen.oneOf(100, 1000, 10000)
  lazy val isRandom = Gen.oneOf(true, false)
  implicit val rand = new Random()

  lazy val testers = for (
    l <- length ;
    r <- isRandom ;
    s = new Sequences(r, l)
  ) yield {
      implicit lazy val ints = s.ints
      implicit lazy val longs = s.longs

      new Tester(() => new JU_HM(l))
    }

  property("merge") = forAll(testers) { tester =>
    tester.merge() > tester.full.size
  }

}
