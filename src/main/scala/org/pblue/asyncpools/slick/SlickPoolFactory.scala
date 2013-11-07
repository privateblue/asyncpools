package org.pblue.asyncpools.slick

import org.pblue.asyncpools.WorkerPoolFactory

trait SlickPoolFactory extends WorkerPoolFactory {

	def newSlickPool(name: String) = newConfiguredPool(name)(config => new SessionFactory(config))

}