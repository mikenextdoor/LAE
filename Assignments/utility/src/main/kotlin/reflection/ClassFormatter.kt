package reflection

import java.sql.ResultSet
import kotlin.reflect.KClass

class ClassFormatter<T: Any>(clazz: KClass<T>) : AbstractClassFormatter<T>(clazz) {
    override fun toClassFormatter(rs: ResultSet): List<T> {
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