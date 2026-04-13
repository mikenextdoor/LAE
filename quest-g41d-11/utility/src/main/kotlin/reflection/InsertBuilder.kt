package reflection

import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

fun <T: Any> buildInsert(obj: T, tableName: String): Pair<String, List<Any?>> {
    val kClass = obj::class
    val properties = kClass.memberProperties

    val columns = properties.map {
        it.findAnnotation<Column>()?.columnName ?: it.name
    }

    val values = properties.map {
        it.getter.call(obj)
    }

    val sqlString = columns.joinToString(", ") { "?" }
    val sql = """
        INSERT INTO $tableName 
            (${columns.joinToString(", ")})
            VALUES ($sqlString)
    """.trimIndent()

    return Pair(sql, values)
}