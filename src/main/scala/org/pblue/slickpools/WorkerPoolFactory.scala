package org.pblue.slickpools

import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
import akka.util.Timeout

import com.typesafe.config.{ Config, ConfigFactory }

trait WorkerPoolFactory extends PoolFactory {

	private lazy val config = ConfigFactory.load

	implicit val actorSystem = 
		ActorSystem(
			"Slickpools",
			config)

	def newConfiguredPool(name: String) = {
		val poolConfig = config.getConfig(s"${configRoot}.${name}")
		
		val size = poolConfig.getInt("size")
		
		val defaultTimeout = Timeout(Duration(poolConfig.getString("defaultTimeout")).toMillis)

		val ds = 
			Datasource(
				url = poolConfig.getString("url"),
				user = poolConfig.getString("user"),
				password = poolConfig.getString("password"),
				driver = poolConfig.getString("driver"))

		new WorkerPool(
			name = name,
			size = size,
			defaultTimeout = defaultTimeout,
			datasource = ds)
	}

}