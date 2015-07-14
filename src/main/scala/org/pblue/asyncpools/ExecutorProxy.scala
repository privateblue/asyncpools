package org.pblue.asyncpools

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class ExecutorProxy[Resource](executor: ActorRef)(defaultTimeout: Timeout, completionHandler: ExecutionContext) {

	protected def markJobReceived(): Unit = ()

	protected def markSuccess(): Unit = ()

	protected def markFailure(): Unit = ()

	protected def markExecutionTime(executionTimeMs: Long): Unit = ()

	def execute[Result](fn: Resource => Result)(implicit timeout: Timeout = defaultTimeout): Future[Result] = {
		markJobReceived()
		ask(executor, Job[Resource, Result](fn)).flatMap {
			case JobCompletion(result: Try[Result]@unchecked, durationMs) =>
				result match {
					case Success(res) =>
						markExecutionTime(durationMs)
						markSuccess()
						Future.successful(res)
					case Failure(t) =>
						markExecutionTime(durationMs)
						markFailure()
						Future.failed(new AsyncPoolsException("AsyncPools execution error", t))
				}
			case _ =>
				Future.failed(new AsyncPoolsException("Invalid message received from worker!"))
		}(completionHandler)
	}
}
