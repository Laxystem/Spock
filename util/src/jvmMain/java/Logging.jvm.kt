package quest.laxla.spock

import io.github.oshai.kotlinlogging.Level

internal object JulLogLevelManager : LogLevelManager.Hierarchal {
	override fun logLevelOf(name: String): Level {
		TODO("Not yet implemented")
	}

	override fun setLogLevelOf(
		name: String,
		level: Level?
	): Level? {
		TODO("Not yet implemented")
	}

	override var logLevel: Level
		get() = TODO("Not yet implemented")
		set(value) {}

}

internal object LogbackLogLevelManager : LogLevelManager.Hierarchal {
	override fun logLevelOf(name: String): Level {
		TODO("Not yet implemented")
	}

	override fun setLogLevelOf(
		name: String,
		level: Level?
	): Level? {
		TODO("Not yet implemented")
	}

	override var logLevel: Level
		get() = TODO("Not yet implemented")
		set(value) {}

}

internal object Slf4jLogLevelManager : LogLevelManager.Hierarchal {
	override fun logLevelOf(name: String): Level {
		Lo
	}

	override fun setLogLevelOf(
		name: String,
		level: Level?
	): Level? {
		TODO("Not yet implemented")
	}

	override var logLevel: Level
		get() = TODO("Not yet implemented")
		set(value) {}

}

internal actual fun LoggingDebugModeComponent.onInit(): DebugMode.Component {
	registerLogLevelManager {
		when (it::class.qualifiedName.substringAfter("io.github.oshai.kotlinlogging.").substringBefore(".")) {
			"jul" -> java.util.logging.LogManager.getLogManager().
			"logback" -> {}
			"slf4j" -> {}
			else -> null
		}
	}
}
