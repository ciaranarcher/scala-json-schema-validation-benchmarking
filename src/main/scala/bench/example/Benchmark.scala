package bench.example

import java.lang.AssertionError
import java.util

import com.google.caliper.Param
import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.networknt.schema.{ValidationMessage, JsonSchemaFactory => NtJsonSchemaFactory}
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
  @Param(Array("10000"))
  val length: Int = 0

  // set up all your benchmark data here
  val playJsonSchemaValidator = new PlayJsonSchemaValidator()
  val orgJsonSchemaValidator = new OrgJsonSchemaValidator()
  val fgeJsonSchemaValidator = new FgeJsonSchemaValidator()
  val ntJsonSchemaValidator = new NtJsonSchemaValidator()

  val validators = Map(
    "play" -> new PlayJsonSchemaValidator,
    "orgjson" -> new OrgJsonSchemaValidator,
    "fge" -> new FgeJsonSchemaValidator,
    "nt" -> new NtJsonSchemaValidator
  )


  validators.values.foreach(_.sanityCheck())

  // the actual code you'd like to test needs to live in one or more methods
  // whose names begin with 'time' and which accept a single 'reps: Int' parameter
  // the body of the method simply executes the code we wish to measure, 'reps' times
  // you can use the 'repeat' method from the SimpleScalaBenchmark trait to repeat with relatively low overhead
  // however, if your code snippet is very fast you might want to implement the reps loop directly with 'while'
  def timePlaySchemaValidator(reps: Int) = repeat(reps) {
    playJsonSchemaValidator.parseAndValidate()
  }

  def timeOrgJsonSchemaValidator(reps: Int) = repeat(reps) {
    orgJsonSchemaValidator.parseAndValidate()
  }

  def timeFgeJsonSchemaValidator(reps: Int) = repeat(reps) {
    fgeJsonSchemaValidator.parseAndValidate()
  }

  def timeNtJsonSchemaValidator(reps: Int) = repeat(reps) {
    ntJsonSchemaValidator.parseAndValidate()
  }
}

trait Logger {
  def log(str: String) = println(s">> BENCH >> ${name} >> ${str}")
  def name = this.getClass.getSimpleName
}

trait Validator[A] {
  def parseAndValidate(): A
  def sanityCheck(): Unit
  def loadDocument(resourceName: String): Any
}

class NtJsonSchemaValidator extends Validator[util.Set[ValidationMessage]] with Logger {
  val schemaData = loadDocument("page_view.schema.json")
  log("schema read successfully")

  val documentData = loadDocument("page_view.sample.json")
  log("document data read successfully")
  val mapper = new ObjectMapper()

  val factory = new NtJsonSchemaFactory()
  val validator = factory.getSchema(schemaData)

  def loadDocument(resourceName: String): String = {
    val document = Source.fromInputStream(getClass.getResourceAsStream(resourceName))
    try document.mkString finally document.close()
  }

  def parseAndValidate(): util.Set[ValidationMessage] = {
    val documentJson = mapper.readTree(documentData)
    validator.validate(documentJson)
  }

  def sanityCheck() = {
    val badDocumentData = loadDocument("page_view.invalid.sample.json")
    val badDocumentJson = mapper.readTree(badDocumentData)

    var errors = validator.validate(badDocumentJson)
    assert(errors.size > 0, "document validation should fail")

    errors = parseAndValidate()
    assert(errors.size == 0, "document validation should pass")

    log("sanity checks were successful")
  }
}

class PlayJsonSchemaValidator extends Validator[JsResult[_]] with Logger {
  val schemaData = loadDocument("page_view.schema.json")
  val jsonSchema = Json.parse(schemaData)
  log("schema read successfully")

  val documentData = loadDocument("page_view.sample.json")
  log("document data read successfully")

  val schema = Json.fromJson[SchemaType](jsonSchema).get
  val validator = new SchemaValidator

  def loadDocument(resourceName: String): String = {
    val document = Source.fromInputStream(getClass.getResourceAsStream(resourceName))
    try document.mkString finally document.close()
  }

  def parseAndValidate() = {
    val documentJson = Json.parse(documentData)
    validator.validate(schema, documentJson)
  }

  def sanityCheck() = {
    val badDocumentData = loadDocument("page_view.invalid.sample.json")
    val badDocumentJson = Json.parse(badDocumentData)

    validator.validate(schema, badDocumentJson) match {
      case _: JsSuccess[_] => throw new AssertionError("document validation should fail")
      case _ =>
    }

    parseAndValidate() match {
      case _: JsError => throw new AssertionError("document validation should pass")
      case _ =>
    }

    log("sanity checks were successful")
  }
}

class OrgJsonSchemaValidator extends Validator[Int] with Logger {
  val schemaData = loadDocument("page_view.schema.json")
  val schema = new JSONObject(schemaData)
  log("schema read successfully")

  val documentData = loadDocument("page_view.sample.json")
  log("document data read successfully")

  val validator = SchemaLoader.load(schema)

  def loadDocument(resourceName: String) = {
    Source.fromInputStream(getClass.getResourceAsStream(resourceName))
  }

  def parseAndValidate() = {
    val document = new JSONObject(documentData)
    validator.validate(document)
    1 // Needs to return an int
  }

  def sanityCheck() = {

    // Seems we cannot get this library to raise a ValidationException :/
    // https://github.com/everit-org/json-schema

    log("sanity checks failed! See code comments.")
  }
}

class FgeJsonSchemaValidator extends Validator[ProcessingReport] with Logger {
  val schemaData = loadDocument("page_view.schema.json")
  val schema = JsonLoader.fromString(schemaData)
  log("schema read successfully")

  val documentData = loadDocument("page_view.sample.json")
  log("document data read successfully")

  val factory = JsonSchemaFactory.byDefault()
  val validator = factory.getJsonSchema(schema)

  def loadDocument(resourceName: String): String = {
    val document = Source.fromInputStream(getClass.getResourceAsStream(resourceName))
    try document.mkString finally document.close()
  }

  def parseAndValidate(): ProcessingReport = {
    val document = JsonLoader.fromString(documentData)
    validator.validate(document)
  }

  def sanityCheck() = {
    val badDocumentData = loadDocument("page_view.invalid.sample.json")
    val badDocument = JsonLoader.fromString(badDocumentData)

    var report = validator.validate(badDocument)
    assert(!report.isSuccess, "document validation should fail")

    report = parseAndValidate()
    assert(report.isSuccess, "document validation should pass")

    log("sanity checks were successful")
  }
}
