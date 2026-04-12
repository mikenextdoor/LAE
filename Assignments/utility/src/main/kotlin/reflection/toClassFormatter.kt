package reflection

import java.sql.ResultSet
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

fun <T: Any> toClassFormatter(rs: ResultSet, clazz: KClass<T>): List<T> {
    val resultList = mutableListOf<T>()
    val constructor = clazz.constructors.first()

    while (rs.next()) {
        val args = constructor.parameters.map { param ->
            val property = clazz.members
                .first { it.name == param.name }
            val columnName = property.findAnnotation<Column>()?.columnName ?: param.name

            rs.getObject(columnName)
        }

        val obj = constructor.call(*args.toTypedArray())
        resultList.add(obj)
    }

    return resultList
}