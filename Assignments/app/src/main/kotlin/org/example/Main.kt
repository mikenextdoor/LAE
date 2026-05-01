import org.example.classes.Interacao
import java.sql.*
import java.util.Properties

fun main() {
    var conn: Connection? = null
    var choice = 0
    try {
        conn = createConnection()
        do {
            menuComandos()
            println("Escolha: ")
            choice = readln().toInt()
            when (choice) {
                1 -> insertInteracao(conn)
                2 -> queryInteracaoUtilizador(conn)
                3 -> queryCasosInteracao(conn)
                4 -> queryTodasInteracao(conn)
                5 -> queryInteracoesCasosUT(conn)
                6 -> queryInfoCasos(conn)
                7 -> queryNumInteracoes(conn)
                8 -> queryCasosPorInteracao(conn)
                9 -> queryPsiCasos(conn)
                10 -> queryAreaPsi(conn)
                11 -> queryUtilizadoresRecursos(conn)
                12 -> queryGravidadeSuperiorMedia(conn)
                13 -> queryPsiIntervencoes(conn)
                0 -> println("terminar!")
            }
        } while (choice != 0)
    } catch (e: SQLException) {
        e.printStackTrace()
    } finally {
        try {
            conn?.close()
            println("Ligacao terminada")
        } catch (ignored: SQLException) { }
    }
}