package pt.isel

import kotlin.test.Test
import kotlin.test.assertEquals

/*
 * Located on testes resources
 */
private const val weatherPath = "q-Lisbon_format-csv_date-2020-05-08_enddate-2020-06-11.csv"

class TestWeatherTemperatures {
    fun loadNaiveCsv(): List<Weather> =
        ClassLoader
            .getSystemResource(weatherPath)
            .openStream()
            .reader()
            .readLines() // List<String>
            .filter { !it.startsWith('#') } // Filter comments
            .drop(1) // Skip line: Not available
            .filterIndexed { index, _ -> index % 2 != 0 } // Filter hourly info
            .map { it.fromCsvToWeather() } // List<Weather>

    private val weatherData = loadNaiveCsv()

    @Test
    fun `check data`() {
        weatherData
            .forEach { println(it) }
    }

    @Test
    fun `count distinct descriptions in rainy days map filter`() {
        var iters = 0
        val size =
            weatherData
                .filter {
                    iters++
                    it.weatherDesc.lowercase().contains("rain")
                }.map {
                    iters++
                    it.weatherDesc
                }.distinct()
                .count()

        println(iters)

        assertEquals(5, size)
    }

    @Test
    fun `count distinct descriptions in rainy days map filter Custom`() {
        var iters = 0
        val size =
            weatherData
                .eagerFilter {
                    iters++
                    it.weatherDesc.lowercase().contains("rain")
                }.eagerMap {
                    iters++
                    it.weatherDesc
                }.eagerDistinct()
                .count()

        println(iters)

        assertEquals(5, size)
    }

    @Test
    fun `count distinct descriptions in rainy days filter map`() {
        var iters = 0
        val size =
            weatherData
                .map {
                    iters++
                    it.weatherDesc
                }.filter {
                    iters++
                    it.lowercase().contains("rain")
                }.distinct()
                .count()

        println(iters)

        assertEquals(5, size)
    }

    @Test
    fun `count distinct descriptions in rainy days filter map Custom`() {
        var iters = 0
        val size =
            weatherData
                .eagerMap {
                    iters++
                    it.weatherDesc
                }.eagerFilter {
                    iters++
                    it.lowercase().contains("rain")
                }.eagerDistinct()
                .count()

        println(iters)

        assertEquals(5, size)
    }

    @Test
    fun `first description in windy days`() {
        var iters = 0
        val desc =
            weatherData
                .filter { // List<Weather>
                    iters++
                    println("Filtering $it")
                    it.windspeedKmph > 22
                }.map { // List<String>
                    iters++
                    println("Mapping $it")
                    it.weatherDesc
                }.first()
        assertEquals("Light rain shower", desc)
        assertEquals(37, iters)
    }

    @Test
    fun `first description in windy days Lazy`() {
        var iters = 0
        val desc =
            weatherData
                .asSequence()
                .filter { // List<Weather>
                    iters++
                    println("Filtering $it")
                    it.windspeedKmph > 22
                }.map { // List<String>
                    iters++
                    println("Mapping $it")
                    it.weatherDesc
                }.first()
        //println(iters)
        assertEquals("Light rain shower", desc)
        assertEquals(5, iters)
    }

    @Test
    fun `first description in windy days Lazy Custom`() {
        var iters = 0
        val desc =
            weatherData
                .asSequence()
                .lazyFilter { // List<Weather>
                    iters++
                    println("Filtering $it")
                    it.windspeedKmph > 22
                }.lazyMap { // List<String>
                    iters++
                    println("Mapping $it")
                    it.weatherDesc
                }.first()
        //println(iters)
        assertEquals("Light rain shower", desc)
        assertEquals(5, iters)
    }

    @Test
    fun `count distinct descriptions in rainy days filter map Custom Lazy`() {
        var iters = 0
        val size =
            weatherData
                .asSequence()
                .lazyFilter {
                    iters++
                    it.weatherDesc.lowercase().contains("rain")
                }.lazyMap {
                    iters++
                    it.weatherDesc
                }.lazyDistinct()
                .count()

        println(iters)

        assertEquals(5, size)
    }
}