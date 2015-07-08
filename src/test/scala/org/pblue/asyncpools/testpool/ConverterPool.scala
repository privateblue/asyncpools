package org.pblue.asyncpools.testpool

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.util.Timeout
import org.pblue.asyncpools.WorkerPool

import scala.concurrent.duration._

class ConverterPool extends WorkerPool[Converter](
	"ConvertingPool",
	5,
	Timeout(500, TimeUnit.MILLISECONDS),
	1,
	5.minutes,
	new ConverterFactory,
	balancing = true
)(ActorSystem("TestActorSystem")) {

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