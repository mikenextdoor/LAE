package org.example

import org.example.classes.*
import org.example.jdbc.InsertsJDBC
import org.example.jdbc.QueriesJDBCSequence
import org.example.jdbc.QueriesQueryable
import org.example.jdbc.QueryableClass
import org.h2.jdbcx.JdbcDataSource
import reflection.buildDynamicClass
import java.sql.Connection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ChannelsSequenceTest {
    companion object {
        private val connection: Connection =
            JdbcDataSource()
                .apply {
                    setURL("jdbc:h2:mem:benchdb;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:h2-init.sql'")
                    user = "sa"
                    password = ""
                }.connection
    }

    @Test
    fun `check items from channels table using sequences`() {
        connection.createStatement().execute("DELETE FROM INTERACAO")
        val inserts = InsertsJDBC(connection)
        val queries = QueriesJDBCSequence(connection)

        inserts.insertInteracao(
            Interacao(
                id = 1,
                data = java.sql.Date.valueOf("2026-05-23"),
                texto = "Test",
                cedulaModerador = "null",
                idUtilizador = 1,
                abusiva = true
            )
        )

        val result = queries.findInteracoesByUser(1).toList()
        val stats = queries.findNumeroInteracoesAbusivas().toList()

        assertEquals(1, stats.size)
        assertEquals(1, result.size)
        assertEquals(1, result[0].id)
        assertEquals(true, result[0].abusiva)
    }

    @Test
    fun `check if sequence works as expected`() {
        connection.createStatement().execute("DELETE FROM INTERACAO")

        val inserts = InsertsJDBC(connection)
        val queries = QueriesJDBCSequence(connection)

        inserts.insertInteracao(
            Interacao(
                id = 2,
                data = java.sql.Date.valueOf("2026-05-23"),
                texto = "Test",
                cedulaModerador = "null",
                idUtilizador = 1,
                abusiva = true
            )
        )
        inserts.insertInteracao(
            Interacao(
                id = 3,
                data = java.sql.Date.valueOf("2026-05-23"),
                texto = "Test",
                cedulaModerador = "null",
                idUtilizador = 2,
                abusiva = true
            )
        )
        inserts.insertInteracao(
            Interacao(
                id = 4,
                data = java.sql.Date.valueOf("2026-05-24"),
                texto = "Test",
                cedulaModerador = "null",
                idUtilizador = 2,
                abusiva = true
            )
        )

        val iterator = queries.findInteracoesByUser(2).iterator()

        val size = queries.findInteracoesByUser(2).count()

        assertEquals(2, size) //verificar se o size esta bem

        assertEquals(3, iterator.next().id)
        assertEquals(4, iterator.next().id)
        assertFalse { iterator.hasNext() }

        if (iterator.hasNext()) {
            println(iterator.next())
        } else {
            println("Nao ha mais elementos")
        }
    }

    @Test
    fun `test whereEquals`() {
        connection.createStatement().execute("DELETE FROM INTERACAO")

        val inserts = InsertsJDBC(connection)
        inserts.insertInteracao(Interacao(id = 1, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test", cedulaModerador = "null", idUtilizador = 1, abusiva = true))
        inserts.insertInteracao(Interacao(id = 2, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test", cedulaModerador = "null", idUtilizador = 2, abusiva = true))

        val queryable = QueriesQueryable(
            connection,
            "SELECT * FROM INTERACAO",
            buildDynamicClass(Interacao::class)
        ).whereEquals(Interacao::idUtilizador, 1)

        val iterator = queryable.iterator()

        assertEquals(1, iterator.next().id)
        assertFalse { iterator.hasNext() }
    }

    @Test
    fun `test orderBy`() {
        connection.createStatement().execute("DELETE FROM INTERACAO")

        val inserts = InsertsJDBC(connection)
        inserts.insertInteracao(Interacao(id = 3, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test", cedulaModerador = "null", idUtilizador = 1, abusiva = true))
        inserts.insertInteracao(Interacao(id = 1, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test", cedulaModerador = "null", idUtilizador = 1, abusiva = true))
        inserts.insertInteracao(Interacao(id = 2, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test", cedulaModerador = "null", idUtilizador = 1, abusiva = true))

        val iterator = QueriesQueryable(
            connection,
            "SELECT * FROM INTERACAO",
            buildDynamicClass(Interacao::class)
        ).orderBy(Interacao::id).iterator()

        assertEquals(1, iterator.next().id)
        assertEquals(2, iterator.next().id)
        assertEquals(3, iterator.next().id)
        assertFalse { iterator.hasNext() }
    }

    @Test
    fun `test findAll , whereEquals and orderBy`() {
        connection.createStatement().execute("DELETE FROM INTERACAO")

        val inserts = InsertsJDBC(connection)
        inserts.insertInteracao(Interacao(id = 3, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test", cedulaModerador = "null", idUtilizador = 1, abusiva = true))
        inserts.insertInteracao(Interacao(id = 1, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test diferente", cedulaModerador = "null", idUtilizador = 1, abusiva = true))
        inserts.insertInteracao(Interacao(id = 2, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test", cedulaModerador = "null", idUtilizador = 1, abusiva = true))

        val queries = QueryableClass(connection, "INTERACAO", Interacao::class)

        val iterator = queries.findAll()

        assertEquals(3, iterator.count())

        //whereEquals

        val equals = iterator.whereEquals(Interacao::texto, "Test")

        assertEquals(2, equals.count())

        assertEquals(1, iterator.whereEquals(Interacao::texto, "Test diferente").count())

        //orderBy
        val order = equals.orderBy(Interacao::id)

        val orderIterator = order.iterator()
        assertEquals(2, orderIterator.next().id)
        assertEquals(3, orderIterator.next().id)
        assertFalse { orderIterator.hasNext() }

    }

    @Test
    fun `pipeline test`() {
        connection.createStatement().execute("DELETE FROM INTERACAO")

        val inserts = InsertsJDBC(connection)
        inserts.insertInteracao(Interacao(id = 3, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test", cedulaModerador = "null", idUtilizador = 1, abusiva = true))
        inserts.insertInteracao(Interacao(id = 1, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test diferente", cedulaModerador = "null", idUtilizador = 1, abusiva = true))
        inserts.insertInteracao(Interacao(id = 2, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test", cedulaModerador = "null", idUtilizador = 1, abusiva = true))
        inserts.insertInteracao(Interacao(id = 4, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test", cedulaModerador = "null", idUtilizador = 1, abusiva = true))
        inserts.insertInteracao(Interacao(id = 5, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test diferente", cedulaModerador = "null", idUtilizador = 1, abusiva = true))
        inserts.insertInteracao(Interacao(id = 6, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test", cedulaModerador = "null", idUtilizador = 1, abusiva = true))
        inserts.insertInteracao(Interacao(id = 9, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test", cedulaModerador = "null", idUtilizador = 1, abusiva = true))
        inserts.insertInteracao(Interacao(id = 8, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test diferente", cedulaModerador = "null", idUtilizador = 1, abusiva = true))
        inserts.insertInteracao(Interacao(id = 7, data = java.sql.Date.valueOf("2026-05-23"), texto = "Test", cedulaModerador = "null", idUtilizador = 1, abusiva = true))

        val queries = QueryableClass(connection, "INTERACAO", Interacao::class)

        val pipeline = queries
            .findAll()
            .whereEquals(Interacao::texto, "Test diferente")
            .orderBy(Interacao::id)

        pipeline.forEach { println(it) }

        val pipelineIterator = pipeline.iterator()

        assertEquals(1, pipelineIterator.next().id)
        assertEquals(5, pipelineIterator.next().id)
        assertEquals(8, pipelineIterator.next().id)
        assertFalse { pipelineIterator.hasNext() }
    }
}