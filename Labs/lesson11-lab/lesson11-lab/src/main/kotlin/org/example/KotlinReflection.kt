package org.example

import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

fun Any?.membersToJson(): String {
    if (this == null) return "null"
    if (this is String) return "\"${slashFix(this)}\""
    if (this is Number || this is Boolean) return this.toString()
    if (this is Char) return "\"${slashFix(this.toString())}\""

    if (this is Iterable<*>) {
        return "[" + this.joinToString(",") { it.membersToJson() } + "]"
    }

    if (this is Array<*>) {
        return "[" + this.joinToString(",") { it.membersToJson() } + "]"
    }

    val KClass = this::class

    val properties = KClass.memberProperties.map {
        val value = it.call(this)
        "\"${it.name}\":${value.membersToJson()}"
    }

    val functions = KClass.memberFunctions
        .filter { it.parameters.size == 1 }
        .filter { it.returnType.toString() != "kotlin.Unit" }
        .filter { it.name != "toString" }
        .filter { it.name != "hashCode" }
        .filter { it.name != "equals" }
        .filter { !it.name.contains("component") }
        .map {
            val value = it.call(this)
            "\"${it.name}\":${value.membersToJson()}"
        }

    val all = (functions + properties).sorted()

    return "{${all.joinToString(",")}}"
}

private fun slashFix(string: String): String {
    return string
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
}