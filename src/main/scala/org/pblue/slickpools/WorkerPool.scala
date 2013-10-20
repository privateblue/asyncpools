package org.pblue.slickpools

import scala.concurrent.Future
import scala.reflect.ClassTag

import akka.actor.{ ActorSystem, Actor, Props }
import akka.routing.RoundRobinRouter
import akka.pattern.ask
import akka.util.Timeout

import scala.slick.session.Session

class WorkerPool(
	name: String, 
	size: Int, 
	defaultTimeout: Timeout,
	datasource: Datasource)(implicit actorSystem: ActorSystem) extends Pool {

	private val router = 
		actorSystem.actorOf(
			props = Props(classOf[Worker], datasource).withRouter(RoundRobinRouter(size)), 
			name = name)

	def execute[T : ClassTag](fn: Session => T)(implicit timeout: Timeout = defaultTimeout) = 
		ask(router, Payload(fn)).mapTo[T]

}