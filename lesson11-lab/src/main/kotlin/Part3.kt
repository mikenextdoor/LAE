import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

fun Any?.membersToJson(): String {
    if (this == null) return "null"
    if (this is String) return "\"${this.replace("\"", "\\\"")}\""
    if (this is Number || this is Boolean) return this.toString()

    if (this is Iterable<*>) {
        val elements = this.map {it.membersToJson()}.joinToString(",")
        return "[$elements]"
    }

    val Kclass = this::class

    val properties = Kclass.memberProperties.sortedBy { it.name }.map {
        val value = it.getter.call(this)
        "\"${it.name}\": ${value.membersToJson()}"
    }

    val functions = Kclass.memberFunctions
        .filter { it.parameters.size == 1 }
        .filter { it.returnType.classifier != Unit::class }
        .filter { it.name != "toString" && it.name != "hashCode" && it.name != "equals" }
        .map {
            val value = it.call(this)
            "\"${it.name}\": ${value.membersToJson()}"
        }

    return "{${(properties + functions).joinToString(",")}}"
}