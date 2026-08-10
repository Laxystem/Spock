package quest.laxla.spock.registry.serialization.plugin

private data object PkgSrc

public val pluginName: String = PkgSrc::class.java.`package`.name.substringBefore(".plugin")

public val classifierAnnotationName: String = "$pluginName.Classifier"

public val boxSerializerName: String = "$pluginName.BoxSerializer"
public val keySerializerName: String = "$pluginName.KeySerializer"
public val registrationSerializerName: String = "$pluginName.RegistrationSerializer"

public val boxNoopSerializerName: String = "$pluginName.BoxNoopSerializer"
public val keyNoopSerializerName: String = "$pluginName.KeyNoopSerializer"
public val registrationNoopSerializerName: String = "$pluginName.RegistrationNoopSerializer"

private val registryPackage: String = pluginName.substringBefore(".serialization")

public val boxName: String = "$registryPackage.Box"
public val keyName: String = "$registryPackage.Key"
public val registrationName: String = "$registryPackage.Registration"
