package cat.dvmlls

import com.google.caliper.Param
import java.util.Random

// a caliper benchmark is a class that extends com.google.caliper.Benchmark
// the SimpleScalaBenchmark trait does it and also adds some convenience functionality
class Benchmark extends SimpleScalaBenchmark {
  
  // to make your benchmark depend on one or more parameterized values, create fields with the name you want
  // the parameter to be known by, and add this annotation (see @Param javadocs for more details)
  // caliper will inject the respective value at runtime and make sure to run all combinations 
  @Param(Array("10", "100", "1000", "10000"))
  val length: Int = 0

  @Param(Array("JU_HM", "JU_C_HM", "SC_M_HM", "SC_I_HM", "SC_I_LM"))
  val implementation:String = ""
  var map:Implementation = null

  val r:Random = new Random()

  override def setUp() {
    implementation match {
      case "JU_HM" => map = new JU_HM()
      case "JU_C_HM" => map = new JU_C_HM()
      case "SC_M_HM" => map = new SC_M_HM()
      case "SC_M_LM" => map = new SC_M_LM()
      case "SC_I_HM" => map = new SC_I_HM()
      case "SC_I_LM" => map = new SC_I_LM()
    }
  }

  def timeSequentialInsert(reps:Int) = repeat(reps) {
    (0 until length).map(n => map.update(n, r.nextDouble()))
  }

  def timeRandomInsert(reps:Int) = repeat(reps) {
    (0 until length).map(n => map.update(r.nextInt(), r.nextDouble()))
  }

  def timeSequentialGet(reps:Int) = repeat(reps) {
    (0 until length).map(n => map.update(n, r.nextDouble()))
    (length until 0).map(map.get)
  }

  def timeRandomGet(reps:Int) = repeat(reps) {
    val indexes = (0 until length).map(n => map.update(r.nextInt(), r.nextDouble()))
    indexes.reverseMap(map.get)
  }

  def timeSequentialDelete(reps:Int) = repeat(reps) {
    (0 until length).map(n => map.update(n, r.nextDouble()))
    (length until 0).map(map.delete)
  }

  def timeRandomDelete(reps:Int) = repeat(reps) {
    val indexes = (0 until length).map(n => map.update(r.nextInt(), r.nextDouble()))
    indexes.reverseMap(map.delete)
  }

  def timeSequentialUpdate(reps:Int) = repeat(reps) {
    (0 until length).map(n => map.update(n, r.nextDouble()))
    (length until 0).map(n => map.update(n, r.nextDouble()))
  }

  def timeRandomUpdate(reps:Int) = repeat(reps) {
    val indexes = (0 until length).map(n => map.update(r.nextInt(), r.nextDouble()))
    indexes.reverseMap(n => map.update(n, r.nextDouble()))
  }

  override def tearDown() {
    // clean up after yourself if required
  }
}