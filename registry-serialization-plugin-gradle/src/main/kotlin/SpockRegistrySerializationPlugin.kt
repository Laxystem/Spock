package quest.laxla.spock.serialization.plugin.gradle

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import quest.laxla.spock.registry.serialization.plugin.pluginName
import java.util.*

public class SpockRegistrySerializationPlugin : KotlinCompilerPluginSupportPlugin {
	private val properties = Properties().apply {
		load(javaClass.getResourceAsStream("metadata.properties"))
	}

	override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
		groupId = properties["group"].toString(),
		artifactId = properties["artifact"].toString(),
		version = properties["version"].toString()
	)

	override fun getCompilerPluginId(): String = pluginName

	override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

	override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> =
		kotlinCompilation.target.project.provider { emptyList() }
}