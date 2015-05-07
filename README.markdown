# Micro-Benchmarking Scala Maps #

[![travis status](https://travis-ci.org/dvmlls/scala-map-benchmarking.svg?branch=master)](https://travis-ci.org/dvmlls/scala-map-benchmarking)
  
Using [Caliper](http://code.google.com/p/caliper/) and Scala 2.11.6.

## Results ##

For maps with 100, 1000, and 10000 elements:

![graph] (src/R/plot.png)

Map implementations: 
* java.util.concurrent.HashMap
* java.util.HashMap
* java.util.TreeMap
* scala.collection.immutable.HashMap
* scala.collection.immutable.IntMap
* scala.collection.immutable.LongMap
* scala.collection.immutable.TreeMap
* scala.collection.mutable.HashMap
* scala.collection.mutable.LinkedHashMap
* scala.collection.mutable.LongMap
* scala.collection.mutable.OpenHashMap