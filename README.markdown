### Micro-Benchmarking Scala Maps ###
  
A fork of [this project](https://github.com/sirthias/scala-benchmarking-template) which uses 
[Caliper](http://code.google.com/p/caliper/) to micro-benchmark.

Currently using scala 2.11.6.

## Results ##
 
![graph] (src/R/plot.png)

* java.util.concurrent.HashMap
* java.util.HashMap
* scala.collections.immutable.HashMap
* scala.collections.immutable.LongMap
* scala.collections.mutable.HashMap
* scala.collections.mutable.LongMap