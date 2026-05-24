package org.example.jdbc

import Queryable
import reflection.AbstractClassFormatter
import reflection.Column
import reflection.buildDynamicClass
import java.sql.Connection
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

class QueriesQueryable<T: Any>(
    private val connection: Connection,
    private val sql: String,
    private val formatter: AbstractClassFormatter<T>
): Queryable<T> {
    override fun <V> whereEquals(prop: KProperty1<T, V>, value: V): Queryable<T> {
        val columnName = prop.findAnnotation<Column>()?.columnName ?: prop.name
        val newSql = "$sql WHERE $columnName = '$value'"
        return QueriesQueryable(connection, newSql, formatter)
    }

    override fun <V> orderBy(prop: KProperty1<T, V>): Queryable<T> {
        val columnName = prop.findAnnotation<Column>()?.columnName ?: prop.name
        val newSql = "$sql ORDER BY $columnName"
        return QueriesQueryable(connection, newSql, formatter)
    }

    /**
     * Returns an [Iterator] that returns the values from the sequence.
     *
     * Throws an exception if the sequence is constrained to be iterated once and `iterator` is invoked the second time.
     */
    override fun iterator(): Iterator<T> {
        val ps = connection.prepareStatement(sql)
        val rs = ps.executeQuery()
        return object : Iterator<T> {
            private var nextReady = false
            override fun hasNext(): Boolean {
                if (nextReady) return true
                nextReady = rs.next()
                return nextReady
            }
            override fun next(): T {
                if (!nextReady && !hasNext()) {
                    throw NoSuchElementException()
                }
                nextReady = false
                val args = formatter.columnNames.map { columnName ->
                    rs.getObject(columnName)
                }
                return formatter.constructor.call(*args.toTypedArray())
            }
        }
    }
}