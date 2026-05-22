package org.example.classes

import reflection.Column
import java.sql.Date

data class Interacao(
    @Column("IDInteracao") val id: Int,
    @Column("DataInteracao") val data: Date,
    @Column("Texto") val texto: String,
    @Column("CedulaProfissionalM") val cedulaModerador: String,
    @Column("IDUtilizador") val idUtilizador: Int,
    @Column("EAbusiva") val abusiva: Boolean,
)

data class Caso(
    @Column("IDCaso") val id: String,
    @Column("AreaAtuacao") val areaAtuacao: String,
    @Column("GrauGravidade") val grauGravidade: Int,
)

data class InteracaoResumo(
    @Column("IDInteracao") val id: Int,
    @Column("NickName") val nickName: String,
    @Column("EAbusiva") val abusiva: Boolean,
)

data class InteracaoCaso(
    @Column("IDInteracao") val interacaoId: Int,
    @Column("IDCaso") val casoId: String,
)

data class CasoDetalhado(
    @Column("IDCaso") val id: Int,
    @Column("DataAbertura") val dataAbertura: Date,
    @Column("DataFecho") val dataFecho: Date,
    @Column("Descricao") val descricao: String,
    @Column("AreaAtuacao") val areaAtuacao: String,
    @Column("Anotacoes") val anotacoes: String,
    @Column("GrauGravidade") val grauGravidade: Int,
    @Column("DataAvaliacao") val dataAvaliacao: Date,
    @Column("TextoAD") val textoAD: String,
    @Column("CedulaProfissionalP") val psicologoId: String,
)

data class EstatisticaAbuso(
    @Column("EAbusiva") val abusiva: Boolean,
    @Column("Total") val total: Long,
)

data class CasosPorInteracao(
    @Column("IDInteracao") val interacaoId: Int,
    @Column("NumCasos") val numCasos: Int,
    @Column("MediaGravidade") val mediaGravidade: Double,
)

data class EstatisticaPsicologo(
    @Column("CedulaProfissionalP") val psicologoId: String,
    @Column("NumCasos") val numCasos: Int,
    @Column("CasosAvaliados") val casosAvaliados: Int,
    @Column("MediaGravidade") val mediaGravidade: Double,
    @Column("MaxGravidade") val maxGravidade: Int,
)

data class EstatisticaArea(
    @Column("AreaAtuacao") val area: String,
    @Column("NumPsicologos") val numPsicologos: Int,
    @Column("CasosIniciados") val casosIniciados: Int,
    @Column("CasosAvaliados") val casosAvaliados: Int,
    @Column("CasosFechados") val casosFechados: Int,
    @Column("MediaGravidade") val mediaGravidade: Double,
)

data class PsicologoIntervencoes(
    @Column("CedulaProfissionalP") val psicologoId: String,
    @Column("TotalIntervencoes") val totalIntervencoes: Int,
)
