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
        /*connection.createStatement().execute("""
            INSERT INTO INTERACAO VALUES
            (1, DATE '2026-05-04', 'A', NULL, 1, TRUE),
            (2, DATE '2026-05-04', 'B', NULL, 1, FALSE)
        """.trimIndent())*/ //trocar para InteracaoAbuso

        connection.createStatement().execute("""
            INSERT INTO ESTATISTICA_ABUSO VALUES
            (TRUE, 1),
            (TRUE, 2),
            (TRUE, 3),
            (TRUE, 4),
            (TRUE, 5),
            (FALSE, 1),
            (FALSE, 2),
            (FALSE, 3),
            (FALSE, 4),
            (FALSE, 5)
        """.trimIndent())
    }

    @Benchmark
    fun queryJDBC() =
        jdbc.getNumeroInteracoesAbusivas()

    @Benchmark
    fun queryReflection() =
        reflect.getNumeroInteracoesAbusivas()

    @Benchmark
    fun queryDynamic() =
        dynamic.getNumeroInteracoesAbusivas()

}