package beam.analysis

import beam.agentsim.agents.vehicles.BeamVehicleType
import beam.agentsim.events.{ParkingEvent, PathTraversalEvent, RefuelSessionEvent}
import beam.analysis.plots.GraphAnalysis
import beam.router.Modes.BeamMode
import beam.sim.BeamServices
import beam.sim.metrics.SimulationMetricCollector.SimulationTime
import beam.utils.ProfilingUtils
import org.apache.curator.utils.DebugUtils
import org.matsim.api.core.v01.Id
import org.matsim.api.core.v01.events.Event
import org.matsim.core.controler.events.IterationEndsEvent

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

case class EventStatus(start: Double, end: Double, eventType: String, nextType: Option[String] = None)

class RideHailFleetAnalysis(beamServices: BeamServices) extends GraphAnalysis {

  val rhFleetAnalysis = new RideHailFleetAnalysisInternal(
    beamServices.beamScenario.vehicleTypes,
    beamServices.simMetricCollector.writeIteration
  )

  override def createGraph(event: IterationEndsEvent): Unit = rhFleetAnalysis.createGraph()
  override def processStats(event: Event): Unit = rhFleetAnalysis.processStats(event)
  override def resetStats(): Unit = rhFleetAnalysis.resetStats()
}
