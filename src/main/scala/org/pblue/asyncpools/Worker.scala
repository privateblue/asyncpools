package org.pblue.asyncpools

import scala.util.{Failure, Try}

import akka.actor.Actor

/**
 * Workers are able to complete [[Job]]s using an internal [[Resource]] instance which they acquire via the
 * [[resourceManager]]. This is to make sure that the same [[Resource]] reference does not get reused when the
 * actor receives a [[akka.actor.SupervisorStrategy.Restart]] event from its supervisor.
 * @param resourceManager Used to acquire a new [[Resource]]  upon instantiation.
 * @tparam Resource The type of resource managed by this [[Worker]].
 */
final class Worker[Resource](resourceManager: Manager[Resource]) extends Actor {

	private val pooledObject = resourceManager.create

	override def postStop(): Unit = resourceManager.destroy(pooledObject)

	def receive = {
    case Job(fn: Function1[Resource, _]) =>

	    val receivedMs = System.currentTimeMillis

	    val result = Try(fn(pooledObject))
	    val completion = JobCompletion(result, System.currentTimeMillis - receivedMs)

	    result match {
		    case Failure(t) =>
			    resourceManager.check(pooledObject).map { exception =>
				    resourceManager.destroy(pooledObject)
				    throw exception
			    }
		    case _ => ()
	    }
	    sender ! completion

  }

}
