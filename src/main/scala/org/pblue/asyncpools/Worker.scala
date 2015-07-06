package org.pblue.asyncpools

import scala.util.Try

import akka.actor.Actor

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
