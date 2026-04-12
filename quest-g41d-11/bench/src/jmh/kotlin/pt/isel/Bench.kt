package pt.isel

import org.h2.jdbcx.JdbcDataSource
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import java.sql.Connection
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime) // Measure execution time per operation
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
open class Bench {
    private lateinit var connection: Connection

    @Setup
    fun setup() {
        // Create an H2 in-memory DataSource and initialise the schema + seed data
        val dataSource =
            JdbcDataSource().apply {
                setURL("jdbc:h2:mem:benchdb;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:h2-init.sql'")
                user = "sa"
                password = ""
            }
        connection = dataSource.connection
    }

    @Benchmark
    fun benchmarkQuery(): List<String> = getChannelsNames(connection)
}
