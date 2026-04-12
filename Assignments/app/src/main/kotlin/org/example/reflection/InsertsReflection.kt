package org.example.reflection

import org.example.classes.Interacao
import org.example.interfaces.*
import reflection.*
import java.sql.Connection
import java.sql.Types

class InsertsReflection(private val connection: Connection) : InsertsInterface {
    override fun insertInteracao(interacao: Interacao) {
        val (sql, values) = buildInsert(interacao, "INTERACAO")

        connection.prepareStatement(sql).use { ps ->
            values.forEachIndexed { index, value ->
                if (value != null) {
                    ps.setObject(index + 1, value)
                } else {
                    ps.setNull(index + 1, Types.NULL)
                }
            }
            ps.executeUpdate()
        }
    }
}