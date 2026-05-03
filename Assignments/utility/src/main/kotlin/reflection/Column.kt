package reflection

// By default an Annotation in Kotlin is already Runtime Retention
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Column(
    val columnName: String,
)
