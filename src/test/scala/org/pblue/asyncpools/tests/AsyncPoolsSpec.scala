package org.pblue.asyncpools.tests

import org.pblue.asyncpools.testpool.ConverterProxy
import org.specs2.execute.{Result, AsResult}

import org.specs2.mutable._
import org.specs2.specification.ForEach

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

class AsyncPoolsSpec extends Specification with PoolContext {

	val input = "whatever"
	val expectedOutput = "WHATEVER"

	private def await[T](f: Future[T]): T = Await.result(f, 5.seconds)

	private def convert(s: String)(implicit pool: ConverterProxy) = pool.execute(converter => converter.convert(s))

	"Jobs" should {
		"return the expected result when executed without errors" in { implicit pool: ConverterProxy =>
			await(convert(input)) === expectedOutput
		}

		"collect metrics when a single job is executed successfully" in { implicit pool: ConverterProxy =>
			await(convert(input)) === expectedOutput

			pool.receivedCount === 1
			pool.successCount === 1
			pool.timerCount === 1
		}

		"collect metrics when multiple jobs are executed successfully" in { implicit pool: ConverterProxy =>
			await(convert(input)) === expectedOutput
			await(convert(input)) === expectedOutput

			pool.receivedCount === 2
			pool.successCount === 2
			pool.timerCount === 2
		}

		"reported job completion time must be in the expected interval" in { implicit pool: ConverterProxy =>
			await(pool.execute{converter =>
				val res = converter.convert(input)
				Thread.sleep(200)
				res}) === expectedOutput

			pool.jobDurationSum must beGreaterThanOrEqualTo(200L)
			pool.jobDurationSum must beLessThanOrEqualTo(220L)
		}

		"collect metrics on errors" in { implicit pool: ConverterProxy =>
			Try(await(convert("fail"))).isFailure === true

			pool.receivedCount === 1
			pool.errorCount === 1
			pool.timerCount === 1
		}

		"be able to handle bursts of jobs" in { implicit pool: ConverterProxy =>
			val jobCount: Int = 500

			await(Future.sequence(1.to(jobCount).toList.map{ _ =>
				convert(input)
			})).forall(_ === expectedOutput)

			pool.receivedCount === jobCount
			pool.successCount === jobCount
			pool.timerCount === jobCount
		}

	}
	
}

trait PoolContext extends ForEach[ConverterProxy] {
	override protected def foreach[R: AsResult](f: (ConverterProxy) => R): Result = {
		AsResult(f(new ConverterProxy()))
	}
}