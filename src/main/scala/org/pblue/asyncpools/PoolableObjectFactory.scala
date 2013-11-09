package org.pblue.asyncpools

trait PoolableObjectFactory[+T] {
	def create: T
}