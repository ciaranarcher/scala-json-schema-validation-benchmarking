package bench.example

import com.google.caliper.Param
import java.util.Random
import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import play.api.libs.json._
import scala.io.Source
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener

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
  val playSchemaValidator = new PlaySchemaValidator()
  val orgJsonSchemaValidator = new OrgJsonSchemaValidator()

  // the actual code you'd like to test needs to live in one or more methods
  // whose names begin with 'time' and which accept a single 'reps: Int' parameter
  // the body of the method simply executes the code we wish to measure, 'reps' times
  // you can use the 'repeat' method from the SimpleScalaBenchmark trait to repeat with relatively low overhead
  // however, if your code snippet is very fast you might want to implement the reps loop directly with 'while'
  def timePlaySchemaValidator(reps: Int) = repeat(reps) {
    playSchemaValidator.validate
  }

  def timeOrgJsonSchemaValidator(reps: Int) = repeat(reps) {
    orgJsonSchemaValidator.validate

  }
}

trait Logger {
  def log(str: String) = println(s">> BENCH >> ${name} >> ${str}")
  def name = this.getClass.getSimpleName
}

class PlaySchemaValidator extends Logger {
  /*
   * Read the schema and create a validator
   */
  val jsonSchema = loadJson("page_view.schema.json")
  log("schema read successfully")

  val schema = Json.fromJson[SchemaType](jsonSchema).get
  val validator = new SchemaValidator

  /*
   * Read the sample JSON document
   */
  val documentJson = loadJson("page_view.sample.json")
  log("document read successfully")

  def loadJson(resourceName: String) = {
    val document = Source.fromInputStream(getClass.getResourceAsStream(resourceName))
    val documentData = try document.mkString finally document.close()
    Json.parse(documentData)
  }


  def validate = {
    val result = validator.validate(schema, documentJson)
    result match {
      case _: JsError => println(s"\n\nDocument is invalid: ${result}\n")
      case default => println("\n\nDocument is valid.\n")
    }
    result
  }
}

class OrgJsonSchemaValidator extends Logger {
  val schema = loadJson("page_view.schema.json")
  log("schema read successfully")
  val document = loadJson("page_view.sample.json")
  log("document read successfully")

  val validator = SchemaLoader.load(schema)

  def loadJson(resourceName: String) = {
    val document = new JSONTokener(getClass.getResourceAsStream(resourceName))
    new JSONObject(document)
  }

  def validate = {
    validator.validate(document)
    1 // Needs to return an int
  }
}