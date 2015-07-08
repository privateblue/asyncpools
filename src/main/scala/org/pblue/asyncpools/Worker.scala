package org.pblue.asyncpools

import scala.util.{Failure, Try}

import akka.actor.Actor

/**
 * Workers are able to complete [[Job]]s using an internal [[Resource]] instance which they acquire via the
 * [[resourceFactory]]. This is to make sure that the same [[Resource]] reference does not get reused when the
 * actor receives a [[akka.actor.SupervisorStrategy.Restart]] event from its supervisor.
 * @param resourceFactory Used to acquire a new [[Resource]]  upon instantiation.
 * @tparam Resource The type of resource managed by this [[Worker]].
 */
final class Worker[Resource](resourceFactory: Factory[Resource]) extends Actor {

	private val pooledObject = resourceFactory.create

  def receive = {
    case Job(fn: Function1[Resource, _]) =>
	    val recievedMs = System.currentTimeMillis
	    val result = Try(fn(pooledObject))
	    result match {
		    case Failure(t) =>
			    resourceFactory.check(pooledObject).map(throw _)
		    case _ => ()
	    }
      sender ! JobCompletion(result, System.currentTimeMillis - recievedMs)

  }

}
