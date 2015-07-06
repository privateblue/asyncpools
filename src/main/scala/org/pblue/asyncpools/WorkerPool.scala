package org.pblue.asyncpools

import scala.util.{ Success, Failure }
import scala.concurrent.Future
import scala.concurrent.duration.Duration

import akka.actor.{ ActorSystem, Props, OneForOneStrategy }
import akka.actor.SupervisorStrategy._
import akka.routing.RoundRobinRouter
import akka.pattern.ask
import akka.util.Timeout

class WorkerPool[Resource](
	val name: String, 
	val size: Int, 
	val defaultTimeout: Timeout,
	val maxNrOfRetries: Int,
	val retryRange: Duration,
	val objectFactory: Factory[Resource])(implicit actorSystem: ActorSystem) {

	private val supervisor = 
		OneForOneStrategy(
			maxNrOfRetries = maxNrOfRetries, 
	    	withinTimeRange = retryRange) {

			case _: Throwable => Restart
		}

	private val router = 
		actorSystem.actorOf(
			props = 
				Props(classOf[Worker[Resource]], objectFactory)
					.withRouter(RoundRobinRouter(
						nrOfInstances = size,
						supervisorStrategy = supervisor)), 
			name = name)

	def execute[Result](fn: Resource => Result)(implicit timeout: Timeout = defaultTimeout): Future[Result] =
		ask(router, Job[Resource, Result](fn)).map {
			case Success(res: Result) => res
			case Failure(t) => throw new AsyncPoolsException("AsyncPools execution error", t)
		}(executor = actorSystem.dispatcher)

}