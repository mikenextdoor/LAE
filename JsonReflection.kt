package org.example

fun Any?.fieldsToJson(): String {
    if (this == null) return "null"
    if (this is String) return "\"${this.replace("\"", "\\\"")}\""
    if (this is Number || this is Boolean) return this.toString()

    val javaClass = this::class.java

    val fields = javaClass.declaredFields.map {
        it.isAccessible = true
        "\"${it.name}\":${it.get(this).fieldsToJson()}"
    }.joinToString(",")

    return "{$fields}"
}