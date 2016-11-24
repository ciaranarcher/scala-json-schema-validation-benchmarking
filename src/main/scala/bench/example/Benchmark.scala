package bench.example

import com.google.caliper.Param
import java.util.Random
import java.io.File

import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import play.api.libs.json._

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


  // set up all your benchmark data here
  val array = new Array(length)

  /*
   * Read the schema and create a validator
   */
  val schemaPath = {
    new File(".", "resources/page_view.schema.json").getCanonicalPath
  }

  val jsonSchema = {
    val document = scala.io.Source.fromInputStream(getClass.getResourceAsStream("page_view.schema.json"))
    val source = try document.mkString finally document.close()
    Json.parse(source)
  }

  log("schema read successfully")

  val schema = Json.fromJson[SchemaType](jsonSchema).get
  val validator = new SchemaValidator

  /*
   * Read the sample JSON document
   */
  val documentJson = {
    val document = scala.io.Source.fromInputStream(getClass.getResourceAsStream("page_view.sample.json"))
    val documentData = try document.mkString finally document.close()
    Json.parse(documentData)
  }

  def log(str: String) = { println(s">> BENCH >> ${str}") }

  // the actual code you'd like to test needs to live in one or more methods
  // whose names begin with 'time' and which accept a single 'reps: Int' parameter
  // the body of the method simply executes the code we wish to measure, 'reps' times
  // you can use the 'repeat' method from the SimpleScalaBenchmark trait to repeat with relatively low overhead
  // however, if your code snippet is very fast you might want to implement the reps loop directly with 'while'
  def timeForValidate(reps: Int) = repeat(reps) {
    //////////////////// CODE SNIPPET ONE ////////////////////

    val result = validator.validate(schema, documentJson)
    result match {
      case _: JsError => println(s"\n\nDocument is invalid: ${result}\n")
      case default => println("\n\nDocument is valid.\n")
    }
    result // always have your snippet return a value that cannot easily be "optimized away"

    //////////////////////////////////////////////////////////
  }

  override def tearDown(): Unit = { }
}