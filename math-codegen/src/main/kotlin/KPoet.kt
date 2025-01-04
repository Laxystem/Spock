package quest.laxla.spock.math.codegen

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName

public inline fun file(packageName: String, name: String, block: FileSpec.Builder.() -> Unit): FileSpec =
	FileSpec.builder(packageName, name).apply(block).build()

public inline fun FileSpec.Builder.property(
	name: String,
	returnType: TypeName,
	vararg modifiers: KModifier,
	block: PropertySpec.Builder.() -> Unit,
): PropertySpec = PropertySpec.builder(name, returnType, *modifiers).apply(block).build().also(::addProperty)

public inline fun PropertySpec.Builder.getter(block: FunSpec.Builder.() -> Unit): FunSpec =
	FunSpec.getterBuilder().apply(block).build().also(::getter)

public inline fun FileSpec.Builder.function(name: String, block: FunSpec.Builder.() -> Unit): FunSpec =
	FunSpec.builder(name).apply(block).build().also(::addFunction)

public inline fun FunSpec.Builder.parameter(name: String, type: TypeName, block: ParameterSpec.Builder.() -> Unit = {}): ParameterSpec =
	ParameterSpec.builder(name, type).apply(block).build().also(::addParameter)