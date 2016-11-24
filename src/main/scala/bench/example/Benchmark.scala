package bench.example

import com.google.caliper.Param
import java.util.Random

class Sequences(isRandom:Boolean, length:Int)(implicit r:Random) {
  def s = 0 until length
  def ints = if (!isRandom) s else s.map(_ => r.nextInt()/2).distinct
  def longs = if (!isRandom) s.map(_.toLong) else s.map(_ => r.nextLong()/2).distinct
}

// a caliper benchmark is a class that extends com.google.caliper.Benchmark
// the SimpleScalaBenchmark trait does it and also adds some convenience functionality
class Benchmark extends SimpleScalaBenchmark {

  // to make your benchmark depend on one or more parameterized values, create fields with the name you want
  // the parameter to be known by, and add this annotation (see @Param javadocs for more details)
  // caliper will inject the respective value at runtime and make sure to run all combinations
  @Param(Array("10", "100", "1000", "10000"))
  val length: Int = 0

  var array: Array[Int] = _

  override def setUp() {
    // set up all your benchmark data here
    array = new Array(length)
  }

  // the actual code you'd like to test needs to live in one or more methods
  // whose names begin with 'time' and which accept a single 'reps: Int' parameter
  // the body of the method simply executes the code we wish to measure, 'reps' times
  // you can use the 'repeat' method from the SimpleScalaBenchmark trait to repeat with relatively low overhead
  // however, if your code snippet is very fast you might want to implement the reps loop directly with 'while'
  def timeForeach(reps: Int) = repeat(reps) {
    //////////////////// CODE SNIPPET ONE ////////////////////

    var result = 0
    array.foreach {
      result += _
    }
    result // always have your snippet return a value that cannot easily be "optimized away"

    //////////////////////////////////////////////////////////
  }

  override def tearDown(): Unit = { }
}