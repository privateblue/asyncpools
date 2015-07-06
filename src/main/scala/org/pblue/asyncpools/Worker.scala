package org.pblue.asyncpools

import scala.util.Try

import akka.actor.Actor

/**
 * Workers are able to complete [[Job]]s using an internal [[Resource]] instance.
 * @param resourceFactory
 * @tparam Resource
 */
final class Worker[Resource](resourceFactory: Factory[Resource]) extends Actor {

	private val pooledObject = resourceFactory.create

  def receive = {
    case Job(fn: Function1[Resource, _]) => {
      resourceFactory.check(pooledObject)
      sender ! Try(fn(pooledObject))
	    resourceFactory.check(pooledObject)
    }
  }

}
