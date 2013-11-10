package org.pblue.asyncpools.slick

import scala.concurrent.Await
import scala.concurrent.duration._

import scala.slick.driver.H2Driver.simple._

import com.typesafe.config.{ Config, ConfigFactory }

object Testing {

	object FibTable extends Table[(Int, Int)]("fib") {
		def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
		def value = column[Int]("value")

		def * = id ~ value
		def forInsert = value
	}

	val data = {
		lazy val fibs: Stream[Int] = 
			0 #:: 1 #:: fibs.zip(fibs.tail).map(n => n._1 + n._2)
		fibs.take(20).toList
	}

	object ConnectionPools extends SlickPoolFactory {
		val testPool = newConfiguredSlickPool("test")
	}

	def initDb = 
		ConnectionPools.testPool execute { implicit session =>
			FibTable.ddl.create
			data.foreach { record =>
				FibTable.forInsert.insert(record)
			}
		}

	def main(args: Array[String]) = {
		println("Initializing database")
		Await.ready(initDb, 1 seconds)
		println("Database initialized")

		var count = 0

		while(true) {
			count = count + 1
			println(s"Querying all data for the ${count}. time:")
			ConnectionPools.testPool execute { implicit session =>
				val first = Query(FibTable).first
				println(s"First record is $first")
				println
			}
			Thread.sleep(1)
		}
	}

}