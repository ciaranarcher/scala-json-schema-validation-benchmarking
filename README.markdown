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
[info] Running bench.example.Runner
[info] >> BENCH >> PlayJsonSchemaValidator >> schema read successfully
[info] >> BENCH >> OrgJsonSchemaValidator >> schema read successfully
[info] >> BENCH >> FgeJsonSchemaValidator >> schema read successfully
[info] Do these parsers actually validate documents as expected? Testing now...
[info] All `good` documents validated as expected
[info] All `bad` documents validated as expected
[info]  0% Scenario{vm=java, trial=0, benchmark=PlaySchemaValidator, length=10000} 92516.01 ns; σ=3433.57 ns @ 10 trials
[info] 25% Scenario{vm=java, trial=0, benchmark=OrgJsonSchemaValidator, length=10000} 17561.98 ns; σ=378.69 ns @ 10 trials
[info] 50% Scenario{vm=java, trial=0, benchmark=FgeJsonSchemaValidator, length=10000} 34183.25 ns; σ=260.53 ns @ 3 trials
[info] 75% Scenario{vm=java, trial=0, benchmark=NtJsonSchemaValidator, length=10000} 4566.60 ns; σ=118.12 ns @ 10 trials
[info]
[info]              benchmark    us linear runtime
[info]    PlaySchemaValidator 92.52 ==============================
[info] OrgJsonSchemaValidator 17.56 =====
[info] FgeJsonSchemaValidator 34.18 ===========
[info]  NtJsonSchemaValidator  4.57 =
[info]
[info] vm: java
[info] trial: 0
[info] length: 10000
[success] Total time: 67 s, completed 26-Nov-2016 22:24:09
```

* Fastest is `NtJsonSchemaValidator` (https://github.com/networknt/json-schema-validator).
* `OrgJsonSchemaValidator` came 2nd but is discounted at this time due to it appearing not to work as expected (sanity check failed).

## Running
`sbt run`

