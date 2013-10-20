package org.pblue.slickpools

import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.util.Timeout

import com.typesafe.config.{ Config, ConfigFactory }

trait WorkerPoolFactory extends PoolFactory {

	private lazy val appConfig = ConfigFactory.load

	implicit val actorSystem = 
		ActorSystem(
			"Slickpools",
			appConfig.getConfig(configRoot))

	def newConfiguredPool(name: String)(implicit config: Config = appConfig) = {
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