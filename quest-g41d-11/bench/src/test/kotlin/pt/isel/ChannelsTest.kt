package pt.isel

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
        val actual = getChannelsNames(connection)
        val expected =
            listOf(
                "General",
                "Development",
                "Support",
                "Gaming Chat",
                "Esports Discussion",
            )
        assertEquals(expected, actual)
    }
}
