package org.pblue.asyncpools.tests

import org.pblue.asyncpools.testpool.ConvertingPool
import org.specs2.mutable._

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

class AsyncPoolsSpec extends Specification {

	val input = "whatever"
	val expectedOutput = "WHATEVER"

	private val pool = new ConvertingPool()

	private def await[T](f: Future[T]): T = Await.result(f, 5.seconds)

	private def convert(s: String) = pool.execute(converter => converter.convert(s))

	"Jobs" should {
		"be executed properly" in {
			await(convert(input)) === expectedOutput
		}
	}
}
