import org.example.classes.EstatisticaAbuso
import reflection.Column
import reflection.buildClassFormatter
import kotlin.reflect.full.memberProperties

data class Caso(
    @Column("IDCaso") val id: String,
    @Column("AreaAtuacao") val areaAtuacao: String?,
    @Column("GrauGravidade") val grauGravidade: Int?,
)

fun main() {
    val clazz = Caso::class

    println(clazz.members)
    println(clazz.memberProperties)


    val formatter = buildClassFormatter(EstatisticaAbuso::class)
}
