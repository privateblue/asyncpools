package org.pblue.asyncpools.slick

import org.pblue.asyncpools.{ Config, AsyncPoolsException }

class ConfiguredSessionFactory(poolConfig: com.typesafe.config.Config) 
	extends AbstractSessionFactory 
	with Config {
		
	val url = poolConfig.get("url", "")
	val user = poolConfig.get("user", "")
	val password = poolConfig.get("password", "")
	val driver = poolConfig.get("driver", throw new AsyncPoolsException(s"Missing driver configuration"))
}