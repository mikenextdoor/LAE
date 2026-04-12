package org.example.reflection

import org.example.classes.*
import org.example.interfaces.QueriesInterface
import reflection.*
import java.sql.Connection

class QueriesReflection(private val connection: Connection) : QueriesInterface {
    override fun getInteracoesByUser(userId: Int): List<Interacao> {

        val sql = """
        SELECT IDInteracao, DataInteracao, Texto, CedulaProfissionalM, IDUtilizador, EAbusiva
        FROM INTERACAO
        WHERE IDUtilizador = ?
        """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            ps.setInt(1, userId)
            val rs = ps.executeQuery()
            return toClassFormatter(rs, Interacao::class)
        }
    }

    override fun getCasosByInteracao(interacaoId: Int): List<Caso> {
        val sql = """
        SELECT IDCaso, AreaAtuacao, GrauGravidade FROM CASOS_DE_CYBERBULLYING
        WHERE IDInteracao = ?
        """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            ps.setInt(1, interacaoId)
            val rs = ps.executeQuery()
            return toClassFormatter(rs, Caso::class)
        }
    }

    override fun getTodasInteracoes(): List<InteracaoResumo> {
        val sql = """
        SELECT I.IDInteracao, U.NickName, I.EAbusiva FROM INTERACAO I
        JOIN UTILIZADOR U ON I.IDUtilizador = U.IDUtilizador
        ORDER BY I.EAbusiva IS NOT NULL
        """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()
            return toClassFormatter(rs, InteracaoResumo::class)
        }
    }

    override fun getInteracoesCasosByUser(userId: Int): List<InteracaoCaso> {
        val sql = """
        SELECT I.IDInteracao, CB.IDCaso FROM INTERACAO I
        LEFT JOIN CASOS_DE_CYBERBULLYING CB ON I.IDInteracao = CB.IDInteracao
        WHERE I.IDUtilizador = ?
        """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            ps.setInt(1, userId)
            val rs = ps.executeQuery()
            return toClassFormatter(rs, InteracaoCaso::class)
        }
    }

    override fun getCasoById(id: Int): CasoDetalhado? {
        val sql = """
        SELECT * FROM CASOS_DE_CYBERBULLYING
        WHERE IDCaso = ?
        """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            ps.setInt(1, id)
            val rs = ps.executeQuery()
            val list = toClassFormatter(rs, CasoDetalhado::class)
            return list.firstOrNull()
        }
    }

    override fun getNumeroInteracoesAbusivas(): List<EstatisticaAbuso> {
        val sql = """
        SELECT EAbusiva, COUNT(*) AS Total 
        FROM INTERACAO
        WHERE EAbusiva IS NOT NULL
        GROUP BY EAbusiva
        """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()
            return toClassFormatter(rs, EstatisticaAbuso::class)
        }
    }

    override fun getCasosPorInteracao(): List<CasosPorInteracao> {
        val sql = """
        SELECT IDInteracao, COUNT(*) AS NumCasos, AVG(GrauGravidade) AS MediaGravidade 
        FROM CASOS_DE_CYBERBULLYING
        GROUP BY IDInteracao
        ORDER BY IDInteracao
        """.trimIndent()

        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()
            return toClassFormatter(rs, CasosPorInteracao::class)
        }
    }

    override fun getEstatisticasPsicologos(): List<EstatisticaPsicologo> {
        val sql = """
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
            return toClassFormatter(rs, EstatisticaPsicologo::class)
        }
    }

    override fun getEstatisticasPorArea(): List<EstatisticaArea> {
        val sql = """
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
            return toClassFormatter(rs, EstatisticaArea::class)
        }
    }

    override fun getUtilizadoresComTodosRecursos(): List<Int> {
        val sql = """
        SELECT C.IDUtilizador 
        FROM CONSULTA C
        GROUP BY C.IDUtilizador
        HAVING COUNT(DISTINCT C.IDRecurso) = (SELECT COUNT(*) FROM RECURSO)
        """.trimIndent()

        val result = mutableListOf<Int>()

        connection.prepareStatement(sql).use { ps ->
            val rs = ps.executeQuery()

            while (rs.next()) {
                result.add(rs.getInt("IDUtilizador"))
            }
        }
        return result
    }

    override fun getCasosGravidadeSuperiorMedia(): List<Caso> {
        val sql = """
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
            return toClassFormatter(rs, Caso::class)
        }
    }

    override fun getPsiIntervencoes(minIntervencoes: Int, minGravidade: Int): List<PsicologoIntervencoes> {
        val sql = """
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
            return toClassFormatter(rs, PsicologoIntervencoes::class)
        }
    }

}