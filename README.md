AsyncPools 0.0.1
================

AsyncPools is an Akka based asynchronous worker pool.

To create pools, first add configuration to your application configuration:
```
asyncpools {
	my-read-pool {
		size = 5
		defaultTimeout = "2 seconds"
		url = "jdbc:h2:mem:testDB;DATABASE_TO_UPPER=false"
		user = ""
		password = ""
		driver = "org.h2.Driver"
	}
	
	my-write-pool {
		size = 2
		defaultTimeout = "10 seconds"
		url = "jdbc:h2:mem:testDB;DATABASE_TO_UPPER=false"
		user = ""
		password = ""
		driver = "org.h2.Driver"
	}
}
```
After that, you can instantiate these pools in your code:
```scala
import org.pblue.asyncpools.WorkerPoolFactory

object MyPools with WorkerPoolFactory {

	val myReadPool = newConfiguredPool("my-read-pool")
	
	val myWritePool = newConfiguredPool("my-write-pool")
	
}
```
This initializes an Akka actor system, and creates two router actors: ```my-read-pool``` and ```my-write-pool```, each with as many routees as you configured as the size of the pool (5 and 2 in this case). Every such routee (or worker) has a connection to the database with the configured details (JDBC driver, connection url, user name, password).

After setting up the pools, you can send some work to them:
```scala
import scala.slick.driver.H2Driver.simple._

object MyRepository {

	val table = new Table[(Int, String)]("my_table") {
		def id = column[Int]("id", O.PrimaryKey)
		def name = column[String]("name")
		def * = id ~ name
	}

	def getName(id: Int): Future[String] =
		MyPools.myReadPool execute { implicit session =>
			Query(table).filter(_.id === id).map(_.name).first
		}
		
	def insert(id: Int, name: String) =
		MyPools.myWritePool execute { implicit session =>
			table.insert((id, name))
		}
		
}
```
Jobs are executed asynchronously, as Slickpools always return ```Future```'s, and are executed in parallel, as they are passed to a pool of workers through a round-robin router. Thus you can basically contain blocking I/O in a separate thread pool, hide the synchronous nature of JDBC behind it, and continue coding in a reactive way in the rest of your application.

You can set a job timeout for every ```Pool.execute``` call by having an implicit value of type ```akka.util.Timeout``` in scope, or fall back to a default timeout configured per pool. 

Slickpools requires Akka 2.2.1, Slick 1.0.1, Typesafe Config 1.0.0, H2 1.3.167 (for its unit tests) and Specs 2.2.1.
