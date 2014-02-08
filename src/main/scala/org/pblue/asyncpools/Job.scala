package org.pblue.asyncpools

case class Job[T, U <: AnyRef](fn: T => U)