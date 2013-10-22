package org.pblue.slickpools

import scala.util.{ Success, Failure }

import akka.actor.Actor

import scala.slick.session.Database

class Worker(ds: Datasource) extends Actor {

	private val session = {
		Class.forName(ds.driver)
		Database
			.forURL(
				url = ds.url,
				user = ds.user,
				password = ds.password,
				driver = ds.driver)
			.createSession()
	}

	def receive = {
		case Job(fn) => 
			val res = 
				try {
					Success(fn(session))
				} catch {
					case t: Throwable => Failure(t)
				}
			sender ! res

	}

}