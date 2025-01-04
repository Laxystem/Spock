package quest.laxla.spock.math.codegen

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getKotlinClassByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import kotlin.reflect.KClass

@OptIn(KspExperimental::class)
public fun Resolver.getDeclarationOf(kClass: KClass<*>): KSClassDeclaration? =
	kClass.qualifiedName?.let(::getKotlinClassByName)

@OptIn(KspExperimental::class)
public inline fun <reified A : Annotation> KSAnnotated.getAnnotations(): Sequence<A> = getAnnotationsByType(A::class)

@OptIn(KspExperimental::class)
public fun <A : Annotation> KSValueParameter.getAllAnnotationsByType(annotationKClass: KClass<A>): Sequence<A> =
	getAnnotationsByType(annotationKClass) + type.getAllAnnotationsByType(annotationKClass)

@OptIn(KspExperimental::class)
public fun <A : Annotation> KSTypeReference.getAllAnnotationsByType(annotationKClass: KClass<A>): Sequence<A> =
	getAnnotationsByType(annotationKClass) + resolve().declaration.getAllAnnotationsByType(annotationKClass)

@OptIn(KspExperimental::class)
public fun <A : Annotation> KSAnnotated.getAllAnnotationsByType(annotationKClass: KClass<A>): Sequence<A> = when(this) {
	is KSTypeReference -> getAllAnnotationsByType(annotationKClass)
	is KSValueParameter -> getAllAnnotationsByType(annotationKClass)
	else -> getAnnotationsByType(annotationKClass)
}

public inline fun <reified A : Annotation> KSAnnotated.getAllAnnotations(): Sequence<A> = getAllAnnotationsByType(A::class)