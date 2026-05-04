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
                setURL("jdbc:h2:mem:benchdb;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:h2-init.sql'")
                user = "sa"
                password = ""
            }.connection
    }
    @Test
    fun `testInsertReflection`() {

        connection.createStatement().execute("DELETE FROM INTERACAO")

        val repo = InsertsReflection(connection)

        repo.insertInteracao(
            Interacao(1, Date.valueOf("2016-10-24"), "A", null, 1, true)
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
            Interacao(1, Date.valueOf("2016-10-24"), "A", null, 1, true)
        )

        inserts.insertInteracao(
            Interacao(2, Date.valueOf("2016-10-25"), "B", null, 1, false)
        )

        val result = queries.getInteracoesByUser(1)


        assertEquals(2, result.size)
    }
}