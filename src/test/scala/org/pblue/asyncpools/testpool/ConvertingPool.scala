package org.pblue.asyncpools.testpool

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.util.Timeout
import org.pblue.asyncpools.WorkerPool

import scala.concurrent.duration._

class ConvertingPool extends WorkerPool[Converter](
	"ConvertingPool",
	5,
	Timeout(500, TimeUnit.MILLISECONDS),
	1,
	5.minutes,
	new ConverterFactory,
	balancing = true
)(ActorSystem("TestActorSystem"))