package org.pblue.asyncpools.testpool

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.util.Timeout
import org.pblue.asyncpools.{ExecutorProxy, Master}

import scala.concurrent.duration._


object ActorFactory {
	def testRef = new Master[Converter](UUID.randomUUID().toString,
		5,
		1,
		5.minutes,
		new ConverterManager,
		true)(system).routerRef

	private val system = ActorSystem("TestSystem")
}

class ConverterProxy
	extends
	ExecutorProxy[Converter](ActorFactory.testRef)(Timeout(500,
	TimeUnit.MILLISECONDS),
    scala.concurrent.ExecutionContext.Implicits.global) {

	// These are here because Mockito allows querying call counts only on functions which return AnyRef

	var receivedCount = 0
	var successCount = 0
	var errorCount = 0
	var timerCount = 0
	var jobDurationSum = 0L

	override def markJobReceived(): Unit = this.synchronized {
		receivedCount = receivedCount + 1
		super.markJobReceived()
	}

	override def markSuccess(): Unit = this.synchronized {
		successCount = successCount + 1
		super.markSuccess()
	}

	override def markFailure(): Unit = this.synchronized {
		errorCount = errorCount + 1
		super.markFailure()
	}

	override def markExecutionTime(executionTimeMs: Long): Unit = this.synchronized {
		timerCount = timerCount + 1
		jobDurationSum = jobDurationSum + executionTimeMs
		super.markExecutionTime(executionTimeMs)
	}
}