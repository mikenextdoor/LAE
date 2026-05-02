package reflection

import java.sql.ResultSet
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

abstract class AbstractClassFormatter<T: Any>(val clazz: KClass<T>) {
    private val constructor = clazz.constructors.first()
    private val columnNames = constructor.parameters.map { param ->
        val property = clazz.memberProperties
            .first { it.name == param.name }
        property.findAnnotation<Column>()?.columnName ?: param.name
    }


    fun toClassFormatter(rs: ResultSet): List<T> {
        val resultList = mutableListOf<T>()
        while (rs.next()) {
            val args = columnNames.map { columnName ->
                rs.getObject(columnName)
            }
            val obj = constructor.call(*args.toTypedArray())
            resultList.add(obj)
        }
        return resultList
    }
}

class ClassFormatter<T: Any>(clazz: KClass<T>) : AbstractClassFormatter<T>(clazz)