package pt.isel

import org.example.classes.Interacao
import org.example.jdbc.InsertsJDBC
import org.example.reflection.InsertsReflection
import org.h2.jdbcx.JdbcDataSource
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.sql.Date
import java.sql.Connection
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime) // Measure execution time per operation
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
open class Bench {
    private val connection: Connection =
        JdbcDataSource().apply {
            setURL("jdbc:h2:mem:bench;DB_CLOSE_DELAY=-1")
        }.connection

    private val jdbc = InsertsJDBC(connection)
    private val reflect = InsertsReflection(connection)

    private val interacao =
        Interacao(1, Date.valueOf("2024-01-01"), "A", null, 1, true)

    init {
        connection.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS INTERACAO (
                IDInteracao INT,
                DataInteracao DATE,
                Texto VARCHAR(255),
                CedulaProfissionalM VARCHAR(50),
                IDUtilizador INT,
                EAbusiva BOOLEAN
            )
        """.trimIndent())
    }

    @Benchmark
    fun jdbcInsert() {
        connection.createStatement().execute("DELETE FROM INTERACAO")
        jdbc.insertInteracao(interacao)
    }

    @Benchmark
    fun reflectionInsert() {
        connection.createStatement().execute("DELETE FROM INTERACAO")
        reflect.insertInteracao(interacao)
    }
}