package cat.dvmlls

import com.google.caliper.Param
import java.util.Random

class Benchmark extends SimpleScalaBenchmark {
  
  @Param(Array("10", "100", "1000", "10000"))
  val length: Int = 0

  @Param(Array("JU_HM", "JU_C_HM", "JU_TM", "SC_M_HM", "SC_M_LoM", "SC_M_LsM", "SC_M_LHM", "SC_I_HM", "SC_I_LoM", "SC_I_IM", "SC_I_TM"))
  val implementation:String = ""

  @Param(Array("sequential", "random"))
  val order:String = ""

  val r:Random = new Random()

  abstract class Tester[T,K](create: => Impl[T, K]) {
    def indexes:Seq[K]

    val empty:Impl[T, K] = create
    val full:Impl[T, K] = indexes.foldLeft(create) {
      case (existing, additional) => existing.put(additional, r.nextDouble()); existing
    }
    val full2:Impl[T, K] = indexes.foldLeft(create) {
      case (existing, additional) => existing.put(additional, r.nextDouble()); existing
    }

    def merge() = full2.merge(full.map)
    def insert() = indexes.map(k => empty.put(k, r.nextDouble()))
    def get() = indexes.map(full.get)
    def delete() = indexes.map(full.remove)
    def update() = indexes.map(k => full.put(k, r.nextDouble()))
  }

  var wrapper:Tester[_,_] = _

  override def setUp(): Unit = {
    val s = 0 until length

    trait Integers { lazy val indexes = if (order == "sequential") s else s.map(_ => r.nextInt()).distinct }
    trait Longs { lazy val indexes = if (order == "sequential") s.map(_.toLong) else s.map(_ => r.nextLong()).distinct }

    wrapper = implementation match {
      case "JU_HM" => new Tester({ new JU_HM {} }) with Integers
      case "JU_TM" => new Tester({ new JU_TM {} }) with Integers
      case "JU_C_HM" => new Tester({ new JU_C_HM {} }) with Integers
      case "SC_M_HM" => new Tester({ new SC_M_HM {} }) with Integers
      case "SC_M_LoM" => new Tester({ new SC_M_LoM {} }) with Longs
      case "SC_M_LsM" => new Tester({ new SC_M_LsM {} }) with Longs
      case "SC_M_LHM" => new Tester({ new SC_M_LHM {} }) with Longs
      case "SC_I_HM" => new Tester({ new SC_I_HM {} }) with Integers
      case "SC_I_LoM" => new Tester({ new SC_I_LoM {} }) with Longs
      case "SC_I_IM" => new Tester({ new SC_I_IM {} }) with Integers
      case "SC_I_TM" => new Tester({ new SC_I_TM {} }) with Integers
    }
  }

  def timeInsert(reps:Int) = repeat(reps) { wrapper.insert() }
  def timeGet(reps:Int) = repeat(reps) { wrapper.get() }
  def timeDelete(reps:Int) = repeat(reps) { wrapper.delete() }
  def timeUpdate(reps:Int) = repeat(reps) { wrapper.update() }
  def timeMerge(reps:Int) = repeat(reps) { wrapper.merge() }

  override def tearDown() { }
}