package org.pblue.asyncpools

import scala.util.Try

import akka.actor.Actor

class Worker[T](objectFactory: PoolableObjectFactory[T]) extends Actor {

	private val pooledObject = objectFactory.create

	def receive = {
		case Job(fn: Function1[T, _]) => sender ! Try(fn(pooledObject))
	}

}