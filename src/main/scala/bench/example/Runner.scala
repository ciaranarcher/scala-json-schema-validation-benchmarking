package bench.example

import com.google.caliper.{Runner => CaliperRunner}

object Runner extends App {
  CaliperRunner.main(classOf[Benchmark], args: _*)
}
