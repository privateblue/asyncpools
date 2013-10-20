package org.pblue.slickpools

import com.typesafe.config.Config

trait PoolProvider {
	val configRoot = "slickpool"

	def newConfiguredPool(name: String)(implicit config: Config): Pool
}