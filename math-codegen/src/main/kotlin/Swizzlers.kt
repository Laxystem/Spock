package quest.laxla.spock.math.codegen

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import kotlin.math.max

private fun FileSpec.Builder.swizzler(tier: VectorTier, elements: List<String>) {
	property(
		name = elements.joinToString(separator = "").lowercase(),
		returnType = VectorTier.entries[elements.lastIndex].exactClass.parameterized()
	) {
		receiver(tier.minimumClass.parameterized())
		parameterized()

		getter {
			addModifiers(KModifier.INLINE)
			addStatement(elements.joinToString(prefix = "return space.vectorOf(", separator = ", ", postfix = ")"))
		}
	}
}

public fun generateSwizzlers(): FileSpec = file(MathPackage, "Swizzlers") {
	for (first in VectorTier.entries) for (second in VectorTier.entries) {
		val tier = VectorTier.entries[max(first.ordinal, second.ordinal)]
		val elements = listOf(first.property.name, second.property.name)

		swizzler(tier, elements)

		for (third in VectorTier.entries) {
			val tier = VectorTier.entries[max(tier.ordinal, third.ordinal)]
			val elements = elements + third.property.name

			swizzler(tier, elements)

			for (fourth in VectorTier.entries) swizzler(
				tier = VectorTier.entries[max(tier.ordinal, fourth.ordinal)],
				elements = elements + fourth.property.name
			)
		}
	}
}
