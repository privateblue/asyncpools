package org.pblue.asyncpools

import scala.util.{ Success, Failure }
import scala.concurrent.Future
import scala.concurrent.duration.Duration

import akka.actor.{ ActorSystem, Props, OneForOneStrategy }
import akka.actor.SupervisorStrategy._
import akka.routing.{Pool, RoundRobinPool, BalancingPool }
import akka.pattern.ask
import akka.util.Timeout

class WorkerPool[Resource](
	val name: String, 
	val size: Int, 
	val defaultTimeout: Timeout,
	val maxNrOfRetries: Int,
	val retryRange: Duration,
	val objectFactory: Factory[Resource],
	val balancing: Boolean = false)(implicit actorSystem: ActorSystem) {

	protected def markJobReceived(): Unit = ()
	protected def markSuccess(): Unit = ()
	protected def markFailure(): Unit = ()
	protected def markExecutionTime(executionTimeMs: Long): Unit = ()

	private val supervisor = 
		OneForOneStrategy(
			maxNrOfRetries = maxNrOfRetries, 
	    	withinTimeRange = retryRange) {

			case _: Throwable => Restart
		}

	private val routerConfig: Pool with Product =
		if (balancing)
			BalancingPool(nrOfInstances = size,
				supervisorStrategy = supervisor)
		else
			RoundRobinPool(
				nrOfInstances = size,
				supervisorStrategy = supervisor)

	private val poolRef =
		actorSystem.actorOf(
			props =
				Props(classOf[Worker[Resource]], objectFactory)
					.withRouter(routerConfig),
			name = name)


	def execute[Result](fn: Resource => Result)(implicit timeout: Timeout = defaultTimeout): Future[Result] = {
		val calledAt = System.currentTimeMillis
		markJobReceived()
		ask(poolRef, Job[Resource, Result](fn)).map {
			case Success(res) =>
				markExecutionTime(System.currentTimeMillis - calledAt)
				markSuccess()
				res.asInstanceOf[Result]
			case Failure(t) =>
				markExecutionTime(System.currentTimeMillis - calledAt)
				markFailure()
				throw new AsyncPoolsException("AsyncPools execution error", t)
		}(executor = actorSystem.dispatcher)
	}

}