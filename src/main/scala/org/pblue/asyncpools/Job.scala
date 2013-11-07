package org.pblue.asyncpools

case class Job[T, U](fn: T => U)