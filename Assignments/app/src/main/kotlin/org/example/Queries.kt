import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

// --- QUERIES --- //
fun queryInteracaoUtilizador(conn: Connection) {
    val validUtilizadores =  arrayOf(1, 2, 3, 4, 5)
    val sql = """
        SELECT IDInteracao, DataInteracao, EAbusiva FROM INTERACAO
        WHERE IDUtilizador = ?
        """.trimIndent()
    var ps: PreparedStatement? = null
    var rs: ResultSet? = null
    println("ID Utilizador: ")
    val idIn = readln()
    val idUtilizador = idIn.toIntOrNull()
    if (idUtilizador == null || idUtilizador !in validUtilizadores) {
        println("Erro: ID ${idUtilizador} e um utilzador inválido")
        return
    }
    try {
        ps = conn.prepareStatement(sql)
        ps.setInt(1, idUtilizador)
        rs = ps.executeQuery()
        while (rs.next()) {
            println("Interacao: ${rs.getInt("IDInteracao")} | " +
                    "Data Interacao : ${rs.getString("DataInteracao")} | " +
                    "E Abusiva (True ou False): ${rs.getString("EAbusiva")}")
        }
    } catch (e: SQLException) {
        println("Erro ao executar a QUERY:")
        e.printStackTrace()
    } finally {
        try {
            rs?.close()
        } catch (ignored: SQLException) { }
        try {
            ps?.close()
        } catch (ignored: SQLException) { }
    }
}

fun queryCasosInteracao(conn: Connection) {
    val sql = """
        SELECT IDCaso, AreaAtuacao, GrauGravidade FROM CASOS_DE_CYBERBULLYING
        WHERE IDInteracao = ?
        """.trimIndent()
    var ps: PreparedStatement? = null
    var rs: ResultSet? = null
    print("ID Interacao: ")
    val idInteracao = readln().toInt()
    try {
        ps = conn.prepareStatement(sql)
        ps.setInt(1, idInteracao)
        rs = ps.executeQuery()

        while (rs.next()) {
            println("Caso: ${rs.getString("IDCaso")} | " +
                    "Area Atuacao: ${rs.getString("AreaAtuacao")} | " +
                    "Grau Gravidade: ${rs.getString("GrauGravidade")}"
            )
        }
    } catch (e: SQLException) {
        println("Erro ao executar a QUERY:")
        e.printStackTrace()
    } finally {
        try {
            rs?.close()
        } catch (ignored: SQLException) { }
        try {
            ps?.close()
        } catch (ignored: SQLException) { }
    }
}

fun queryTodasInteracao(conn: Connection) {
    val sql = """
        SELECT I.IDInteracao, U.NickName, I.EAbusiva FROM INTERACAO I
        JOIN UTILIZADOR U ON I.IDUtilizador = U.IDUtilizador
        ORDER BY I.EAbusiva IS NOT NULL
    """.trimIndent()
    var ps: Statement? = null
    var rs: ResultSet? = null
    try {
        ps = conn.createStatement()
        rs = ps.executeQuery(sql)
        while (rs.next()) {
            println("Interacao: ${rs.getInt("IDInteracao")} | " +
                    "NickName = ${rs.getString("NickName")} | " +
                    "E Abusiva = ${rs.getString("EAbusiva")}"
            )
        }
    } catch (e: SQLException) {
        println("Erro ao executar a QUERY:")
        e.printStackTrace()
    } finally {
        try {
            rs?.close()
        } catch (ignored: SQLException) { }
        try {
            ps?.close()
        } catch (ignored: SQLException) { }
    }
}

