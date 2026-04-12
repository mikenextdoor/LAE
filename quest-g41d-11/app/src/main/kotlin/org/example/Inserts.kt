import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.SQLException

// --- INSERTS --- //
fun insertInteracao(conn: Connection) {
    val validModerador = arrayOf(10, 70, 83)
    val sql = """
        INSERT INTO INTERACAO
            (IDInteracao, DataInteracao, Texto, CedulaProfissionalM, IDUtilizador, EAbusiva)
            VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()
    print("ID Interacao: ")
    val idInteracao = readln().toInt()
    print("Data Interacao (AAAA-MM-DD): ")
    val dataInteracao = readln()
    print("Texto: ")
    val texto = readln()
    print("Cedula moderador: ")
    val modIn = readln()
    val idModerador = modIn.toIntOrNull()
    if (idModerador == null || idModerador !in validModerador) {
        println("Erro: ID do moderador nao e valido")
        return
    }
    print("ID Utilizador: ")
    val idUtilizador = readln().toInt()
    print("E Abusiva (True ou False): ")
    val abusivaIn = readln()
    val eAbusiva = if (abusivaIn.isEmpty()) null else abusivaIn.toBoolean()

    var ps: PreparedStatement? = null
    try {
        ps = conn.prepareStatement(sql)
        ps.setInt(1, idInteracao)
        ps.setDate(2, Date.valueOf(dataInteracao))
        ps.setString(3, texto)
        ps.setObject(4, idModerador)
        ps.setInt(5, idUtilizador)
        ps.setObject(6, eAbusiva)

        ps.executeUpdate()
        println("INSERT executed")
    } catch (e: SQLException) {
        println("Erro ao executar o INSERT:")
        e.printStackTrace()
    } finally {
        try {
            ps?.close()
        } catch (ignored: SQLException) { }
    }
}