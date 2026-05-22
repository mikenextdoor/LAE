package org.example.reflection

import org.example.classes.*
import org.example.interfaces.QueriesInterface
import reflection.*
import java.sql.Connection

class QueriesReflectionDynamic(
    private val connection: Connection,
) : QueriesInterface {
    private val interacaoFormatter = buildDynamicClass(Interacao::class)
    private val casoFormatter = buildDynamicClass(Caso::class)
    private val interacaoResumoFormatter = buildDynamicClass(InteracaoResumo::class)
    private val interacaoCasoFormatter = buildDynamicClass(InteracaoCaso::class)
    private val casoDetalhadoFormatter = buildDynamicClass(CasoDetalhado::class)
    private val estatisticaAbusoFormatter = buildDynamicClass(EstatisticaAbuso::class)
    private val casosPorInteracaoFormatter = buildDynamicClass(CasosPorInteracao::class)
    private val estatisticaPsicologoFormatter = buildDynamicClass(EstatisticaPsicologo::class)
    private val estatisticaAreaFormatter = buildDynamicClass(EstatisticaArea::class)
    private val psicologoIntervencoesFormatter = buildDynamicClass(PsicologoIntervencoes::class)

    /***
     * falta terminar o buildDynamicClass para ser geral, para poder aceitar todas as classes
     * por enquanto os testes do Bench e do DynamicTest apenas estão a usar o estatisticaAbusoFormatter.
     */

    val getInteracoesByUserSQL = """
            SELECT IDInteracao, DataInteracao, Texto, CedulaProfissionalM, IDUtilizador, EAbusiva
            FROM INTERACAO
            WHERE IDUtilizador = ?
            """.trimIndent()
    override fun getInteracoesByUser(userId: Int): List<Interacao> {
        val sql =getInteracoesByUserSQL
        connection.prepareStatement(sql).use { ps ->
            ps.setInt(1, userId)
            val rs = ps.executeQuery()
            return interacaoFormatter.toClassFormatter(rs)
        }
    }

    val getCasosByInteracaoSQL = """
            SELECT IDCaso, AreaAtuacao, GrauGravidade FROM CASOS_DE_CYBERBULLYING
            WHERE IDInteracao = ?
            """.trimIndent()
    override fun getCasosByInteracao(interacaoId: Int): List<Caso> {
        val sql = getCasosByInteracaoSQL
        connection.prepareStatement(sql).use { ps ->
            ps.setInt(1, interacaoId)
            val rs = ps.executeQuery()
            return casoFormatter.toClassFormatter(rs)
        }
    }

    val getTodasInteracoesSQL = """
            SELECT I.IDInteracao, U.NickName, I.EAbusiva FROM INTERACAO I
            JOIN UTILIZADOR U ON I.IDUtilizador = U.IDUtilizador
            ORDER BY I.EAbusiva IS NOT NULL
            """.trimIndent()
    override fun getTodasInteracoes(): List<InteracaoResumo> {
        val sql = getTodasInteracoesSQL
        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()
            return interacaoResumoFormatter.toClassFormatter(rs)
        }
    }

    val getInteracoesCasosByUserSQL ="""
            SELECT I.IDInteracao, CB.IDCaso FROM INTERACAO I
            LEFT JOIN CASOS_DE_CYBERBULLYING CB ON I.IDInteracao = CB.IDInteracao
            WHERE I.IDUtilizador = ?
            """.trimIndent()
    override fun getInteracoesCasosByUser(userId: Int): List<InteracaoCaso> {
        val sql = getInteracoesCasosByUserSQL
        connection.prepareStatement(sql).use { ps ->
            ps.setInt(1, userId)
            val rs = ps.executeQuery()
            return interacaoCasoFormatter.toClassFormatter(rs)
        }
    }

    val getCasoByIdSQL = """
            SELECT * FROM CASOS_DE_CYBERBULLYING
            WHERE IDCaso = ?
            """.trimIndent()
    override fun getCasoById(id: Int): CasoDetalhado? {
        val sql = getCasoByIdSQL
        connection.prepareStatement(sql).use { ps ->
            ps.setInt(1, id)
            val rs = ps.executeQuery()
            val list = casoDetalhadoFormatter.toClassFormatter(rs)
            return list.firstOrNull()
        }
    }

    val getNumeroInteracoesAbusivasSQL = """
            SELECT EAbusiva, COUNT(*) AS Total 
            FROM INTERACAO
            WHERE EAbusiva IS NOT NULL
            GROUP BY EAbusiva
            """.trimIndent()
    override fun getNumeroInteracoesAbusivas(): List<EstatisticaAbuso> {
        val sql = getNumeroInteracoesAbusivasSQL
        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()
            return estatisticaAbusoFormatter.toClassFormatter(rs)
        }
    }

    val getCasosPorInteracaoSQL = """
            SELECT IDInteracao, COUNT(*) AS NumCasos, AVG(GrauGravidade) AS MediaGravidade 
            FROM CASOS_DE_CYBERBULLYING
            GROUP BY IDInteracao
            ORDER BY IDInteracao
            """.trimIndent()
    override fun getCasosPorInteracao(): List<CasosPorInteracao> {
        val sql = getCasosPorInteracaoSQL
        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()
            return casosPorInteracaoFormatter.toClassFormatter(rs)
        }
    }

    val getEstatisticasPsicologos = """
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
    override fun getEstatisticasPsicologos(): List<EstatisticaPsicologo> {
        val sql = getEstatisticasPsicologos
        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()
            return estatisticaPsicologoFormatter.toClassFormatter(rs)
        }
    }

    val getEstatisticasPorAreaSQL = """
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
    override fun getEstatisticasPorArea(): List<EstatisticaArea> {
        val sql = getEstatisticasPorAreaSQL
        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()
            return estatisticaAreaFormatter.toClassFormatter(rs)
        }
    }

    val getUtilizadoresComTodosRecursos = """
            SELECT C.IDUtilizador 
            FROM CONSULTA C
            GROUP BY C.IDUtilizador
            HAVING COUNT(DISTINCT C.IDRecurso) = (SELECT COUNT(*) FROM RECURSO)
            """.trimIndent()
    override fun getUtilizadoresComTodosRecursos(): List<Int> {
        val sql = getUtilizadoresComTodosRecursos
        val result = mutableListOf<Int>()

        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()

            while (rs.next()) {
                result.add(rs.getInt("IDUtilizador"))
            }
        }
        return result
    }

    val getCasosGravidadeSuperiorMediaSQL = """
            SELECT IDCaso, NULL AS AreaAtuacao, GrauGravidade 
            FROM CASOS_DE_CYBERBULLYING
            WHERE GrauGravidade > (
                SELECT AVG(GrauGravidade) 
                FROM CASOS_DE_CYBERBULLYING 
                WHERE GrauGravidade IS NOT NULL
            )
            """.trimIndent()
    override fun getCasosGravidadeSuperiorMedia(): List<Caso> {
        val sql = getCasosGravidadeSuperiorMediaSQL
        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()
            return casoFormatter.toClassFormatter(rs)
        }
    }

    val getPsiIntervencoesSQL = """
            SELECT I.CedulaProfissionalP, COUNT(*) AS TotalIntervencoes 
            FROM INTERVENCAO I
            LEFT JOIN CASOS_DE_CYBERBULLYING CB ON CB.IDCaso = I.IDCaso
            WHERE GrauGravidade > ? AND I.CedulaProfissionalP IS NOT NULL
            GROUP BY I.CedulaProfissionalP
            HAVING COUNT(*) > ?
            ORDER BY I.CedulaProfissionalP
            """.trimIndent()
    override fun getPsiIntervencoes(
        minIntervencoes: Int,
        minGravidade: Int,
    ): List<PsicologoIntervencoes> {
        val sql = getPsiIntervencoesSQL
        connection.prepareStatement(sql).use { ps ->
            ps.setInt(1, minGravidade)
            ps.setInt(2, minIntervencoes)
            val rs = ps.executeQuery()
            return psicologoIntervencoesFormatter.toClassFormatter(rs)
        }
    }
}
