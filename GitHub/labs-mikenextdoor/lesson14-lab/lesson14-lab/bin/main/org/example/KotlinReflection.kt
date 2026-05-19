package org.example

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.reflect.full.findAnnotation
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
        val annotation = it.findAnnotation<ToJsonFormatter>()
        val value = it.call(this)
        val jsonValue = if (annotation != null) {
            if (value == null) "null"
            else {
                val formatterClass = annotation.formatter
                val instance = formatterClass.constructors.first().call() as (Any) -> String
                "\"${instance(value)}\""
            }
        } else {
            value?.membersToJson() ?: "null"
        }
        "\"${it.name}\":${jsonValue}"
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

class DateFormatter : (Any) -> String {
    override fun invoke(value: Any): String {
        val date = value as LocalDate
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}

data class Student(
    val name: String,
    val age: Int,
    @ToJsonFormatter(DateFormatter::class)
    val enrollmentDate: LocalDate
)

class UpperCaseFormatter : (Any) -> String {
    override fun invoke(value: Any): String {
        val text = value.toString().uppercase()
        return text
    }
}

data class Person(
    @ToJsonFormatter(UpperCaseFormatter::class) val firstName: String,
    val lastName: String
)

fun main() {
    val student = Student("ZE", 20, LocalDate.of(2023, 3, 15))
    println(student.membersToJson())

    val person = Person("Miguel", "Silva")
    println(person.membersToJson())
}