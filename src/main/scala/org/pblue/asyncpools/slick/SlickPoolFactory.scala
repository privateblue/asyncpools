package org.pblue.asyncpools.slick

import org.pblue.asyncpools.WorkerPoolFactory

import scala.concurrent.duration.Duration

import akka.util.Timeout

trait SlickPoolFactory extends WorkerPoolFactory {

	def newSlickPool(
		name: String,
		size: Int,
		defaultTimeout: Timeout,
		maxNrOfRetries: Int,
		retryRange: Duration,
		url: String,
		user: String,
		password: String,
		driver: String) =
		newPool(
			name = name,
			size = size,
			defaultTimeout = defaultTimeout,
			maxNrOfRetries = maxNrOfRetries,
			retryRange = retryRange,
			objectFactory = new SessionFactory(
				url = url,
				user = user,
				password = password,
				driver = driver))

	def newConfiguredSlickPool(name: String) = 
		newConfiguredPool(name)(config => new ConfiguredSessionFactory(config))

}