fun queryInteracoesCasosUT (conn: Connection) {
    val validUtilizadores =  arrayOf(1, 2, 3, 4, 5)
    val sql = """
        SELECT I.IDInteracao, CB.IDCaso FROM INTERACAO I
        LEFT JOIN CASOS_DE_CYBERBULLYING CB ON I.IDInteracao = CB.IDInteracao
        WHERE I.IDUtilizador = ?
    """.trimIndent()
    var ps: Statement? = null
    var rs: ResultSet? = null
    println("ID Utilizador: ")
    val idIn = readln()
    val idUtilizador = idIn.toIntOrNull()
    if (idUtilizador == null || idUtilizador !in validUtilizadores) {
        println("Erro: ID ${idUtilizador} e um utilzador inválido")
        return
    }
    try {
        ps = conn.prepareStatement(sql)
        ps.setInt(1, idUtilizador)
        rs = ps.executeQuery()
        while (rs.next()) {
            println("Interacao: ${rs.getInt("IDInteracao")} | " +
                    "Caso : ${rs.getString("IDCaso")}"
            )
        }
    } catch (e: SQLException) {
        println("Erro ao executar a QUERY:")
        e.printStackTrace()
    } finally {
        try {
            rs?.close()
        } catch (ignored: SQLException) { }
        try {
            ps?.close()
        } catch (ignored: SQLException) { }
    }
}

fun queryInfoCasos(conn: Connection) {
    val sql = """
        SELECT * FROM CASOS_DE_CYBERBULLYING
        WHERE IDCaso = ?
    """.trimIndent()
    var ps: Statement? = null
    var rs: ResultSet? = null
    print("ID Caso: ")
    val idCaso = readln()
    try {
        ps = conn.prepareStatement(sql)
        ps.setString(1, idCaso)
        rs = ps.executeQuery()
        while (rs.next()) {
            println("Caso : ${rs.getString("IDCaso")} | " +
                    "Data Abertura: ${rs.getString("DataAbertura")} | " +
                    "Data Fecho: ${rs.getString("DataFecho")} | " +
                    "Descricao: ${rs.getString("Descricao")} |" +
                    "Area Atuacao: ${rs.getString("AreaAtuacao")} | " +
                    "Anotacoes : ${rs.getString("Anotacoes")} | " +
                    "Grau Gravidade : ${rs.getString("GrauGravidade")} | " +
                    "Data Avaliacao : ${rs.getString("DataAvaliacao")} | " +
                    "Texto AD : ${rs.getString("TextoAD")} |" +
                    "Psicologo : ${rs.getString("CedulaProfissionalP")}"
            )
        }
    } catch (e: SQLException) {
        println("Erro ao executar a QUERY:")
        e.printStackTrace()
    } finally {
        try {
            rs?.close()
        } catch (ignored: SQLException) { }
        try {
            ps?.close()
        } catch (ignored: SQLException) { }
    }
}

fun queryNumInteracoes(conn: Connection) {
    val sql = """
        SELECT EAbusiva, COUNT(*) AS Total FROM INTERACAO
        WHERE EAbusiva IS NOT NULL
        GROUP BY EAbusiva
    """.trimIndent()
    var ps: Statement? = null
    var rs: ResultSet? = null
    try {
        ps = conn.createStatement()
        rs = ps.executeQuery(sql)
        while (rs.next()) {
            println("E Abusiva : ${rs.getBoolean("EAbusiva")} |" +
                    "Total : ${rs.getInt("Total")}"
            )
        }
    } catch (e: SQLException) {
        println("Erro ao executar a QUERY:")
        e.printStackTrace()
    } finally {
        try {
            rs?.close()
        } catch (ignored: SQLException) {
        }
        try {
            ps?.close()
        } catch (ignored: SQLException) {
        }
    }
}

fun queryCasosPorInteracao(conn: Connection) {
    val sql = """
        SELECT IDInteracao, COUNT(*) AS NumCasos, AVG(GrauGravidade) AS MediaGravidade FROM CASOS_DE_CYBERBULLYING
        GROUP BY IDInteracao
        ORDER BY IDInteracao
    """.trimIndent()
    var ps: Statement? = null
    var rs: ResultSet? = null
    try {
        ps = conn.createStatement()
        rs = ps.executeQuery(sql)
        while (rs.next()) {
            println("Interacao: ${rs.getInt("IDInteracao")} | " +
                    "Casos : ${rs.getInt("NumCasos")} | " +
                    "Media Gravidade : ${rs.getDouble("MediaGravidade")}"
            )
        }
    } catch (e: SQLException) {
        println("Erro ao executar a QUERY:")
        e.printStackTrace()
    } finally {
        try {
            rs?.close()
        } catch (ignored: SQLException) {
        }
        try {
            ps?.close()
        } catch (ignored: SQLException) {
        }
    }
}

