package org.example

import kotlin.reflect.KClass

// By default an Annotation in Kotlin is already Runtime Retention
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class ToJsonFormatter(
    val formatter: KClass<out (Any) -> String>
)