package org.pblue.slickpools

import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
import akka.util.Timeout

import com.typesafe.config.{ Config, ConfigFactory, ConfigException }

trait WorkerPoolFactory extends PoolFactory {

	private lazy val config = 
		try {
			ConfigFactory.load
		} catch {
			case ce: ConfigException => throw new SlickpoolsException("Configuration failed to load", ce)
		}

	implicit val actorSystem = 
		ActorSystem(
			"Slickpools",
			config)

	def newConfiguredPool(name: String) = {
		val poolConfig = 
			try {
				config.getConfig(s"${configRoot}.${name}")
			} catch {
				case ce: ConfigException => 
					throw new SlickpoolsException(s"Unable to create pool $name, due to missing configuration", ce)
			}
		
		val size = 
			try {
				poolConfig.getInt("size")
			} catch {
				case ce: ConfigException => 
					throw new SlickpoolsException(s"Unable to create pool $name, due to missing or invalid size configuration", ce)
			}
		
		val defaultTimeout = 
			try {
				Timeout(Duration(poolConfig.getString("defaultTimeout")).toMillis)
			} catch {
				case ce: ConfigException => 
					throw new SlickpoolsException(s"Unable to create pool $name, due to missing or invalid defaultTimeout configuration", ce)
			}

		val maxNrOfRetries = 
			try {
				poolConfig.getInt("maxNrOfRetries")
			} catch {
				case ce: ConfigException => 
					throw new SlickpoolsException(s"Unable to create pool $name, due to missing or invalid maxNrOfRetries configuration", ce)
			}

		val retryRange = 
			try {
				Duration(poolConfig.getString("retryRange"))
			} catch {
				case ce: ConfigException => 
					throw new SlickpoolsException(s"Unable to create pool $name, due to missing or invalid retryRange configuration", ce)
			}

		val driver = 
			try {
				poolConfig.getString("driver")
			} catch {
				case ce: ConfigException => 
					throw new SlickpoolsException(s"Unable to create pool $name, due to missing driver configuration", ce)
			}

		val ds = 
			Datasource(
				url = 
					try {
						poolConfig.getString("url")
					} catch {
						case ce: ConfigException => ""
					},
				user = 
					try {
						poolConfig.getString("user")
					} catch {
						case ce: ConfigException => ""
					},
				password = 
					try {
						poolConfig.getString("password")
					} catch {
						case ce: ConfigException => ""
					},
				driver = driver)

		new WorkerPool(
			name = name,
			size = size,
			defaultTimeout = defaultTimeout,
			maxNrOfRetries = maxNrOfRetries,
			retryRange = retryRange,
			datasource = ds)
	}

}