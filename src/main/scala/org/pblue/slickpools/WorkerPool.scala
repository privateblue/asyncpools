package org.pblue.slickpools

import scala.util.{ Success, Failure }
import scala.concurrent._
import scala.concurrent.duration._

import akka.actor.{ ActorSystem, Props, OneForOneStrategy }
import akka.actor.SupervisorStrategy._
import akka.routing.RoundRobinRouter
import akka.pattern.ask
import akka.util.Timeout

import scala.slick.session.Session

class WorkerPool(
	name: String, 
	size: Int, 
	defaultTimeout: Timeout,
	datasource: Datasource)(implicit actorSystem: ActorSystem) extends Pool {

	private val supervisor = 
		OneForOneStrategy(
			maxNrOfRetries = 10, 
	    	withinTimeRange = 1 minute) {

			case _: Throwable => Resume
		}

	private val router = 
		actorSystem.actorOf(
			props = 
				Props(classOf[Worker], datasource)
					.withRouter(RoundRobinRouter(
						nrOfInstances = size,
						supervisorStrategy = supervisor)), 
			name = name)

	private implicit val ec = actorSystem.dispatcher 

	def execute[T](fn: Session => T)(implicit timeout: Timeout = defaultTimeout) = 
		ask(router, Job(fn)).map {
			case Success(res: T) => res
			case Failure(t) => throw new SlickpoolsException("Slickpools query execution error", t)
		}

}