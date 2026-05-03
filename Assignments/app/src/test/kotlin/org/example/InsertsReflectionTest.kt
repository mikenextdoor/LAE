package org.example

import org.example.classes.Interacao
import org.example.reflection.InsertsReflection
import org.example.reflection.QueriesReflection
import org.h2.jdbcx.JdbcDataSource
import java.sql.Connection
import java.sql.Date
import kotlin.test.assertEquals
import kotlin.test.Test

class InsertsReflectionTest {

    companion object {
        val connection: Connection =
            JdbcDataSource().apply {
                setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                user = "sa"
                password = ""
            }.connection

        init {
            connection.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS INTERACAO (
                    IDInteracao INT PRIMARY KEY,
                    DataInteracao DATE,
                    Texto VARCHAR(255),
                    CedulaProfissionalM VARCHAR(50),
                    IDUtilizador INT,
                    EAbusiva BOOLEAN
                )
            """.trimIndent())
        }
    }
    @Test
    fun `testInsertReflection`() {

        connection.createStatement().execute("DELETE FROM INTERACAO")

        val repo = InsertsReflection(connection)

        repo.insertInteracao(
            Interacao(1, Date.valueOf("2024-01-01"), "A", null, 1, true)
        )

        val rs = connection.createStatement().executeQuery("SELECT * FROM INTERACAO")
        rs.next()

        assertEquals(1, rs.getInt("IDInteracao"))
    }

    @Test
    fun `testQueriesReflection`() {

        connection.createStatement().execute("DELETE FROM INTERACAO")

        val inserts = InsertsReflection(connection)
        val queries = QueriesReflection(connection)

        inserts.insertInteracao(
            Interacao(1, Date.valueOf("2024-01-01"), "A", null, 1, true)
        )

        inserts.insertInteracao(
            Interacao(2, Date.valueOf("2024-01-02"), "B", null, 1, false)
        )

        val result = queries.getInteracoesByUser(1)


        assertEquals(2, result.size)
    }
}