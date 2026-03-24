import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

data class Person(val name: String, val age: Int, val team: String)

class Part2Tests {
    @Test
    fun testJsonReflection() {
        val person = Person("Pizzi", 34, "Estoril Praia")
        val result = person.fieldsToJson()
        val expected = "{\"name\":\"Pizzi\",\"age\":34,\"team\":\"Estoril Praia\"}"
        assertEquals(expected, result)
    }

    data class Address(val city: String)
    data class Pessoa(val name: String, val address: Address)

    @Test
    fun testClassClass() {
        val person = Pessoa("André Almeida", Address("Benfica"))

        val result = person.fieldsToJson()

        val expected = "{\"name\":\"André Almeida\",\"address\":{\"city\":\"Benfica\"}}"

        assertEquals(expected, result)
    }

    data class Team(val players: List<Person>)

    @Test
    fun testIterableClass() {
        val team = Team(listOf(Person("Pizzi", 34, "Benfica"), Person("Cristiano Ronaldo", 41, "Al-Nassr")))

        val result = team.fieldsToJson()

        val expected = "{\"players\":[{\"name\":\"Pizzi\",\"age\":34,\"team\":\"Benfica\"},{\"name\":\"Cristiano Ronaldo\",\"age\":41,\"team\":\"Al-Nassr\"}]}"

        assertEquals(expected, result)
    }
}