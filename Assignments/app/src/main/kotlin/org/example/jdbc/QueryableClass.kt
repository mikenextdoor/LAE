package org.example.jdbc

import Queryable
import reflection.buildDynamicClass
import java.sql.Connection
import kotlin.reflect.KClass

class QueryableClass<T: Any>(
    private val connection: Connection,
    private val tableName: String,
    private val clazz: KClass<T>
) {
    fun findAll(): Queryable<T> {
        return QueriesQueryable(
            connection,
            "SELECT * FROM ${tableName.uppercase()}",
            buildDynamicClass(clazz)
        )
    }
}