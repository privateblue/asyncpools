package org.pblue.slickpools

import com.typesafe.config.Config

trait PoolFactory {
	val configRoot = "slickpools"

	def newConfiguredPool(name: String)(implicit config: Config): Pool
}