package org.pblue.asyncpools

trait Factory[T] {
        def create: T
        def check(pooledObject: T): Unit // Throw an exception if pooled object is invalid
}
