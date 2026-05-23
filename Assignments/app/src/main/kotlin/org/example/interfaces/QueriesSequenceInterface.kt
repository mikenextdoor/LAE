package org.example.interfaces

import org.example.classes.Caso
import org.example.classes.CasoDetalhado
import org.example.classes.CasosPorInteracao
import org.example.classes.EstatisticaAbuso
import org.example.classes.EstatisticaArea
import org.example.classes.EstatisticaPsicologo
import org.example.classes.Interacao
import org.example.classes.InteracaoCaso
import org.example.classes.InteracaoResumo
import org.example.classes.PsicologoIntervencoes

interface QueriesSequenceInterface {

    fun findInteracoesByUser(userId: Int): Sequence<Interacao>

    fun findCasosByInteracao(interacaoId: Int): Sequence<Caso>

    fun findTodasInteracoes(): Sequence<InteracaoResumo>

    fun findInteracoesCasosByUser(userId: Int): Sequence<InteracaoCaso>

    fun findCasoById(id: Int): Sequence<CasoDetalhado?>

    fun findNumeroInteracoesAbusivas(): Sequence<EstatisticaAbuso>

    fun findCasosPorInteracao(): Sequence<CasosPorInteracao>

    fun findEstatisticasPsicologos(): Sequence<EstatisticaPsicologo>

    fun findEstatisticasPorArea(): Sequence<EstatisticaArea>

    fun findUtilizadoresComTodosRecursos(): Sequence<Int>

    fun findCasosGravidadeSuperiorMedia(): Sequence<Caso>

    fun findPsiIntervencoes(
        minIntervencoes: Int,
        minGravidade: Int,
    ): Sequence<PsicologoIntervencoes>
}