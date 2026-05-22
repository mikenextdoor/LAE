package pt.isel

import org.example.reflection.QueriesReflection
import org.example.jdbc.QueriesJDBC
import org.example.reflection.QueriesReflectionDynamic
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
    private val dynamic = QueriesReflectionDynamic(connection)

    init {
        connection.createStatement().execute("""
        INSERT INTO INTERACAO VALUES
        (1, DATE '2026-05-20', 'A', NULL, 1, TRUE),
        (2, DATE '2026-05-20', 'B', NULL, 1, FALSE),
        (3, DATE '2026-05-20', 'C', NULL, 1, TRUE),
        (4, DATE '2026-05-20', 'D', NULL, 1, FALSE),
        (5, DATE '2026-05-20', 'E', NULL, 1, TRUE)
    """.trimIndent())

        connection.createStatement().execute("""
        INSERT INTO CASOS_DE_CYBERBULLYING VALUES
        (1, DATE '2026-05-19', DATE '2026-05-20', 'DESCRIÇÃO', 'AREA DE ATUACAO', 'ANOTACOES', 1, DATE '2026-05-20', 'TEXTOAD', 'CEDULA'),
        (2, DATE '2026-05-19', DATE '2026-05-20', 'DESCRIÇÃO', 'AREA DE ATUACAO', 'ANOTACOES', 1, DATE '2026-05-20', 'TEXTOAD', 'CEDULA'),
        (1, DATE '2026-05-19', DATE '2026-05-20', 'DESCRIÇÃO', 'AREA DE ATUACAO', 'ANOTACOES', 1, DATE '2026-05-20', 'TEXTOAD', 'CEDULA'),
        (2, DATE '2026-05-19', DATE '2026-05-20', 'DESCRIÇÃO', 'AREA DE ATUACAO', 'ANOTACOES', 1, DATE '2026-05-20', 'TEXTOAD', 'CEDULA'),
        (1, DATE '2026-05-19', DATE '2026-05-20', 'DESCRIÇÃO', 'AREA DE ATUACAO', 'ANOTACOES', 1, DATE '2026-05-20', 'TEXTOAD', 'CEDULA'),
        (2, DATE '2026-05-19', DATE '2026-05-20', 'DESCRIÇÃO', 'AREA DE ATUACAO', 'ANOTACOES', 1, DATE '2026-05-20', 'TEXTOAD', 'CEDULA')
        """.trimIndent()
        )
    }

    @Benchmark
    fun queryJDBC() =
        jdbc.getCasoById(1)

    @Benchmark
    fun queryReflection() =
        reflect.getCasoById(1)

    @Benchmark
    fun queryDynamic() =
        dynamic.getCasoById(1)

}