fun queryPsiCasos(conn: Connection) {
    val sql = """
        SELECT P.CedulaProfissionalP, COUNT(CB.IDCaso) AS NumCasos, 
            SUM(CASE WHEN E.Estado IN ('Avaliado', 'Fechado') THEN 1 ELSE 0 END) AS CasosAvaliados, 
		    AVG(CB.GrauGravidade) AS MediaGravidade, MAX(CB.GrauGravidade) AS MaxGravidade FROM PSICOLOGO P
        LEFT JOIN CASOS_DE_CYBERBULLYING CB ON P.CedulaProfissionalP = CB.CedulaProfissionalP
        LEFT JOIN ESTADO E ON CB.IDCaso = E.IDCaso
        WHERE P.CedulaProfissionalP IS NOT NULL
        GROUP BY P.CedulaProfissionalP
        ORDER BY P.CedulaProfissionalP
    """.trimIndent()

    var ps: Statement? = null
    var rs: ResultSet? = null
    try {
        ps = conn.createStatement()
        rs = ps.executeQuery(sql)
        while (rs.next()) {
            println("Psicologo : ${rs.getInt("CedulaProfissionalP")} | " +
                    "Total : ${rs.getInt("NumCasos")} | " +
                    "Analisados : ${rs.getInt("CasosAvaliados")} | " +
                    "Media : ${rs.getInt("MediaGravidade")} | " +
                    "Maximo : ${rs.getInt("MaxGravidade")}"
            )
        }
    } catch (e: SQLException) {
        println("Erro ao executar a QUERY:")
        e.printStackTrace()
    } finally {
        try {
            rs?.close()
        } catch (ignored: SQLException) {
        }
        try {
            ps?.close()
        } catch (ignored: SQLException) {
        }
    }
}

fun queryAreaPsi(conn: Connection) {
    val sql = """
        SELECT CB.AreaAtuacao, COUNT(DISTINCT p.CedulaProfissionalP) AS NumPsicologos, 
            SUM(CASE WHEN E.Estado = 'Iniciado' THEN 1 ELSE 0 END) AS CasosIniciados,
            SUM(CASE WHEN E.Estado = 'Avaliado' THEN 1 ELSE 0 END) AS CasosAvaliados,
            SUM(CASE WHEN E.Estado = 'Fechado'  THEN 1 ELSE 0 END) AS CasosFechados,
            AVG(CB.GrauGravidade) AS MediaGravidade FROM CASOS_DE_CYBERBULLYING CB
        LEFT JOIN PSICOLOGO P ON CB.CedulaProfissionalP = P.CedulaProfissionalP
        LEFT JOIN ESTADO E ON CB.IDCaso = E.IDCaso
        GROUP BY CB.AreaAtuacao
        ORDER BY CB.AreaAtuacao
    """.trimIndent()

    var ps: Statement? = null
    var rs: ResultSet? = null
    try {
        ps = conn.createStatement()
        rs = ps.executeQuery(sql)
        while (rs.next()) {
            println("Area Atuacao : ${rs.getString("AreaAtuacao")} | " +
                    "Psicologos : ${rs.getInt("NumPsicologos")} | " +
                    "Casos Iniciados : ${rs.getInt("CasosIniciados")} | " +
                    "Casos Avaliados : ${rs.getInt("CasosAvaliados")} | " +
                    "Casos Fechados : ${rs.getInt("CasosFechados")} | " +
                    "Media Gravidade : ${rs.getDouble("MediaGravidade")} | "
            )
        }
    } catch (e: SQLException) {
        println("Erro ao executar a QUERY:")
        e.printStackTrace()
    } finally {
        try {
            rs?.close()
        } catch (ignored: SQLException) {
        }
        try {
            ps?.close()
        } catch (ignored: SQLException) {
        }
    }
}

