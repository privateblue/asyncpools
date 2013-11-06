package org.pblue.asyncpools

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import org.specs2.mutable.Specification 

import scala.slick.driver.H2Driver.simple._

class WorkerSpec extends Specification with WorkerPoolFactory {

	private val testPool = newConfiguredPool("test")

	object FibTable extends Table[(Int, Int)]("fib") {
		def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
		def value = column[Int]("value")

		def * = id ~ value
		def forInsert = value
	}

	private val data = {
		lazy val fibs: Stream[Int] = 
			0 #:: 1 #:: fibs.zip(fibs.tail).map(n => n._1 + n._2)
		fibs.take(20).toList
	}

	private def setupDb(implicit session: Session) = {
		FibTable.ddl.create
		data.foreach { record =>
			FibTable.forInsert.insert(record)
		}
	}

	"execute" should {
		
		"retrieve previously stored data" in { 
			val storedFibs =
				testPool execute { implicit session => 
					setupDb

					Query(FibTable).list
				}

			val result = Await.result(storedFibs, Duration("1 seconds")) 
			val control = data.zipWithIndex.map { case (x, y) => (y + 1, x) }
			
			result === control
		}

		"wrap and re-throw exceptions thrown in worker" in {
			val res =
				testPool execute { implicit session =>
					Query(FibTable).filter(i => i.id === 30).first
				}
			Await.result(res, Duration("1 seconds")) must throwA[SlickpoolsException]
		}

	}

}