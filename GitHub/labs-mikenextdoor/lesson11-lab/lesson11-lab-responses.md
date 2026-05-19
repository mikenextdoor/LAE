# lesson11-lab Answers

# Part 2: Exploring Reflection in Java
1. **Implement the extension function Any.fieldsToJson(): String, which converts the values of declared fields of a given object to JSON.**  
This function should use the Java Reflection API to inspect fields.  
R -> **Code**:
    ``` Kotlin
    fun Any?.fieldsToJson(): String {
        if (this == null) return "null"
        if (this is String) return "\"${slashFix(this)}\""
        if (this is Number || this is Boolean) return this.toString()

        val javaClass = this::class.java

        val fields = javaClass.declaredFields
            .sortedBy { it.name }.map {
                it.isAccessible = true
                "\"${it.name}\":${it.get(this).fieldsToJson()}"
            }

        return "{${fields.joinToString(",")}}"

    }

    private fun slashFix(string: String): String {
        return string
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
    }
    ```
    The slashFix function was created to make the replace logic easier to visualize.

2. **Implement a unit test that validates the functionality of the fieldsToJson() function for a domain object with only String and primitive types as properties.**  
The test should create an instance of the domain object, call the fieldsToJson() function, and assert that the resulting JSON string correctly represents the field names and their corresponding values.  
R -> **Code**:
    ``` Kotlin
    import org.junit.jupiter.api.Assertions.assertEquals
    import org.junit.jupiter.api.Test

    class JsonReflectionTests {
        @Test //String
        fun testString() {
            data class Player(val name: String, val position: String)

            val player = Player("Ronaldo", "Ponta de Lança")

            val json = player.fieldsToJson()

            assertEquals("{\"name\":\"Ronaldo\",\"position\":\"Ponta de Lança\"}", json)
        }

        @Test //Int, Char, Long, Boolean
        fun testPrimitives() {
            data class CarInfo(val carId: Int, val carLetter: Char, val carWeight: Double, var carUsed: Boolean)

            val car = CarInfo(365, 'F', 1.485, false)

            val json = car.fieldsToJson()

            assertEquals("{\"carId\":365,\"carLetter\":\"F\",\"carWeight\":1.485,\"carUsed\":false}", json)
        }
    }
    ```

3. **Enhance the fieldsToJson() function to handle non-primitive types, also including iterables (i.e. Iterable\<T>).**  
The function should be able to convert the fields of these non-primitive types into JSON as well, by recursively inspecting their fields and values.  
For iterables, the function should convert each element to JSON and include it in the resulting JSON string.  
For other reference types, it should recursively call fieldsToJson() on the instance to convert its fields to JSON.  
R -> **Code**:
    ``` Kotlin
    fun Any?.fieldsToJson(): String {
        if (this == null) return "null"
        if (this is String) return "\"${slashFix(this)}\""
        if (this is Number || this is Boolean) return this.toString()
        if (this is Char) return "\"${slashFix(this.toString())}\""

        if (this is Iterable<*>) {
            return "[" + this.joinToString(",") { it.fieldsToJson() } + "]"
        }

        if (this is Array<*>) {
            return "[" + this.joinToString(",") { it.fieldsToJson() } + "]"
        }

        val KClass = this::class.java

        val fields = KClass.declaredFields
            .sortedBy { it.name }.map {
                it.isAccessible = true
                "\"${it.name}\":${it.get(this).fieldsToJson()}"
            }

        return "{${fields.joinToString(",")}}"

    }
    ```

