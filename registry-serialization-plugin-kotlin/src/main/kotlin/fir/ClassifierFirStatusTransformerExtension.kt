package quest.laxla.spock.serialization.plugin.kotlin.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.expressions.buildUnaryArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.expressions.builder.buildGetClassCall
import org.jetbrains.kotlin.fir.expressions.builder.buildResolvedQualifier
import org.jetbrains.kotlin.fir.extensions.FirStatusTransformerExtension
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.classLikeLookupTagIfAny
import org.jetbrains.kotlin.fir.types.coneTypeOrNull
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.name.Name
import quest.laxla.spock.serialization.plugin.kotlin.SerializableReferenceClass
import quest.laxla.spock.serialization.plugin.kotlin.classifierAnnotation
import quest.laxla.spock.serialization.plugin.kotlin.serializableAnnotation

class ClassifierFirStatusTransformerExtension(session: FirSession) : FirStatusTransformerExtension(session) {
	override fun needTransformStatus(declaration: FirDeclaration): Boolean =
		declaration is FirProperty && declaration.annotations.any {
			it.annotationTypeRef.coneTypeOrNull?.classLikeLookupTagIfAny?.classId == classifierAnnotation
		}

	override fun transformStatus(
		status: FirDeclarationStatus,
		property: FirProperty,
		containingClass: FirClassLikeSymbol<*>?,
		isLocal: Boolean
	): FirDeclarationStatus {
		if (containingClass == null) {
			property.isClassifiedAtTopLevel = true
			return status
		}

		val propertyType = property.returnTypeRef.coneTypeOrNull?.classLikeLookupTagIfAny?.classId ?: return status
		val serializer = SerializableReferenceClass[propertyType]?.noopSerializer

		if (serializer == null) {
			property.isReturnTypeUnclassifiable = true
			return status
		}

		val serializable = buildAnnotation {
			annotationTypeRef = buildResolvedTypeRef {
				coneType = serializableAnnotation.constructClassLikeType()
			}

			argumentMapping = buildAnnotationArgumentMapping {
				mapping[Name.identifier("with")] = buildGetClassCall {
					argumentList = buildUnaryArgumentList(
						buildResolvedQualifier {
							packageFqName = serializer.packageFqName
							relativeClassFqName = serializer.relativeClassName
						}
					)
				}
			}
		}

		property.replaceAnnotations(property.annotations + serializable)

		return super.transformStatus(status, property, containingClass, isLocal)
	}

}