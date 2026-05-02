package reflection

import java.sql.ResultSet
import java.sql.SQLException
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

interface ClassFormatter<T> {
    @Throws(SQLException::class)
    fun mapFrom(rs: ResultSet): List<T>
}

class ReflectionFormatter<T: Any>(private val clazz: KClass<T>): ClassFormatter<T> {
    private val constructor = clazz.constructors.first()
    private val columnNames = constructor.parameters.map { param ->
        val property = clazz.memberProperties.first { it.name == param.name }
        property.findAnnotation<Column>()?.columnName ?: param.name
    }

    override fun mapFrom(
        rs: ResultSet
    ): List<T> {
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