fun queryUtilizadoresRecursos(conn: Connection) {
    val sql = """
        SELECT C.IDUtilizador FROM CONSULTA C
        GROUP BY C.IDUtilizador
        HAVING COUNT(DISTINCT C.IDRecurso) = (SELECT COUNT(*) FROM RECURSO)
    """.trimIndent()

    var ps: Statement? = null
    var rs: ResultSet? = null
    try {
        ps = conn.createStatement()
        rs = ps.executeQuery(sql)
        while (rs.next()) {
            println("Utilizador : ${rs.getInt("IDUtilizador")}")
        }
    } catch (e: SQLException) {
        println("Erro ao executar a QUERY:")
        e.printStackTrace()
    } finally {
        try {
            rs?.close()
        } catch (ignored: SQLException) {
        }
        try {
            ps?.close()
        } catch (ignored: SQLException) {
        }
    }
}

fun queryGravidadeSuperiorMedia(conn: Connection) {
    val sql = """
        SELECT IDCaso, GrauGravidade FROM CASOS_DE_CYBERBULLYING
        WHERE GrauGravidade > (SELECT AVG(GrauGravidade) FROM CASOS_DE_CYBERBULLYING WHERE GrauGravidade IS NOT NULL)
    """.trimIndent()
    var ps: Statement? = null
    var rs: ResultSet? = null
    try {
        ps = conn.createStatement()
        rs = ps.executeQuery(sql)
        while (rs.next()) {
            println("Caso ${rs.getString("IDCaso")} | " +
                    "Gravidade ${rs.getInt("GrauGravidade")}"
            )
        }
    } catch (e: SQLException) {
        println("Erro ao executar a QUERY:")
        e.printStackTrace()
    } finally {
        try {
            rs?.close()
        } catch (ignored: SQLException) {
        }
        try {
            ps?.close()
        } catch (ignored: SQLException) {
        }
    }
}

fun queryPsiIntervencoes(conn: Connection) {
    val sql = """
        SELECT I.CedulaProfissionalP, COUNT(*) AS TotalIntervencoes FROM INTERVENCAO I
        LEFT JOIN CASOS_DE_CYBERBULLYING CB ON CB.IDCaso = I.IDCaso 
        WHERE GrauGravidade > ? AND I.CedulaProfissionalP IS NOT NULL
        GROUP BY I.CedulaProfissionalP
        HAVING COUNT(*) > ?
        ORDER BY I.CedulaProfissionalP
    """.trimIndent()
    var ps: Statement? = null
    var rs: ResultSet? = null
    print("Numero Minimo de interacoes: ")
    val numIntervencoes = readln().toInt()
    print("Grau Gravidade (1 a 5): ")
    val gravidadeInput = readln()
    val gravidadeConfirmada = gravidadeInput.toIntOrNull()
    if (gravidadeConfirmada == null || gravidadeConfirmada !in 1..5) {
        println("Erro: O grau de gravidade $gravidadeConfirmada nao esta entre 1 e 5")
        return
    }
    try {
        ps = conn.prepareStatement(sql)
        ps.setInt(1, gravidadeConfirmada)
        ps.setInt(2, numIntervencoes)
        rs = ps.executeQuery()
        while (rs.next()) {
            println("Psicologo: ${rs.getString("CedulaProfissionalP")} | " +
                    "Total de intervencoes com grau de gravidade maior que $gravidadeInput: " +
                    " ${rs.getInt("TotalIntervencoes")}"
            )
        }
    } catch (e: SQLException) {
        println("Erro ao executar a QUERY:")
        e.printStackTrace()
    } finally {
        try {
            rs?.close()
        } catch (ignored: SQLException) {
        }
        try {
            ps?.close()
        } catch (ignored: SQLException) {
        }
    }
}