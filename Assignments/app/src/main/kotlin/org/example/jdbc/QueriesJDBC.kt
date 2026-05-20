package org.example.jdbc

import org.example.classes.*
import org.example.interfaces.QueriesInterface
import java.sql.Connection
import java.sql.Date

class QueriesJDBC(
    private val connection: Connection,
) : QueriesInterface {
    override fun getInteracoesByUser(userId: Int): List<Interacao> {
        val resultList = mutableListOf<Interacao>()

        val sql =
            """
            SELECT IDInteracao, DataInteracao, Texto, CedulaProfissionalM, IDUtilizador, EAbusiva
            FROM INTERACAO
            WHERE IDUtilizador = ?
            """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            ps.setInt(1, userId)
            val rs = ps.executeQuery()

            while (rs.next()) {
                resultList.add(
                    Interacao(
                        id = rs.getInt("IDInteracao"),
                        data = rs.getDate("DataInteracao"),
                        texto = rs.getString("Texto"),
                        cedulaModerador = rs.getString("CedulaProfissionalM"),
                        idUtilizador = rs.getInt("IDUtilizador"),
                        abusiva = rs.getObject("EAbusiva") as Boolean?,
                    ),
                )
            }
        }

        return resultList
    }

    override fun getCasosByInteracao(interacaoId: Int): List<Caso> {
        val resultList = mutableListOf<Caso>()

        val sql =
            """
            SELECT IDCaso, AreaAtuacao, GrauGravidade FROM CASOS_DE_CYBERBULLYING
            WHERE IDInteracao = ?
            """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            ps.setInt(1, interacaoId)
            val rs = ps.executeQuery()
            while (rs.next()) {
                resultList.add(
                    Caso(
                        id = rs.getString("IDCaso"),
                        areaAtuacao = rs.getString("AreaAtuacao"),
                        grauGravidade = rs.getObject("GrauGravidade") as Int?,
                    ),
                )
            }
        }

        return resultList
    }

    override fun getTodasInteracoes(): List<InteracaoResumo> {
        val resultList = mutableListOf<InteracaoResumo>()

        val sql =
            """
            SELECT I.IDInteracao, U.NickName, I.EAbusiva FROM INTERACAO I
            JOIN UTILIZADOR U ON I.IDUtilizador = U.IDUtilizador
            ORDER BY I.EAbusiva IS NOT NULL
            """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()
            while (rs.next()) {
                resultList.add(
                    InteracaoResumo(
                        id = rs.getInt("IDInteracao"),
                        nickName = rs.getString("NickName"),
                        abusiva = rs.getObject("EAbusiva") as Boolean?,
                    ),
                )
            }
        }

        return resultList
    }

    override fun getInteracoesCasosByUser(userId: Int): List<InteracaoCaso> {
        val resultList = mutableListOf<InteracaoCaso>()

        val sql =
            """
            SELECT I.IDInteracao, CB.IDCaso FROM INTERACAO I
            LEFT JOIN CASOS_DE_CYBERBULLYING CB ON I.IDInteracao = CB.IDInteracao
            WHERE I.IDUtilizador = ?
            """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            ps.setInt(1, userId)
            val rs = ps.executeQuery()
            while (rs.next()) {
                resultList.add(
                    InteracaoCaso(
                        interacaoId = rs.getInt("IDInteracao"),
                        casoId = rs.getString("IDCaso"),
                    ),
                )
            }
        }

        return resultList
    }

    override fun getCasoById(id: Int): CasoDetalhado? {
        var result: CasoDetalhado? = null

        val sql =
            """
            SELECT * FROM CASOS_DE_CYBERBULLYING
            WHERE IDCaso = ?
            """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            ps.setInt(1, id)
            val rs = ps.executeQuery()

            if (rs.next()) {
                result =
                    CasoDetalhado(
                        id = rs.getString("IDCaso"),
                        dataAbertura = rs.getObject("DataAbertura") as Date?,
                        dataFecho = rs.getObject("DataFecho") as Date?,
                        descricao = rs.getString("Descricao"),
                        areaAtuacao = rs.getString("AreaAtuacao"),
                        anotacoes = rs.getString("Anotacoes"),
                        grauGravidade = rs.getObject("GrauGravidade") as Int?,
                        dataAvaliacao = rs.getObject("DataAvaliacao") as Date?,
                        textoAD = rs.getString("TextoAD"),
                        psicologoId = rs.getString("CedulaProfissionalP"),
                    )
            }
        }

        return result
    }

    override fun getNumeroInteracoesAbusivas(): List<EstatisticaAbuso> {
        val resultList = mutableListOf<EstatisticaAbuso>()

        val sql =
            """
            SELECT EAbusiva, COUNT(*) AS Total 
            FROM INTERACAO
            WHERE EAbusiva IS NOT NULL
            GROUP BY EAbusiva
            """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()

            while (rs.next()) {
                resultList.add(
                    EstatisticaAbuso(
                        abusiva = rs.getObject("EAbusiva") as Boolean,
                        total = rs.getLong("Total"),
                    ),
                )
            }
        }

        return resultList
    }

    override fun getCasosPorInteracao(): List<CasosPorInteracao> {
        val resultList = mutableListOf<CasosPorInteracao>()

        val sql =
            """
            SELECT IDInteracao, COUNT(*) AS NumCasos, AVG(GrauGravidade) AS MediaGravidade 
            FROM CASOS_DE_CYBERBULLYING
            GROUP BY IDInteracao
            ORDER BY IDInteracao
            """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()

            while (rs.next()) {
                resultList.add(
                    CasosPorInteracao(
                        interacaoId = rs.getInt("IDInteracao"),
                        numCasos = rs.getInt("NumCasos"),
                        mediaGravidade = rs.getObject("MediaGravidade") as Double?,
                    ),
                )
            }
        }

        return resultList
    }

    override fun getEstatisticasPsicologos(): List<EstatisticaPsicologo> {
        val resultList = mutableListOf<EstatisticaPsicologo>()

        val sql =
            """
            SELECT P.CedulaProfissionalP, COUNT(CB.IDCaso) AS NumCasos, 
            SUM(CASE WHEN E.Estado IN ('Avaliado', 'Fechado') THEN 1 ELSE 0 END) AS CasosAvaliados,
            AVG(CB.GrauGravidade) AS MediaGravidade, 
            MAX(CB.GrauGravidade) AS MaxGravidade 
            FROM PSICOLOGO P
            LEFT JOIN CASOS_DE_CYBERBULLYING CB ON P.CedulaProfissionalP = CB.CedulaProfissionalP
            LEFT JOIN ESTADO E ON CB.IDCaso = E.IDCaso
            WHERE P.CedulaProfissionalP IS NOT NULL
            GROUP BY P.CedulaProfissionalP
            ORDER BY P.CedulaProfissionalP
            """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()

            while (rs.next()) {
                resultList.add(
                    EstatisticaPsicologo(
                        psicologoId = rs.getString("CedulaProfissionalP"),
                        numCasos = rs.getInt("NumCasos"),
                        casosAvaliados = rs.getInt("CasosAvaliados"),
                        mediaGravidade = rs.getObject("MediaGravidade") as Double?,
                        maxGravidade = rs.getObject("MaxGravidade") as Int?,
                    ),
                )
            }
        }

        return resultList
    }

    override fun getEstatisticasPorArea(): List<EstatisticaArea> {
        val resultList = mutableListOf<EstatisticaArea>()

        val sql =
            """
            SELECT CB.AreaAtuacao, COUNT(DISTINCT P.CedulaProfissionalP) AS NumPsicologos,
            SUM(CASE WHEN E.Estado = 'Iniciado' THEN 1 ELSE 0 END) AS CasosIniciados,
            SUM(CASE WHEN E.Estado = 'Avaliado' THEN 1 ELSE 0 END) AS CasosAvaliados,
            SUM(CASE WHEN E.Estado = 'Fechado' THEN 1 ELSE 0 END) AS CasosFechados,
            AVG(CB.GrauGravidade) AS MediaGravidade
            FROM CASOS_DE_CYBERBULLYING CB
            LEFT JOIN PSICOLOGO P ON CB.CedulaProfissionalP = P.CedulaProfissionalP
            LEFT JOIN ESTADO E ON CB.IDCaso = E.IDCaso
            GROUP BY CB.AreaAtuacao
            ORDER BY CB.AreaAtuacao
            """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()

            while (rs.next()) {
                resultList.add(
                    EstatisticaArea(
                        area = rs.getString("AreaAtuacao"),
                        numPsicologos = rs.getInt("NumPsicologos"),
                        casosIniciados = rs.getInt("CasosIniciados"),
                        casosAvaliados = rs.getInt("CasosAvaliados"),
                        casosFechados = rs.getInt("CasosFechados"),
                        mediaGravidade = rs.getObject("MediaGravidade") as Double?,
                    ),
                )
            }
        }

        return resultList
    }

    override fun getUtilizadoresComTodosRecursos(): List<Int> {
        val resultList = mutableListOf<Int>()

        val sql =
            """
            SELECT C.IDUtilizador 
            FROM CONSULTA C
            GROUP BY C.IDUtilizador
            HAVING COUNT(DISTINCT C.IDRecurso) = (SELECT COUNT(*) FROM RECURSO)
            """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()

            while (rs.next()) {
                resultList.add(rs.getInt("IDUtilizador"))
            }
        }

        return resultList
    }

    override fun getCasosGravidadeSuperiorMedia(): List<Caso> {
        val resultList = mutableListOf<Caso>()

        val sql =
            """
            SELECT IDCaso, GrauGravidade 
            FROM CASOS_DE_CYBERBULLYING
            WHERE GrauGravidade > (
                SELECT AVG(GrauGravidade) 
                FROM CASOS_DE_CYBERBULLYING 
                WHERE GrauGravidade IS NOT NULL
            )
            """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()

            while (rs.next()) {
                resultList.add(
                    Caso(
                        id = rs.getString("IDCaso"),
                        areaAtuacao = null,
                        grauGravidade = rs.getObject("GrauGravidade") as Int?,
                    ),
                )
            }
        }

        return resultList
    }

    override fun getPsiIntervencoes(
        minIntervencoes: Int,
        minGravidade: Int,
    ): List<PsicologoIntervencoes> {
        val resultList = mutableListOf<PsicologoIntervencoes>()

        val sql =
            """
            SELECT I.CedulaProfissionalP, COUNT(*) AS TotalIntervencoes 
            FROM INTERVENCAO I
            LEFT JOIN CASOS_DE_CYBERBULLYING CB ON CB.IDCaso = I.IDCaso
            WHERE GrauGravidade > ? AND I.CedulaProfissionalP IS NOT NULL
            GROUP BY I.CedulaProfissionalP
            HAVING COUNT(*) > ?
            ORDER BY I.CedulaProfissionalP
            """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            ps.setInt(1, minGravidade)
            ps.setInt(2, minIntervencoes)

            val rs = ps.executeQuery()

            while (rs.next()) {
                resultList.add(
                    PsicologoIntervencoes(
                        psicologoId = rs.getString("CedulaProfissionalP"),
                        totalIntervencoes = rs.getInt("TotalIntervencoes"),
                    ),
                )
            }
        }

        return resultList
    }
}
