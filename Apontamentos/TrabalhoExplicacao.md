### Classes
**mainClasses**: Declaradas data classes para todas as entidades, usado @Column para alinhar o nome da classe com o da Database se necessário;
**ModeratorClass**: Declarada a data class Moderador, subtipo da classe **User**;
**PsicologistClass**: Declarada a data class Psicologo, subtipo da classe **User**;
**UserClass**: Declarada a data class User.

---

### Interfaces
**InsertsInterface**: Declarada a interface com a função insertInteração:
```Kotlin
interface InsertsInterface {
    fun insertInteracao(interacao: Interacao)
}
```
**QueriesInterface**: Declarada a interface com todas as funções de Query:
```Kotlin
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
```
### JDBC
**InsertsJDBC**: Declarada a class InsertsJDBC que utiliza a interface InsertsInterface. Implementa a função insertInteracao sem qualquer tipo de reflexão (Implementação original do trabalho).
```Kotlin
class InsertsJDBC(private val connection: Connection) : InsertsInterface {
    override fun insertInteracao(interacao: Interacao) {

        val sql = """
        INSERT INTO INTERACAO
        (IDInteracao, DataInteracao, Texto, CedulaProfissionalM, IDUtilizador, EAbusiva)
        VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()

        connection.prepareStatement(sql).use { ps ->

            ps.setInt(1, interacao.id)
            ps.setDate(2, interacao.data)
            ps.setString(3, interacao.texto)
            ps.setString(4, interacao.cedulaModerador)
            ps.setInt(5, interacao.idUtilizador)

            if (interacao.abusiva != null) {
                ps.setBoolean(6, interacao.abusiva)
            } else {
                ps.setNull(6, java.sql.Types.BOOLEAN)
            }

            ps.executeUpdate()
        }
    }
}
```
**QueriesJDBC**: Declarada a class QueriesJDBC que utiliza a interface QueriesInterface. Implementa todas as funções declaradas na interface sem reflect (Implementação original do trabalho).

---

### Reflection
**InsertsReflection**: Declarada a class InsertsReflection que utiliza a interface InsertsInterface. Implementa a função insertInteracao utilizando reflection a partir da função **buildInsert**.
```Kotlin
class InsertsReflection(private val connection: Connection) : InsertsInterface {
    override fun insertInteracao(interacao: Interacao) {
        val (sql, values) = buildInsert(interacao, "INTERACAO")

        connection.prepareStatement(sql).use { ps ->
            values.forEachIndexed { index, value ->
                if (value != null) {
                    ps.setObject(index + 1, value)
                } else {
                    ps.setNull(index + 1, Types.NULL)
                }
            }
            ps.executeUpdate()
        }
    }
}
```
##### InsertBuilder
```Kotlin
fun <T: Any> buildInsert(obj: T, tableName: String): Pair<String, List<Any?>> {
    val kClass = obj::class
    val properties = kClass.memberProperties

    val columns = properties.map {
        it.findAnnotation<Column>()?.columnName ?: it.name
    }

    val values = properties.map {
        it.getter.call(obj)
    }

    val sqlString = columns.joinToString(", ") { "?" }
    val sql = """
        INSERT INTO $tableName 
            (${columns.joinToString(", ")})
            VALUES ($sqlString)
    """.trimIndent()

    return Pair(sql, values)
}
```
**QueriesReflection**: Declarada a class QueriesReflection que utiliza a interface QueriesInterface. Implementa as funções da interface utilizando reflection. Faz uso da função **toClassFormatter** no retorno da Lista de Queries.
##### toClassFormatter
```Kotlin
fun <T: Any> toClassFormatter(rs: ResultSet, clazz: KClass<T>): List<T> {
    val resultList = mutableListOf<T>()
    val constructor = clazz.constructors.first()

    while (rs.next()) {
        val args = constructor.parameters.map { param ->
            val property = clazz.members
                .first { it.name == param.name }
            val columnName = property.findAnnotation<Column>()?.columnName ?: param.name

            rs.getObject(columnName)
        }

        val obj = constructor.call(*args.toTypedArray())
        resultList.add(obj)
    }

    return resultList
}
```