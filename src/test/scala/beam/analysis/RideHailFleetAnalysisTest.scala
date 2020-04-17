package beam.analysis

import beam.sim.metrics.SimulationMetricCollector.SimulationTime
import beam.utils.{BeamVehicleUtils, EventReader, ProfilingUtils}
import org.matsim.api.core.v01.events.Event
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable
import scala.util.Try

class Values(values: mutable.Map[Long, Double] = mutable.Map.empty) {

  def apply(key: Long): Double = values.getOrElse(key, 0.0)

  def Add(time: SimulationTime, value: Double): Unit = {
    val hour = time.hours
    values.get(hour) match {
      case Some(oldValue) => values(hour) = value + oldValue
      case None           => values(hour) = value
    }
  }

  def Print(prefix: String): Unit = {
    //collectedMetrics("rh-ev-cav-count") ("vehicle-state:driving-topickup") (0) shouldBe 0.0
    values.toSeq
      .sortBy(_._1)
      .foreach(entry => {
        val (hour, value) = entry
        println(s"$prefix ($hour) shouldBe $value")
      })
    println("")
  }
}

class Metrics(val metrics: mutable.Map[String, Values] = mutable.Map.empty) extends FlatSpec with Matchers {

  def apply(key: String): Values = metrics.getOrElse(key, new Values())

  def Add(time: SimulationTime, value: Double, tags: Map[String, String]): Unit = {
    tags.size should be(1)

    val tag = tags.head._1 + ":" + tags.head._2
    metrics.get(tag) match {
      case Some(values) => values.Add(time, value)
      case None =>
        val values = new Values()
        values.Add(time, value)
        metrics(tag) = values
    }
  }

  def Print(prefix: String): Unit = {
    //collectedMetrics("rh-ev-cav-count") ("vehicle-state:driving-topickup") (0) shouldBe 0.0
    metrics.foreach(entry => {
      val (tag, values) = entry
      values.Print(prefix + "(\"" + tag + "\")")
    })
  }
}

class RideHailFleetAnalysisTest extends FlatSpec with Matchers {

  // eventsFileBig was downloaded from https://beam-outputs.s3.amazonaws.com/output/austin/austin-prod-200k-skims-with-h3-index__2020-04-14_07-06-56_xah/ITERS/it.0/0.events.csv.gz
  val eventsFileBig = "/mnt/data/work/beam/test-data/EVCAV-performance-test-0.events.csv.gz"
  val eventsFileSmall = "/mnt/data/work/beam/test-data/EVCAV-performance-test-small-0.events.csv.gz"

  val vehicleTypeFile = "test/input/sf-light/vehicleTypes.csv"
  val vehicleTypes = BeamVehicleUtils.readBeamVehicleTypeFile(vehicleTypeFile)

  vehicleTypes shouldNot be(empty)

  val collectedMetrics: mutable.Map[String, Metrics] = mutable.Map.empty

  def writeIteration(
    metricName: String,
    time: SimulationTime,
    metricValue: Double = 1.0,
    tags: Map[String, String] = Map.empty,
    overwriteIfExist: Boolean = false
  ): Unit = {

    overwriteIfExist should be(true)

    collectedMetrics.get(metricName) match {
      case Some(metrics) => metrics.Add(time, metricValue, tags)
      case None =>
        val metrics = new Metrics()
        metrics.Add(time, metricValue, tags)
        collectedMetrics(metricName) = metrics
    }
  }

  def testBig(metrics: mutable.Map[String, Metrics]): Unit = {
    RideHailFleetAnalysisTestData.testExpectedOutputBig1(metrics)
    RideHailFleetAnalysisTestData.testExpectedOutputBig2(metrics)
    RideHailFleetAnalysisTestData.testExpectedOutputBig3(metrics)
    RideHailFleetAnalysisTestData.testExpectedOutputBig4(metrics)
    RideHailFleetAnalysisTestData.testExpectedOutputBig5(metrics)
    RideHailFleetAnalysisTestData.testExpectedOutputBig6(metrics)
    RideHailFleetAnalysisTestData.testExpectedOutputBig7(metrics)
    RideHailFleetAnalysisTestData.testExpectedOutputBig8(metrics)
  }

  def testSmall(metrics: mutable.Map[String, Metrics]): Unit = {
    RideHailFleetAnalysisTestData.testExpectedOutputSmall1(metrics)
    RideHailFleetAnalysisTestData.testExpectedOutputSmall2(metrics)
    RideHailFleetAnalysisTestData.testExpectedOutputSmall3(metrics)
    RideHailFleetAnalysisTestData.testExpectedOutputSmall4(metrics)
    RideHailFleetAnalysisTestData.testExpectedOutputSmall5(metrics)
    RideHailFleetAnalysisTestData.testExpectedOutputSmall6(metrics)
    RideHailFleetAnalysisTestData.testExpectedOutputSmall7(metrics)
    RideHailFleetAnalysisTestData.testExpectedOutputSmall8(metrics)
  }

  def test(process: Event => Unit): Unit = {

    collectedMetrics.clear()

    val (it, toClose) = EventReader.fromCsvFile(eventsFileSmall, _ => true)
    try {
      it.foreach { event =>
        process(event)
      }
    } finally {
      Try(toClose.close())
    }

    collectedMetrics shouldNot be(empty)

    testSmall(collectedMetrics)

    println("done")

    //    var counter = 11
    //    collectedMetrics.foreach(entry => {
    //      val (metricName, metrics) = entry
    //      println()
    //      println(s"def testExpectedOutput$counter(collectedMetrics: mutable.Map[String, Metrics]): Unit = {")
    //
    //      //collectedMetrics("rh-ev-cav-count") ("vehicle-state:driving-topickup") (0) shouldBe 0.0
    //      metrics.Print("    collectedMetrics(\"" + metricName + "\")")
    //
    //      println("}")
    //      counter += 1
    //    })  }

  }

  "fleet analysis" must "return expected values" in {
    val RHFleetEventsAnalysis = new RideHailFleetAnalysisInternal(vehicleTypes, writeIteration)
    test(event => RHFleetEventsAnalysis.processStats(event))
  }

  "fleet analysis V2" must "return expected values" in {
    val RHFleetEventsAnalysis = new RideHailFleetAnalysisInternalV2(vehicleTypes, writeIteration)
    test(event => RHFleetEventsAnalysis.processStats(event))
  }
}
