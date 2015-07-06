package org.pblue.asyncpools

trait Factory[Resource] {
	def create: Resource
	def check(resource: Resource): Option[Throwable]
}
