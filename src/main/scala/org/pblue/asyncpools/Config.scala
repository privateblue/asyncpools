package org.pblue.asyncpools

import com.typesafe.config.{ ConfigFactory, ConfigException }

trait Config {

	implicit class WrappedConfig(config: com.typesafe.config.Config) {
		def get(key: String, orElse: =>String) =
			try {
				config.getString(key)
			} catch {
				case ce: ConfigException => orElse
			}
	}

	protected lazy val appConfig = 
		try {
			ConfigFactory.load
		} catch {
			case ce: ConfigException => throw new AsyncPoolsException("Configuration failed to load", ce)
		}

	private val configRoot = "asyncpools"

	def poolConfig(name: String) = 
		try {
			appConfig.getConfig(s"${configRoot}.${name}")
		} catch {
			case ce: ConfigException => 
				throw new AsyncPoolsException(s"Missing configuration of pool $name", ce)
		}

}