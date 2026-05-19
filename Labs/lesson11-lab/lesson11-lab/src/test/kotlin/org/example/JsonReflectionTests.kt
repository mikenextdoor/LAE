package org.example

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
}