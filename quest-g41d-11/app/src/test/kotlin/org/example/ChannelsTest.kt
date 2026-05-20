package pt.isel

import org.example.classes.*
import org.example.jdbc.InsertsJDBC
import org.example.jdbc.QueriesJDBC
import org.h2.jdbcx.JdbcDataSource
import java.sql.Connection
import kotlin.test.Test
import kotlin.test.assertEquals

class ChannelsTest {
    companion object {
        private val connection: Connection =
            JdbcDataSource() // Create an H2 in-memory DataSource and initialise the schema + seed data
                .apply {
                    setURL("jdbc:h2:mem:benchdb;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:h2-init.sql'")
                    user = "sa"
                    password = ""
                }.connection
    }

    @Test
    fun `check items from channels table`() {
        val inserts = InsertsJDBC(connection)
        val queries = QueriesJDBC(connection)

        inserts.insertInteracao(
            Interacao(
                id = 1,
                data = java.sql.Date.valueOf("2026-04-11"),
                texto = "Test",
                cedulaModerador = null,
                idUtilizador = 1,
                abusiva = true
            )
        )

        val result = queries.getInteracoesByUser(1)

        val stats = queries.getNumeroInteracoesAbusivas()
        assertEquals(1, stats.size)

        assertEquals(1, result.size)
        assertEquals(1, result[0].id)
        assertEquals(true, result[0].abusiva)
    }
}