package org.pblue.asyncpools

import akka.actor.{ Actor, Status }

class Worker[T](objectFactory: PoolableObjectFactory[T]) extends Actor {

	private var pooledObject: Option[T] = None

	override def preStart {
		pooledObject = Some(objectFactory.create)
	}

	override def postStop {
		pooledObject.foreach(objectFactory.stop)
	}

	def receive = {
		case Job(fn: Function1[T, AnyRef]) =>
			val result =
				try {
					Status.Success(
						pooledObject
							.map(fn)
							.getOrElse(throw new AsyncPoolsException("Worker not initialized"))
					)
				} catch {
					case t: Throwable => Status.Failure(t)
				}
			sender ! result
	}

}