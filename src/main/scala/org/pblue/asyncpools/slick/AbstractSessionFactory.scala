package org.pblue.asyncpools.slick

import org.pblue.asyncpools.PoolableObjectFactory

import scala.slick.session.{ Database, Session }

trait AbstractSessionFactory extends PoolableObjectFactory[Session] {
	
	val url: String
	val user: String
	val password: String
	val driver: String

	def create = {
		Class.forName(driver)
		Database
			.forURL(
				url = url,
				user = user,
				password = password,
				driver = driver)
			.createSession()
	}

}