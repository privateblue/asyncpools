AsyncPools 0.0.1
================

AsyncPools is an Akka based asynchronous worker pool. 

The following example cover how to use it as a Slick query executor pool.

To create Slick executor pools, first add configuration to your application configuration:
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
import org.pblue.asyncpools.slick.SlickPoolFactory

object MySlickPools with SlickPoolFactory {

	val myReadPool = newSlickPool("my-read-pool")
	
	val myWritePool = newSlickPool("my-write-pool")
	
}
```
This creates two router actors: ```my-read-pool``` and ```my-write-pool```, each with as many routees as you configured as the size of the pool (5 and 2 in this case). Every such routee (or worker) has a connection to the database with the configured details (JDBC driver, connection url, user name, password).

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
		MySlickPools.myReadPool execute { implicit session =>
			Query(table).filter(_.id === id).map(_.name).first
		}
		
	def insert(id: Int, name: String) =
		MySlickPools.myWritePool execute { implicit session =>
			table.insert((id, name))
		}
		
}
```
Jobs are executed asynchronously, as AsyncPools always return ```Future```'s, and are executed in parallel, as they are passed to a pool of workers through a round-robin router. Thus you can basically contain blocking I/O in a separate thread pool, hide the synchronous nature of JDBC behind it, and continue coding in a reactive way in the rest of your application.

You can set a job timeout for every ```execute``` call by having an implicit value of type ```akka.util.Timeout``` in scope, or fall back to a default timeout configured per pool. 

As AsyncPools is based on Akka, you can add standard [Akka configuration](http://doc.akka.io/docs/akka/2.2.3/general/configuration.html) to further tweak AsyncPools. Here's an example of switching to a balancing dispatcher, so that all workers in a pool share the same mailbox:
```
akka {
	worker-dispatcher {
		type = BalancingDispatcher
	}

	actor.deployment {
		"/my-read-pool/*" {
			dispatcher = akka.worker-dispatcher
		}
	}
}
```
To create a pool of different objects, create a new implementation of a PoolableObjectFactory:
```scala
import org.pblue.asyncpools.PoolableObjectFactory

class MyResourceFactory extends PoolableObjectFactory[MyResource] {
	def create = new MyResource
}
```
It is recommended to extend the WorkerPoolFactory and add a factory method that creates instances of your pool, but not necessary. The below example shows the recommended way.
```scala
import org.pblue.asyncpools.WorkerPoolFactory

trait MyResourcePoolFactory extends WorkerPoolFactory {
	def newMyResourcePool(name: String) = newConfiguredPool(name)(config => new MyResourceFactory)
}
```
AsyncPools requires Akka 2.2, Slick, Typesafe Config, H2 1.3.167 (for its unit tests) and Specs 2.2.1.
