import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties

// --- Conexão --- //
fun createConnection(): Connection {
    val password = "benf1ca1904"
    val url = "jdbc:postgresql://localhost:5432/CasosCyberBullying"

    val properties =
        Properties().apply {
            put("user", "postgres")
            put("password", password) // password indicada na instalação do PostgreSQL
        }
    return DriverManager.getConnection(url, properties)
}
