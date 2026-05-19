package org.example.jdbc

import org.example.classes.*
import org.example.interfaces.*
import java.sql.Connection

class InsertsJDBC(
    private val connection: Connection,
) : InsertsInterface {
    override fun insertInteracao(interacao: Interacao) {
        val sql =
            """
            INSERT INTO INTERACAO
            (IDInteracao, DataInteracao, Texto, CedulaProfissionalM, IDUtilizador, EAbusiva)
            VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent()

        connection.prepareStatement(sql).use { ps ->

            ps.setInt(1, interacao.id)
            ps.setDate(2, interacao.data)
            ps.setString(3, interacao.texto)
            ps.setString(4, interacao.cedulaModerador)
            ps.setInt(5, interacao.idUtilizador)

            if (interacao.abusiva != null) {
                ps.setBoolean(6, interacao.abusiva)
            } else {
                ps.setNull(6, java.sql.Types.BOOLEAN)
            }

            ps.executeUpdate()
        }
    }
}
