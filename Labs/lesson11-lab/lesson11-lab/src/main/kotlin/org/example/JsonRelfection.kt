package org.example

fun Any?.fieldsToJson(): String {
    if (this == null) return "null"
    if (this is String) return "\"${slashFix(this)}\""
    if (this is Number || this is Boolean) return this.toString()
    if (this is Char) return "\"${slashFix(this.toString())}\""

    if (this is Iterable<*>) {
        return "[" + this.joinToString(",") { it.fieldsToJson() } + "]"
    }

    if (this is Array<*>) {
        return "[" + this.joinToString(",") { it.fieldsToJson() } + "]"
    }

    val KClass = this::class.java

    val fields = KClass.declaredFields
        .sortedBy { it.name }.map {
            it.isAccessible = true
            "\"${it.name}\":${it.get(this).fieldsToJson()}"
        }

    return "{${fields.joinToString(",")}}"

}

private fun slashFix(string: String): String {
    return string
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
}

fun main() {
    data class Person(val name: String, val age: Int)
    println(Person("Miguel", 19).fieldsToJson())
    data class Company(val name: String, val employees: Array<Person>)
    println(Company("Tesla", arrayOf(Person("Miguel", 19), Person("Helder Conduto", 52))).fieldsToJson())
}