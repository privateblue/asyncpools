package org.pblue.asyncpools.slick

import org.pblue.asyncpools.{ PoolableObjectFactory, Config, AsyncPoolsException }

import scala.slick.session.{ Database, Session }

class SessionFactory(poolConfig: com.typesafe.config.Config) extends PoolableObjectFactory[Session] with Config {

	def create = {
		val driver = poolConfig.get("driver", throw new AsyncPoolsException(s"Missing driver configuration"))
		Class.forName(driver)
		Database
			.forURL(
				url = poolConfig.get("url", ""),
				user = poolConfig.get("user", ""),
				password = poolConfig.get("password", ""),
				driver = driver)
			.createSession()
	}

}