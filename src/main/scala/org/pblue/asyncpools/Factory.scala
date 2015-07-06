package org.pblue.asyncpools

trait Factory[Resource] {
	def create: Resource
	def check(pooledObject: Resource): Option[Throwable]
}
