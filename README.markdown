# Benchmarking Scala JSON schema validation libraries

## Implementations
As part of this benchmarking suite we've examined:

* https://github.com/fge/json-schema-validator (via http://json-schema.org/implementations) as `FgeJsonSchemaValidator`
* https://github.com/everit-org/json-schema  (via http://json-schema.org/implementations) as `OrgJsonSchemaValidator`
* https://github.com/networknt/json-schema-validator (via http://json-schema.org/implementations) as `NtJsonSchemaValidator`
* https://github.com/eclipsesource/play-json-schema-validator (via Google search) as `PlayJsonSchemaValidator`
 

## Notes
* Using [Caliper](http://code.google.com/p/caliper/) and Scala 2.11.6 which provides JVM warm-up etc.
* We want to understand the document JSON parse and validate time, i.e. we presume the schema is pre-loaded.
* We are too concerned with ensuring we use a particular JSON parsing library, e.g. Jackson etc.
* Raw speed was the goal.
* Tests consisted for 10,000 parse and validation steps for each implementation. 
* A sanity test for each implementation is included to ensure that they pass and fail validation as expected.

## Results

```
[info] Loading global plugins from /Users/carcher/.sbt/0.13/plugins
[info] Loading project definition from /Users/carcher/Code/scala/scala-json-schema-validation-benchmarking/project
[info] Set current project to scala-map-benchmarking (in build file:/Users/carcher/Code/scala/scala-json-schema-validation-benchmarking/)
[info] Compiling 1 Scala source to /Users/carcher/Code/scala/scala-json-schema-validation-benchmarking/target/scala-2.11/classes...
[info] Compiling 1 Scala source to /Users/carcher/Code/scala/scala-json-schema-validation-benchmarking/target/scala-2.11/classes...
[info] Running bench.example.Runner
[info] >> BENCH >> PlayJsonSchemaValidator >> schema read successfully
[info] >> BENCH >> PlayJsonSchemaValidator >> document data read successfully
[info] >> BENCH >> OrgJsonSchemaValidator >> schema read successfully
[info] >> BENCH >> OrgJsonSchemaValidator >> document data read successfully
[info] >> BENCH >> FgeJsonSchemaValidator >> schema read successfully
[info] >> BENCH >> FgeJsonSchemaValidator >> document data read successfully
[info] >> BENCH >> NtJsonSchemaValidator >> schema read successfully
[info] >> BENCH >> NtJsonSchemaValidator >> document data read successfully
[error] SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
[error] SLF4J: Defaulting to no-operation (NOP) logger implementation
[error] SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
[info] >> BENCH >> PlayJsonSchemaValidator >> sanity checks were successful
[info] >> BENCH >> OrgJsonSchemaValidator >> sanity checks failed! See code comments.
[info] >> BENCH >> FgeJsonSchemaValidator >> sanity checks were successful
[info] >> BENCH >> NtJsonSchemaValidator >> sanity checks were successful
[info]  0% Scenario{vm=java, trial=0, benchmark=PlaySchemaValidator, length=10000} 88821.78 ns; σ=854.56 ns @ 3 trials
[info] 25% Scenario{vm=java, trial=0, benchmark=OrgJsonSchemaValidator, length=10000} 13235.91 ns; σ=773.60 ns @ 10 trials
[info] 50% Scenario{vm=java, trial=0, benchmark=FgeJsonSchemaValidator, length=10000} 33915.07 ns; σ=1382.68 ns @ 10 trials
[info] 75% Scenario{vm=java, trial=0, benchmark=NtJsonSchemaValidator, length=10000} 4705.60 ns; σ=26.44 ns @ 3 trials
[info]
[info]              benchmark    us linear runtime
[info]    PlaySchemaValidator 88.82 ==============================
[info] OrgJsonSchemaValidator 13.24 ====
[info] FgeJsonSchemaValidator 33.92 ===========
[info]  NtJsonSchemaValidator  4.71 =
[info]
[info] vm: java
[info] trial: 0
[info] length: 10000
[success] Total time: 63 s, completed 25-Nov-2016 11:57:24
```

* Fastest is `NtJsonSchemaValidator` (https://github.com/networknt/json-schema-validator).
* `OrgJsonSchemaValidator` came 2nd but is discounted at this time due to it appearing not to work as expected (sanity check failed).

## Running
`sbt run`

