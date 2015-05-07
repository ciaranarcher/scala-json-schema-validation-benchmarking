package cat.dvmlls

import com.google.caliper.Param
import java.util.Random

class Benchmark extends SimpleScalaBenchmark {

  @Param(Array("sequential", "random"))
  val order:String = ""

  @Param(Array("100", "1000", "10000"))
  val length: Int = 0

  @Param(Array(
    "JU_HM", "JU_C_HM", "JU_TM",
    "SC_M_HM", "SC_M_LoM", "SC_M_LsM", "SC_M_LHM", "SC_M_OHM",
    "SC_I_HM", "SC_I_LoM", "SC_I_IM", "SC_I_TM"))
  val implementation:String = ""

  implicit val r:Random = new Random()

  var tester:Tester[_,_] = _

  def s = 0 until length

  def ints = if (order == "sequential") s else s.map(_ => r.nextInt()/2).distinct
  def longs = if (order == "sequential") s.map(_.toLong) else s.map(_ => r.nextLong()/2).distinct

  override def setUp(): Unit = {
    implicit lazy val intIndexes:Seq[Int] = ints
    implicit lazy val longIndexes:Seq[Long] = longs

    tester = implementation match {
      case "JU_HM" => new Tester(() => new JU_HM(length))
      case "JU_TM" => new Tester(() => new JU_TM(length))
      case "JU_C_HM" => new Tester(() => new JU_C_HM(length))
      case "SC_M_HM" => new Tester(() => new SC_M_HM(length))
      case "SC_M_OHM" => new Tester(() => new SC_M_OHM(length))
      case "SC_M_LoM" => new Tester(() => new SC_M_LoM(length))
      case "SC_M_LsM" => new Tester(() => new SC_M_LsM(length))
      case "SC_M_LHM" => new Tester(() => new SC_M_LHM(length))
      case "SC_I_HM" => new Tester(() => new SC_I_HM(length))
      case "SC_I_LoM" => new Tester(() => new SC_I_LoM(length))
      case "SC_I_IM" => new Tester(() => new SC_I_IM(length))
      case "SC_I_TM" => new Tester(() => new SC_I_TM(length))
    }
  }

  def timeInsert(reps:Int) = repeat(reps) { tester.insert() }
  def timeGet(reps:Int) = repeat(reps) { tester.get() }
  def timeDelete(reps:Int) = repeat(reps) { tester.delete() }
  def timeUpdate(reps:Int) = repeat(reps) { tester.update() }
  def timeMerge(reps:Int) = repeat(reps) { tester.merge() }

  override def tearDown(): Unit = { }
}