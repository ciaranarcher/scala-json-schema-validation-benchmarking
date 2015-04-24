package cat.dvmlls

import com.google.caliper.Param
import java.util.Random

// a caliper benchmark is a class that extends com.google.caliper.Benchmark
// the SimpleScalaBenchmark trait does it and also adds some convenience functionality
class Benchmark extends SimpleScalaBenchmark {
  
  @Param(Array("100", "1000", "10000"))
  val length: Int = 0

  @Param(Array("JU_HM", "JU_C_HM", "SC_M_HM", "SC_M_LM", "SC_I_HM", "SC_I_LM"))
  val implementation:String = ""

  var empty:Implementation = null
  var fullSeq:Implementation = null
  var fullRand:Implementation = null

  var indexesSeq:Seq[Int] = null
  var indexesLong:Seq[Int] = null
  var indexesRand:Seq[Int] = null

  val r:Random = new Random()

  private def createMap:Implementation = implementation match {
    case "JU_HM" => new JU_HM()
    case "JU_C_HM" => new JU_C_HM()
    case "SC_M_HM" => new SC_M_HM()
    case "SC_M_LM" => new SC_M_LM()
    case "SC_I_HM" => new SC_I_HM()
    case "SC_I_LM" => new SC_I_LM()
  }

  override def setUp(): Unit = {
    empty = createMap

    fullSeq = createMap
    indexesSeq = 0 until length
    indexesSeq.map(n => fullSeq.put(n, r.nextDouble()))

    fullRand = createMap
    indexesRand = indexesSeq
      .map(n => fullRand.put(r.nextInt(), r.nextDouble()))
      .reverse.distinct
  }

  def timeSequentialInsert(reps:Int) = repeat(reps) { indexesSeq.map(n => empty.put(n, r.nextDouble())) }

  def timeRandomInsert(reps:Int) = repeat(reps) { indexesRand.map(n => empty.put(r.nextInt(), r.nextDouble())) }

  def timeSequentialGet(reps:Int) = repeat(reps) { indexesSeq.map(fullSeq.get) }

  def timeRandomGet(reps:Int) = repeat(reps) { indexesRand.map(fullRand.get) }

  def timeSequentialDelete(reps:Int) = repeat(reps) { indexesSeq.map(fullSeq.remove) }

  def timeRandomDelete(reps:Int) = repeat(reps) { indexesRand.map(fullRand.remove) }

  def timeSequentialUpdate(reps:Int) = repeat(reps) { indexesSeq.map(n => fullSeq.put(n, r.nextDouble())) }

  def timeRandomUpdate(reps:Int) = repeat(reps) { indexesRand.map(n => fullRand.put(n, r.nextDouble())) }

  override def tearDown() { }
}