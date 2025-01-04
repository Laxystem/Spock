package quest.laxla.spock.math.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import quest.laxla.spock.math.Space

internal const val MathPackage = "quest.laxla.spock.math"
private const val SpaceTypeParameterName = "S"

public val VectorTypeParameter: TypeVariableName = TypeVariableName("V")
public val SpaceTypeParameter: TypeVariableName =
	TypeVariableName(SpaceTypeParameterName, Space::class.asClassName().parameterizedBy(VectorTypeParameter))

public fun ClassName.asSpaceTypeParameter(vector: TypeName = VectorTypeParameter): TypeVariableName =
	TypeVariableName(SpaceTypeParameterName, parameterizedBy(vector))

public fun VectorKClass.parameterized(space: TypeVariableName = SpaceTypeParameter): ParameterizedTypeName =
	asClassName().parameterizedBy(VectorTypeParameter, space)

public fun PropertySpec.Builder.parameterized(space: TypeVariableName = SpaceTypeParameter) {
	addTypeVariable(VectorTypeParameter)
	addTypeVariable(space)
}
