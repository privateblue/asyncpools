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

	var receivedCount = 0
	var successCount = 0
	var errorCount = 0
	var timerCount = 0

	def clearStats() = {
		receivedCount = 0
		successCount = 0
		errorCount = 0
		timerCount = 0
	}

	// make these accessible

	override def markJobReceived(): Unit = {
		receivedCount = receivedCount + 1
		super.markJobReceived()
	}

	override def markSuccess(): Unit = {
		successCount = successCount + 1
		super.markSuccess()
	}

	override def markFailure(): Unit = {
		errorCount = errorCount + 1
		super.markFailure()
	}

	override def markExecutionTime(executionTimeMs: Long): Unit = {
		timerCount = timerCount + 1
		super.markExecutionTime(executionTimeMs)
	}
}