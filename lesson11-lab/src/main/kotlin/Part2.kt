fun Any?.fieldsToJson(): String {
    if (this == null) return "null"

    if (this is String) return "\"${this.replace("\"", "\\\"")}\""

    if (this is Number || this is Boolean) return this.toString()

    if (this is Iterable<*>) {
        val elements = this.map { it.fieldsToJson() }.joinToString(",")
        return "[${elements}]"
    }

    val javaClass = this::class.java

    val fields = javaClass.declaredFields.filter{!it.isSynthetic}.map {
        it.isAccessible = true
        "\"${it.name}\":${it.get(this).fieldsToJson()}"
    }.joinToString(",")

    return "{$fields}"
}