4. **Implement a unit test that validates the enhanced functionality of the fieldsToJson() function for a domain object that contains non-primitive types as fields, including iterables and other reference types.**  
R -> **Code**:
    ``` Kotlin
    @Test //List, Array
    fun testEnhanced() {
        data class Player(val name: String, val position: String)
        data class Score(val score: String)
        data class Team(val name: String, val players: List<Player>, val scores: Array<Score>)

        val Pizzi = Player("Pizzi", "Médio")
        val BenficaNacional = Score("Benfica 10 - 0 Nacional")
        val Benfica = Team("Benfica", listOf(Pizzi), arrayOf(BenficaNacional))

        val json = Benfica.fieldsToJson()
        
        assertEquals("{\"name\":\"Benfica\",\"players\":[{\"name\":\"Pizzi\",\"position\":\"Médio\"}],\"scores\":[{\"score\":\"Benfica 10 - 0 Nacional\"}]}", json)
    }
    ```

# Part 3: Exploring Reflection in Kotlin
1. **Implement the extension function membersToJson using the Kotlin Reflection API.**  
This function encodes properties and non-void parameterless functions.  
Follow the same steps as in Part 2 to implement and test the membersToJson() function, ensuring that it correctly converts the properties and parameterless functions of a given object into JSON format, for both primitive and non-primitive types, including iterables.
R -> **Code**:
    ``` Kotlin
    import kotlin.reflect.full.memberFunctions
    import kotlin.reflect.full.memberProperties

    fun Any?.membersToJson(): String {
        if (this == null) return "null"
        if (this is String) return "\"${slashFix(this)}\""
        if (this is Number || this is Boolean) return this.toString()
        if (this is Char) return "\"${slashFix(this.toString())}\""

        if (this is Iterable<*>) {
            return "[" + this.joinToString(",") { it.membersToJson() } + "]"
        }

        if (this is Array<*>) {
            return "[" + this.joinToString(",") { it.membersToJson() } + "]"
        }

        val KClass = this::class

        val properties = KClass.memberProperties.map {
            val value = it.call(this)
            "\"${it.name}\":${value.membersToJson()}"
        }

        val functions = KClass.memberFunctions
            .filter { it.parameters.size == 1 }
            .filter { it.returnType.toString() != "kotlin.Unit" }
            .filter { it.name != "toString" }
            .filter { it.name != "hashCode" }
            .filter { it.name != "equals" }
            .filter { !in.name.contains ("component") }
            .map {
                val value = it.call(this)
                "\"${it.name}\":${value.membersToJson()}"
            }

        val all = (functions + properties).sorted()

        return "{${all.joinToString(",")}}"
    }
    ```
    The ``slashFix`` implementation is the same as the ``Part 2`` one.  
    **Tests**:
    ``` Kotlin
    class KotlinReflectionTests {
        @Test //String
        fun testString() {
            data class Player(val name: String, val position: String)

            val player = Player("Ronaldo", "Ponta de Lança")

            val json = player.membersToJson()

            assertEquals("{\"name\":\"Ronaldo\",\"position\":\"Ponta de Lança\"}", json)
        }

        @Test //Int, Char, Long, Boolean
        fun testPrimitives() {
            data class CarInfo(val carId: Int, val carLetter: Char, val carWeight: Double, var carUsed: Boolean)

            val car = CarInfo(365, 'F', 1.485, false)

            val json = car.membersToJson()

            assertEquals("{\"carId\":365,\"carLetter\":\"F\",\"carWeight\":1.485,\"carUsed\":false}", json)
        }

        @Test //List, Array
        fun testEnhanced() {
            data class Player(val name: String, val position: String)
            data class Score(val score: String)
            data class Team(val name: String, val players: List<Player>, val scores: Array<Score>)

            val Pizzi = Player("Pizzi", "Médio")
            val BenficaNacional = Score("Benfica 10 - 0 Nacional")
            val Benfica = Team("Benfica", listOf(Pizzi), arrayOf(BenficaNacional))

            val json = Benfica.membersToJson()

            assertEquals("{\"name\":\"Benfica\",\"players\":[{\"name\":\"Pizzi\",\"position\":\"Médio\"}],\"scores\":[{\"score\":\"Benfica 10 - 0 Nacional\"}]}", json)
        }
    }
    ```
    These are the same tests as in Part 2, but all the `fieldsToJson()` calls are now `membersToJson()`.