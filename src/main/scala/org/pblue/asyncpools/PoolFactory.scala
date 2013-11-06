package org.pblue.asyncpools

import com.typesafe.config.Config

trait PoolFactory {
	val configRoot = "asyncpools"

	def newConfiguredPool(name: String): Pool
}