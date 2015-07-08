package org.pblue.asyncpools.tests

import org.pblue.asyncpools.testpool.ConverterPool
import org.specs2.execute.{Result, AsResult}

import org.specs2.mutable._
import org.specs2.specification.ForEach

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.util.Try

class AsyncPoolsSpec extends Specification with PoolContext {

	val input = "whatever"
	val expectedOutput = "WHATEVER"

	private def await[T](f: Future[T]): T = Await.result(f, 5.seconds)

	private def convert(s: String)(implicit pool: ConverterPool) = pool.execute(converter => converter.convert(s))

	"Jobs" should {
		"return the expected result when executed without errors" in { implicit pool: ConverterPool =>
			await(convert(input)) === expectedOutput
		}

		"metrics should be collected when a single job is executed successfully" in { implicit pool: ConverterPool =>
			await(convert(input)) === expectedOutput

			pool.receivedCount === 1
			pool.successCount === 1
			pool.timerCount === 1
		}

		"metrics should be collected when multiple jobs are executed successfully" in { implicit pool: ConverterPool =>
			await(convert(input)) === expectedOutput
			await(convert(input)) === expectedOutput

			pool.receivedCount === 2
			pool.successCount === 2
			pool.timerCount === 2
		}

		"metrics should be collected on errors" in { implicit pool: ConverterPool =>
			Try(await(convert("fail"))).isFailure === true

			pool.receivedCount === 1
			pool.errorCount === 1
			pool.timerCount === 1
		}

	}
	
}

trait PoolContext extends ForEach[ConverterPool] {
	override protected def foreach[R: AsResult](f: (ConverterPool) => R): Result = {
		AsResult(f(new ConverterPool()))
	}
}