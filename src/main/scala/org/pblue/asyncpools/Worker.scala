package org.pblue.asyncpools

import scala.util.Try

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
		case Job(fn) => sender ! Try(fn(session))
	}

}