package bench.example

import java.util

import com.google.caliper.Param
import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.networknt.schema.{ValidationMessage, JsonSchemaFactory => NtJsonSchemaFactory}
import org.everit.json.schema.ValidationException
import play.api.libs.json._

import scala.io.{BufferedSource, Source}
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


  val schemaData = loadDocument("page_view.schema.json")
  val goodDocData = loadDocument("page_view.sample.json")
  val badDocData = loadDocument("page_view.invalid.sample.json")

  val validators = Map(
    "play" -> new PlayJsonSchemaValidator(schemaData),
    "orgjson" -> new OrgJsonSchemaValidator(schemaData),
    "fge" -> new FgeJsonSchemaValidator(schemaData),
    "nt" -> new NtJsonSchemaValidator(schemaData)
  )

  println("Do these parsers actually validate documents as expected? Testing now...")
  printViolations(validators.mapValues(_.parseAndValidate(goodDocData))
    .collect { case (k, None) => k }, "good")

  printViolations(validators.mapValues(_.parseAndValidate(badDocData))
    .collect{ case (k, Some(_)) => k}, "bad")

  def printViolations(violations: Iterable[String], testType: String) = {
    violations.size match {

      case 0 => println(s"All `${testType}` documents validated as expected")
      case _ => println(s"There were issues validating `${testType}` documents for parsers: ${violations}")
    }
  }

  // the actual code you'd like to test needs to live in one or more methods
  // whose names begin with 'time' and which accept a single 'reps: Int' parameter
  // the body of the method simply executes the code we wish to measure, 'reps' times
  // you can use the 'repeat' method from the SimpleScalaBenchmark trait to repeat with relatively low overhead
  // however, if your code snippet is very fast you might want to implement the reps loop directly with 'while'
  def timePlaySchemaValidator(reps: Int) = repeat(reps) {
    validators("play").parseAndValidate(goodDocData)
  }

  def timeOrgJsonSchemaValidator(reps: Int) = repeat(reps) {
    validators("orgjson").parseAndValidate(goodDocData)
  }

  def timeFgeJsonSchemaValidator(reps: Int) = repeat(reps) {
    validators("fge").parseAndValidate(goodDocData)
  }

  def timeNtJsonSchemaValidator(reps: Int) = repeat(reps) {
    validators("nt").parseAndValidate(goodDocData)
  }

  private def loadDocument(resourceName: String): String = {
    val document = Source.fromInputStream(getClass.getResourceAsStream(resourceName))
    try document.mkString finally document.close()
  }
}

trait Logger {
  def log(str: String) = println(s">> BENCH >> ${name} >> ${str}")
  def name = this.getClass.getSimpleName
}

trait Validator[A] {
  def parseAndValidate(documentData: String): Option[_]
}

class NtJsonSchemaValidator(schemaData: String) extends Validator[util.Set[ValidationMessage]] with Logger {
  val mapper = new ObjectMapper()

  val factory = new NtJsonSchemaFactory()
  val validator = factory.getSchema(schemaData)


  def parseAndValidate(documentData: String): Option[JsonNode] = {
    val documentJson = mapper.readTree(documentData)
    val validationMessages = validator.validate(documentJson)
    if (validationMessages.isEmpty) Some(documentJson) else None
  }
}

class PlayJsonSchemaValidator(schemaData: String) extends Validator[JsResult[_]] with Logger {
  val jsonSchema = Json.parse(schemaData)
  log("schema read successfully")

  val schema = Json.fromJson[SchemaType](jsonSchema).get
  val validator = new SchemaValidator

  def loadDocument(resourceName: String): String = {
    val document = Source.fromInputStream(getClass.getResourceAsStream(resourceName))
    try document.mkString finally document.close()
  }

  def parseAndValidate(documentData: String): Option[JsValue] = {
    val documentJson = Json.parse(documentData)
    validator.validate(schema, documentJson) match {
      case JsSuccess(value, _) => Some(value)
      case JsError(_) => None
    }
  }
}

class OrgJsonSchemaValidator(schemaData: String) extends Validator[Int] with Logger {
  val schema = new JSONObject(schemaData)
  log("schema read successfully")

  val validator = SchemaLoader.load(schema)

  def loadDocument(resourceName: String): BufferedSource = {
    Source.fromInputStream(getClass.getResourceAsStream(resourceName))
  }

  def parseAndValidate(documentData: String): Option[JSONObject] = {
    val document = new JSONObject(documentData)

    try {
      validator.validate(document)
      Some(document)
    } catch {
      case e: ValidationException => None
    }
  }
}

class FgeJsonSchemaValidator(schemaData: String) extends Validator[ProcessingReport] with Logger {
  val schema = JsonLoader.fromString(schemaData)
  log("schema read successfully")

  val factory = JsonSchemaFactory.byDefault()
  val validator = factory.getJsonSchema(schema)

  def loadDocument(resourceName: String): String = {
    val document = Source.fromInputStream(getClass.getResourceAsStream(resourceName))
    try document.mkString finally document.close()
  }

  def parseAndValidate(documentData: String): Option[ProcessingReport] = {
    val document = JsonLoader.fromString(documentData)
    val report = validator.validate(document)

    if (report.isSuccess) Some(report) else None
  }
}
