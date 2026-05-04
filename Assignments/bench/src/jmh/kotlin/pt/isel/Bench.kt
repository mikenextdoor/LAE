package pt.isel

import org.example.reflection.QueriesReflection
import org.example.jdbc.QueriesJDBC
import org.h2.jdbcx.JdbcDataSource
import org.openjdk.jmh.annotations.*
import java.sql.Connection
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
open class Bench {

    private val connection: Connection =
        JdbcDataSource().apply {
            setURL("jdbc:h2:mem:bench;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:h2-init.sql'")
        }.connection

    private val jdbc = QueriesJDBC(connection)
    private val reflect = QueriesReflection(connection)

    init {
        connection.createStatement().execute("""
            INSERT INTO INTERACAO VALUES
            (1, DATE '2026-05-04', 'A', NULL, 1, TRUE),
            (2, DATE '2026-05-04', 'B', NULL, 1, FALSE)
        """.trimIndent())
    }

    @Benchmark
    fun queryJDBC() =
        jdbc.getInteracoesByUser(1)

    @Benchmark
    fun queryReflection() =
        reflect.getInteracoesByUser(1)
}