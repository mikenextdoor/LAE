package org.example.interfaces

import org.example.classes.*

interface QueriesInterface {
    fun getInteracoesByUser(userId: Int): List<Interacao>

    fun getCasosByInteracao(interacaoId: Int): List<Caso>

    fun getTodasInteracoes(): List<InteracaoResumo>

    fun getInteracoesCasosByUser(userId: Int): List<InteracaoCaso>

    fun getCasoById(id: Int): CasoDetalhado?

    fun getNumeroInteracoesAbusivas(): List<EstatisticaAbuso>

    fun getCasosPorInteracao(): List<CasosPorInteracao>

    fun getEstatisticasPsicologos(): List<EstatisticaPsicologo>

    fun getEstatisticasPorArea(): List<EstatisticaArea>

    fun getUtilizadoresComTodosRecursos(): List<Int>

    fun getCasosGravidadeSuperiorMedia(): List<Caso>

    fun getPsiIntervencoes(minIntervencoes: Int, minGravidade: Int): List<PsicologoIntervencoes>
}