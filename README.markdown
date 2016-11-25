# Benchmarking Scala JSON schema validation libraries

## Implementations
As part of this benchmarking suite we've examined:

* https://github.com/fge/json-schema-validator (via http://json-schema.org/implementations) as `FgeJsonSchemaValidator`
* https://github.com/everit-org/json-schema  (via http://json-schema.org/implementations) as `OrgJsonSchemaValidator`
* https://github.com/networknt/json-schema-validator (via http://json-schema.org/implementations) as `SchemaValidator`
* https://github.com/eclipsesource/play-json-schema-validator (via Google search) as `PlayJsonSchemaValidator`
 

## Notes
* Using [Caliper](http://code.google.com/p/caliper/) and Scala 2.11.6 which provides JVM warm-up etc.
* We want to understand the document JSON parse and validate time, i.e. we presume the schema is pre-loaded.
* We are too concerned with ensuring we use a particular JSON parsing library, e.g. Jackson etc.
* Raw speed was the goal.
* Tests consisted for 10,000 parse and validation steps for each implementation. 

## Results

