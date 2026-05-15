import org.example.classes.EstatisticaAbuso
import reflection.Column
import reflection.buildDynamicClass
import kotlin.reflect.full.memberProperties
import org.example.classes.*
import reflection.descriptor
import java.sql.Date
import kotlin.reflect.full.findAnnotation

data class InteracaoCaso(
    @Column("IDInteracao") val interacaoId: Int,
    @Column("IDCaso") val casoId: String?,
)

fun main() {
   /* val clazz = Caso::class

    println(clazz.members)
    println(clazz.memberProperties)*/
    var bool = true

    /*while (bool) {
        trocarPelaOutra(Interacao::class)
        trocarPelaOutra(Caso::class)
        bool = false
    }*/

    /*val clazz = InteracaoCaso::class.constructors.first().parameters[0].type.classifier
    println(String::class.descriptor())*/


    /*val classe = InteracaoCaso(12, "Ola")
    val ctor = classe::class.constructors.first()

    val ctorArgs = ctor.parameters
    val ctorArgsDiff = ctor.parameters.map { it.type }

    val properties = classe::class.memberProperties

    println(ctorArgs)
    println(ctorArgsDiff)

    println(
        properties.forEach { member ->
            println(member.name)
            println(member.findAnnotation<Column>()?.columnName
                ?: member.name)
        }
    )*/


    while (bool) {
        buildDynamicClass(Interacao::class)
        buildDynamicClass(Caso::class)
        buildDynamicClass(InteracaoResumo::class)
        buildDynamicClass(InteracaoCaso::class)
        buildDynamicClass(CasoDetalhado::class)
        buildDynamicClass(EstatisticaAbuso::class)
        buildDynamicClass(CasosPorInteracao::class)
        buildDynamicClass(EstatisticaPsicologo::class)
        buildDynamicClass(EstatisticaArea::class)
        buildDynamicClass(PsicologoIntervencoes::class)
        bool = false
    }



}
