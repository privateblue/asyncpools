package org.pblue.asyncpools

import scala.concurrent.duration.Duration

import akka.actor.{ ActorSystem, Props, OneForOneStrategy }
import akka.actor.SupervisorStrategy._
import akka.routing.{Pool, RoundRobinPool, BalancingPool }

class Master[Resource](
	val name: String,
	val size: Int,
	val maxNrOfRetries: Int,
	val retryRange: Duration,
	val objectFactory: Factory[Resource],
	val balancing: Boolean = false)(implicit actorSystem: ActorSystem) {

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

	val routerRef =
		actorSystem.actorOf(
			props =
				Props(classOf[Worker[Resource]], objectFactory)
					.withRouter(routerConfig),
			name = name)

}