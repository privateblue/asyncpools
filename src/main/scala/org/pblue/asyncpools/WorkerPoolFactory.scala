package org.pblue.asyncpools

import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
import akka.util.Timeout

trait WorkerPoolFactory extends Config {

	protected implicit val actorSystem = ActorSystem("AsyncPools", appConfig)

	protected def newPool[T](
		name: String,
		size: Int,
		defaultTimeout: Timeout,
		maxNrOfRetries: Int,
		retryRange: Duration,
		objectFactory: Factory[T]) =
		new WorkerPool(
			name = name,
			size = size,
			defaultTimeout = defaultTimeout,
			maxNrOfRetries = maxNrOfRetries,
			retryRange = retryRange,
			objectFactory = objectFactory)

	protected def newConfiguredPool[T](name: String)(factory: com.typesafe.config.Config => Factory[T]) = {
		val config = poolConfig(name)
		val size = config.get("size", throw new AsyncPoolsException(s"Unable to create pool $name, due to missing or invalid size configuration")).toInt
		val defaultTimeout = Timeout(Duration(config.get("defaultTimeout", throw new AsyncPoolsException(s"Unable to create pool $name, due to missing or invalid defaultTimeout configuration"))).toMillis)
		val maxNrOfRetries = config.get("maxNrOfRetries", throw new AsyncPoolsException(s"Unable to create pool $name, due to missing or invalid maxNrOfRetries configuration")).toInt
		val retryRange = Duration(config.get("retryRange", throw new AsyncPoolsException(s"Unable to create pool $name, due to missing or invalid retryRange configuration")))

		newPool(
			name = name,
			size = size,
			defaultTimeout = defaultTimeout,
			maxNrOfRetries = maxNrOfRetries,
			retryRange = retryRange,
			objectFactory = factory(config))
	}

}