package org.example

import junit.framework.TestCase.assertEquals
import org.example.EstatisticaAbusoBaseLineTest.Companion
import org.example.classes.*
import org.example.classes.EstatisticaAbuso
import org.example.classes.Interacao
import org.example.reflection.QueriesReflectionDynamic
import org.h2.jdbcx.JdbcDataSource
import org.junit.BeforeClass
import reflection.Column
import reflection.buildDynamicClass
import java.sql.Connection
import java.sql.Date
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.test.Test
import kotlin.test.assertNotNull

class DynamicTest {
    companion object {
        private val connection: Connection =
            JdbcDataSource() // Create an H2 in-memory DataSource and initialise the schema + seed data
                .apply {
                    setURL("jdbc:h2:mem:benchdb;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:h2-init.sql'")
                    user = "sa"
                    password = ""
                }.connection

        @BeforeClass
        @JvmStatic
        fun setup() {
            connection.createStatement().execute("DELETE FROM INTERACAO")
            connection.createStatement().execute("""
                INSERT INTO INTERACAO VALUES
                (1, DATE '2026-05-14', 'Mensagem 1', 'CEDULA', 42, TRUE),
                (2, DATE '2026-05-14', 'Mensagem 2', 'CEDULA', 42, FALSE),
                (3, DATE '2026-05-14', 'Mensagem 3', 'CEDULA', 42, NULL)
            """.trimIndent())

            connection.createStatement().execute("DELETE FROM ESTATISTICA_ABUSO")
            connection.createStatement().execute("""
                INSERT INTO ESTATISTICA_ABUSO VALUES
                (FALSE, 5),
                (TRUE, 1),
                (TRUE, 2)
            """.trimIndent())

            connection.createStatement().execute("DELETE FROM CASOS_DE_CYBERBULLYING")
            connection.createStatement().execute("""
                INSERT INTO CASOS_DE_CYBERBULLYING VALUES
                (
                    99,
                    DATE '2026-05-14',
                    DATE '2026-05-15',
                    'Descricao',
                    'Area Atuacao',
                    'Anotacoes',
                    4,
                    DATE '2026-05-15',
                    'Texto AD',
                    'Cedula');
            """.trimIndent())
        }
    }

    val dynamic = QueriesReflectionDynamic(connection)

    /***
     * falta alterar o buildDynamicClass para ser geral e nao apenas da class EstatisticaAbuso, os testes abaixo
     * sao apenas para verificar se esta a funcionar corretamente com EstatisticaAbuso
     */
    @Test
    fun `verify if parameters are right`() {
        val ctor = Interacao::class.constructors.first()

        val properties = Interacao::class.memberProperties.toList()

        assertEquals(6, properties.size)

        ctor.parameters.forEach { param ->
            val member = properties.first { it.name == param.name }
            val columnName = member.findAnnotation<Column>()?.columnName
                ?: member.name

            /*println(columnName)
            println(member.findAnnotation<Column>()?.columnName)*/
            assertEquals(columnName, member.findAnnotation<Column>()?.columnName)
        }

    }

    @Test
    fun `returns abusive interactions expected`() {
        val result = dynamic.getNumeroInteracoesAbusivas()
        assertEquals(2, result.size)
    }

    @Test
    fun `formatter not null expected`() {
        val formatter = buildDynamicClass(EstatisticaAbuso::class)
        assertNotNull(formatter)
    }

    @Test
    fun `matching mmembers`() {
        val formatter = buildDynamicClass(EstatisticaAbuso::class)
        assertEquals(
            listOf("EAbusiva", "Total"),
            formatter.columnNames
        )
    }

    @Test
    fun `teste do  buildDynamicClass`() {
        val formatter  =  buildDynamicClass(EstatisticaAbuso::class)
        val rs = connection.createStatement()
            .executeQuery("SELECT EAbusiva, Total FROM ESTATISTICA_ABUSO")

        val result = formatter.toClassFormatter(rs)

        assertEquals(3, result.size)
        assertEquals(false, result[0].abusiva)
        assertEquals(5L, result[0].total)
        assertEquals(true, result[1].abusiva)
        assertEquals(1, result[1].total)
        assertEquals(true, result[2].abusiva)
        assertEquals(2, result[2].total)

    }


    /***
     * Erro: java.lang.NoSuchMethodError: 'java.lang.Integer java.sql.ResultSet.getInt(java.lang.String)'
     * getInt nao retorna Integer, diz que method nao existe
     * provavelmente temos que fazer unbox
     *
     *
     * FOI CORRIGIDO, REMOVEMOS TODAS AS POSSIBILIDADES DE TER VALORES NULL
     * O teste ja correu com sucesso
     */
    @Test
    fun `getCasoById test`() {
        val formatter = buildDynamicClass(CasoDetalhado::class)

        connection.createStatement().execute("""
        INSERT INTO CASOS_DE_CYBERBULLYING VALUES
        (1, DATE '2026-05-19', DATE '2026-05-20', 'DESCRIÇÃO', 'AREA DE ATUACAO', 'ANOTACOES', 1, DATE '2026-05-20', 'TEXTOAD', 'CEDULA'),
        (2, DATE '2026-05-19', DATE '2026-05-20', 'DESCRIÇÃO', 'AREA DE ATUACAO', 'ANOTACOES', 1, DATE '2026-05-20', 'TEXTOAD', 'CEDULA'),
        (1, DATE '2026-05-19', DATE '2026-05-20', 'DESCRIÇÃO', 'AREA DE ATUACAO', 'ANOTACOES', 1, DATE '2026-05-20', 'TEXTOAD', 'CEDULA'),
        (2, DATE '2026-05-19', DATE '2026-05-20', 'DESCRIÇÃO', 'AREA DE ATUACAO', 'ANOTACOES', 1, DATE '2026-05-20', 'TEXTOAD', 'CEDULA'),
        (1, DATE '2026-05-19', DATE '2026-05-20', 'DESCRIÇÃO', 'AREA DE ATUACAO', 'ANOTACOES', 1, DATE '2026-05-20', 'TEXTOAD', 'CEDULA'),
        (2, DATE '2026-05-19', DATE '2026-05-20', 'DESCRIÇÃO', 'AREA DE ATUACAO', 'ANOTACOES', 1, DATE '2026-05-20', 'TEXTOAD', 'CEDULA')
        """.trimIndent()
        )

        val dynamic = QueriesReflectionDynamic(connection)

        val cases = dynamic.getCasoById(1)

        assertEquals(CasoDetalhado(1, Date.valueOf("2026-05-19"), Date.valueOf("2026-05-20"), "DESCRIÇÃO", "AREA DE ATUACAO", "ANOTACOES", 1, Date.valueOf("2026-05-20"), "TEXTOAD", "CEDULA"), cases)
    }
}