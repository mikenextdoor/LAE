package org.example

import EstatisticaAbusoBaseLine
import org.h2.jdbcx.JdbcDataSource
import org.junit.Test
import org.junit.BeforeClass
import java.sql.Connection
import kotlin.test.assertEquals

class EstatisticaAbusoBaseLineTest {

    companion object {
        val connection: Connection =
            JdbcDataSource().apply {
                setURL("jdbc:h2:mem:test_estatistica;DB_CLOSE_DELAY=-1")
                user = "sa"
                password = ""
            }.connection

        @BeforeClass
        @JvmStatic
        fun setup() {
            connection.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS ESTATISTICA_ABUSO (
                    EAbusiva BOOLEAN,
                    Total INT
                )
            """.trimIndent())

            connection.createStatement().execute("INSERT INTO ESTATISTICA_ABUSO VALUES (true, 5)")
            connection.createStatement().execute("INSERT INTO ESTATISTICA_ABUSO VALUES (false, 3)")
        }
    }

    @Test
    fun `test EstatisticaAbusoBaseLine toClassFormatter`() {
        val rs = connection.createStatement()
            .executeQuery("SELECT EAbusiva, Total FROM ESTATISTICA_ABUSO")

        val formatter = EstatisticaAbusoBaseLine()
        val result = formatter.toClassFormatter(rs)

        assertEquals(2, result.size)
        assertEquals(true, result[0].abusiva)
        assertEquals(5, result[0].total)
        assertEquals(false, result[1].abusiva)
        assertEquals(3, result[1].total)
    }
}