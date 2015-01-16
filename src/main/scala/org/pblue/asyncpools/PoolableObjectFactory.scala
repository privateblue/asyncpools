package org.pblue.asyncpools

trait PoolableObjectFactory[T] {
        def create: T
        def check(pooledObject: T): Unit // Throw an exception if pooled object is invalid
        def postCheck(pooledObject: T): Unit = check(pooledObject) // Additional check after using the pooledObject to see if it's still valid
}
