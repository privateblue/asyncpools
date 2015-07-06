package org.pblue.asyncpools

final case class Job[Resource, Result](fn: Resource => Result)