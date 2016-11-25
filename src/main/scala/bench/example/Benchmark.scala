package bench.example

import com.google.caliper.Param
import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.main.JsonSchemaFactory
import play.api.libs.json._

import scala.io.Source
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject

// a caliper benchmark is a class that extends com.google.caliper.Benchmark
// the SimpleScalaBenchmark trait does it and also adds some convenience functionality
class Benchmark extends SimpleScalaBenchmark {
  // to make your benchmark depend on one or more parameterized values, create fields with the name you want
  // the parameter to be known by, and add this annotation (see @Param javadocs for more details)
  // caliper will inject the respective value at runtime and make sure to run all combinations
  @Param(Array("10", "10000"))
  val length: Int = 0

  // set up all your benchmark data here
  val playJsonSchemaValidator = new PlayJsonSchemaValidator()
  val orgJsonSchemaValidator = new OrgJsonSchemaValidator()
  val fgeJsonSchemaValidator = new FgeJsonSchemaValidator()

  // the actual code you'd like to test needs to live in one or more methods
  // whose names begin with 'time' and which accept a single 'reps: Int' parameter
  // the body of the method simply executes the code we wish to measure, 'reps' times
  // you can use the 'repeat' method from the SimpleScalaBenchmark trait to repeat with relatively low overhead
  // however, if your code snippet is very fast you might want to implement the reps loop directly with 'while'
  def timePlaySchemaValidator(reps: Int) = repeat(reps) {
    playJsonSchemaValidator.parseAndValidate
  }

  def timeOrgJsonSchemaValidator(reps: Int) = repeat(reps) {
    orgJsonSchemaValidator.validate
  }

  def timeFgeJsonSchemaValidator(reps: Int) = repeat(reps) {
    fgeJsonSchemaValidator.validate
  }
}

trait Logger {
  def log(str: String) = println(s">> BENCH >> ${name} >> ${str}")
  def name = this.getClass.getSimpleName
}

class PlayJsonSchemaValidator extends Logger {
  val schemaData = loadDocument("page_view.schema.json")
  val jsonSchema = Json.parse(schemaData)
  log("schema read successfully")

  val documentData = loadDocument("page_view.sample.json")
  log("document data read successfully")

  val schema = Json.fromJson[SchemaType](jsonSchema).get
  val validator = new SchemaValidator

  def loadDocument(resourceName: String) = {
    val document = Source.fromInputStream(getClass.getResourceAsStream(resourceName))
    try document.mkString finally document.close()
  }

  def parseAndValidate = {
    val documentJson = Json.parse(documentData)
    validator.validate(schema, documentJson)
  }
}

class OrgJsonSchemaValidator extends Logger {
  val schemaData = loadDocument("page_view.schema.json")
  val schema = new JSONObject(schemaData)
  log("schema read successfully")

  val documentData = loadDocument("page_view.sample.json")
  log("document data read successfully")

  val validator = SchemaLoader.load(schema)

  def loadDocument(resourceName: String) = {
    Source.fromInputStream(getClass.getResourceAsStream(resourceName))
  }

  def validate = {
    val document = new JSONObject(documentData)
    validator.validate(document)
    1 // Needs to return an int
  }
}

class FgeJsonSchemaValidator extends Logger {
  val schemaData = loadDocument("page_view.schema.json")
  val schema = JsonLoader.fromString(schemaData)
  log("schema read successfully")

  val documentData = loadDocument("page_view.sample.json")
  log("document data read successfully")

  val factory = JsonSchemaFactory.byDefault()
  val validator = factory.getJsonSchema(schema)

  def loadDocument(resourceName: String) = {
    val document = Source.fromInputStream(getClass.getResourceAsStream(resourceName))
    try document.mkString finally document.close()
  }

  def validate = {
    val document = JsonLoader.fromString(documentData)
    val result = validator.validate(document)
    result
  }
}
