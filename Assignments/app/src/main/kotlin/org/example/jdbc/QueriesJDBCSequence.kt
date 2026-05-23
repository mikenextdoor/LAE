package org.example.jdbc

import org.example.classes.*
import org.example.interfaces.QueriesInterface
import org.example.interfaces.QueriesSequenceInterface
import java.lang.Thread.yield
import java.sql.Connection
import java.sql.Date
import kotlin.sequences.Sequence

class QueriesJDBCSequence(
    private val connection: Connection,
) : QueriesSequenceInterface {

   override fun findInteracoesByUser(userId: Int): Sequence<Interacao> {
        val sql =
            """
            SELECT IDInteracao, DataInteracao, Texto, CedulaProfissionalM, IDUtilizador, EAbusiva
            FROM INTERACAO
            WHERE IDUtilizador = ?
            """.trimIndent()

        return sequence {
            connection.prepareStatement(sql).use { ps ->
                ps.setInt(1, userId)
                val rs = ps.executeQuery()

                while (rs.next()) {
                    yield(
                        Interacao(
                            id = rs.getInt("IDInteracao"),
                            data = rs.getDate("DataInteracao"),
                            texto = rs.getString("Texto"),
                            cedulaModerador = rs.getString("CedulaProfissionalM"),
                            idUtilizador = rs.getInt("IDUtilizador"),
                            abusiva = rs.getObject("EAbusiva") as Boolean,
                        ),
                    )
                }
            }
        }

    }


   override fun findCasosByInteracao(interacaoId: Int): Sequence<Caso> {
        val sql =
            """
            SELECT IDCaso, AreaAtuacao, GrauGravidade FROM CASOS_DE_CYBERBULLYING
            WHERE IDInteracao = ?
            """.trimIndent()

        return sequence {
            connection.prepareStatement(sql).use { ps ->
                ps.setInt(1, interacaoId)
                val rs = ps.executeQuery()
                while (rs.next()) {
                    yield(
                        Caso(
                            id = rs.getString("IDCaso"),
                            areaAtuacao = rs.getString("AreaAtuacao"),
                            grauGravidade = rs.getObject("GrauGravidade") as Int,
                        ),
                    )
                }
            }
        }

    }

    override fun findTodasInteracoes(): Sequence<InteracaoResumo> {
        val sql =
            """
            SELECT I.IDInteracao, U.NickName, I.EAbusiva FROM INTERACAO I
            JOIN UTILIZADOR U ON I.IDUtilizador = U.IDUtilizador
            ORDER BY I.EAbusiva IS NOT NULL
            """.trimIndent()

        return sequence {
            connection.prepareStatement(sql).use { ps ->
                val rs = ps.executeQuery()
                while (rs.next()) {
                    yield(
                        InteracaoResumo(
                            id = rs.getInt("IDInteracao"),
                            nickName = rs.getString("NickName"),
                            abusiva = rs.getObject("EAbusiva") as Boolean,
                        ),
                    )
                }
            }
        }

    }

    override fun findInteracoesCasosByUser(userId: Int): Sequence<InteracaoCaso> {
        val sql =
            """
            SELECT I.IDInteracao, CB.IDCaso FROM INTERACAO I
            LEFT JOIN CASOS_DE_CYBERBULLYING CB ON I.IDInteracao = CB.IDInteracao
            WHERE I.IDUtilizador = ?
            """.trimIndent()

        return sequence {
            connection.prepareStatement(sql).use { ps ->
                ps.setInt(1, userId)
                val rs = ps.executeQuery()
                while (rs.next()) {
                    yield(
                        InteracaoCaso(
                            interacaoId = rs.getInt("IDInteracao"),
                            casoId = rs.getString("IDCaso"),
                        ),
                    )
                }
            }
        }

    }

   override fun findCasoById(id: Int): Sequence<CasoDetalhado?>{

        val sql =
            """
            SELECT * FROM CASOS_DE_CYBERBULLYING
            WHERE IDCaso = ?
            """.trimIndent()

        return sequence {
            connection.prepareStatement(sql).use { ps ->
                ps.setInt(1, id)
                val rs = ps.executeQuery()

                if (rs.next()) {
                    yield(
                        CasoDetalhado(
                            id = rs.getInt("IDCaso"),
                            dataAbertura = rs.getObject("DataAbertura") as Date,
                            dataFecho = rs.getObject("DataFecho") as Date,
                            descricao = rs.getString("Descricao"),
                            areaAtuacao = rs.getString("AreaAtuacao"),
                            anotacoes = rs.getString("Anotacoes"),
                            grauGravidade = rs.getObject("GrauGravidade") as Int,
                            dataAvaliacao = rs.getObject("DataAvaliacao") as Date,
                            textoAD = rs.getString("TextoAD"),
                            psicologoId = rs.getString("CedulaProfissionalP"),
                        ))
                }
            }
        }


    }

    override fun findNumeroInteracoesAbusivas(): Sequence<EstatisticaAbuso> {
        val sql =
            """
            SELECT EAbusiva, COUNT(*) AS Total 
            FROM INTERACAO
            WHERE EAbusiva IS NOT NULL
            GROUP BY EAbusiva
            """.trimIndent()

        return sequence {
            connection.prepareStatement(sql).use { ps ->
                val rs = ps.executeQuery()

                while (rs.next()) {
                    yield(
                        EstatisticaAbuso(
                            abusiva = rs.getObject("EAbusiva") as Boolean,
                            total = rs.getLong("Total")
                        )
                    )
                }
            }
        }
    }

    override fun findCasosPorInteracao(): Sequence<CasosPorInteracao> {
        val sql =
            """
            SELECT IDInteracao, COUNT(*) AS NumCasos, AVG(GrauGravidade) AS MediaGravidade 
            FROM CASOS_DE_CYBERBULLYING
            GROUP BY IDInteracao
            ORDER BY IDInteracao
            """.trimIndent()

        return sequence {
            connection.prepareStatement(sql).use { ps ->
                val rs = ps.executeQuery()

                while (rs.next()) {
                    yield(
                        CasosPorInteracao(
                            interacaoId = rs.getInt("IDInteracao"),
                            numCasos = rs.getInt("NumCasos"),
                            mediaGravidade = rs.getObject("MediaGravidade") as Double,
                        ),
                    )
                }
            }
        }

    }


    override fun findEstatisticasPsicologos(): Sequence<EstatisticaPsicologo> {
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

        return sequence {
            connection.prepareStatement(sql).use { ps ->
                val rs = ps.executeQuery()

                while (rs.next()) {
                    yield(
                        EstatisticaPsicologo(
                            psicologoId = rs.getString("CedulaProfissionalP"),
                            numCasos = rs.getInt("NumCasos"),
                            casosAvaliados = rs.getInt("CasosAvaliados"),
                            mediaGravidade = rs.getObject("MediaGravidade") as Double,
                            maxGravidade = rs.getObject("MaxGravidade") as Int,
                        ),
                    )
                }
            }

        }
    }

    override fun findEstatisticasPorArea(): Sequence<EstatisticaArea> {
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

        return sequence{
            connection.prepareStatement(sql).use { ps ->
                val rs = ps.executeQuery()

                while (rs.next()) {
                    yield(
                        EstatisticaArea(
                            area = rs.getString("AreaAtuacao"),
                            numPsicologos = rs.getInt("NumPsicologos"),
                            casosIniciados = rs.getInt("CasosIniciados"),
                            casosAvaliados = rs.getInt("CasosAvaliados"),
                            casosFechados = rs.getInt("CasosFechados"),
                            mediaGravidade = rs.getObject("MediaGravidade") as Double,
                        ),
                    )
                }
            }
        }

    }

    override fun findUtilizadoresComTodosRecursos(): Sequence<Int> {
        val sql =
            """
            SELECT C.IDUtilizador 
            FROM CONSULTA C
            GROUP BY C.IDUtilizador
            HAVING COUNT(DISTINCT C.IDRecurso) = (SELECT COUNT(*) FROM RECURSO)
            """.trimIndent()

        return sequence {
            connection.prepareStatement(sql).use { ps ->
                val rs = ps.executeQuery()

                while (rs.next()) {
                    yield(
                        rs.getInt("IDUtilizador"))
                }

            }
        }

    }

    override fun findCasosGravidadeSuperiorMedia(): Sequence<Caso> {
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
        return sequence {
            connection.prepareStatement(sql).use { ps ->
                val rs = ps.executeQuery()

                while (rs.next()) {
                    yield(
                        Caso(
                            id = rs.getString("IDCaso"),
                            areaAtuacao = "null",
                            grauGravidade = rs.getObject("GrauGravidade") as Int,
                        ),
                    )
                }
            }
        }

    }

    override fun findPsiIntervencoes(
        minIntervencoes: Int,
        minGravidade: Int,
    ): Sequence<PsicologoIntervencoes> {
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

        return sequence {
            connection.prepareStatement(sql).use { ps ->
                ps.setInt(1, minGravidade)
                ps.setInt(2, minIntervencoes)

                val rs = ps.executeQuery()

                while (rs.next()) {
                    yield(
                        PsicologoIntervencoes(
                            psicologoId = rs.getString("CedulaProfissionalP"),
                            totalIntervencoes = rs.getInt("TotalIntervencoes"),
                        ),
                    )
                }
            }
        }

    }
}
