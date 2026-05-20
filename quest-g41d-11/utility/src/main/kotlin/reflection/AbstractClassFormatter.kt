package reflection

import java.sql.ResultSet
import java.sql.SQLException
import kotlin.jvm.Throws
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

abstract class AbstractClassFormatter<T: Any>(val clazz: KClass<T>) {
    val constructor = clazz.constructors.first()
    val columnNames = constructor.parameters.map { param ->
        val property = clazz.memberProperties
            .first { it.name == param.name }
        property.findAnnotation<Column>()?.columnName ?: param.name
    }

    @Throws (SQLException::class)
    abstract fun toClassFormatter(rs: ResultSet): List<T>

}


