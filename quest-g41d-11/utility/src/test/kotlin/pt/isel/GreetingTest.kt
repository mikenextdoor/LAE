package pt.isel

import kotlin.test.Test
import kotlin.test.assertNotNull

class GreetingTest {
    @Test
    fun appHasAGreeting() {
        val classUnderTest = Greeting()
        assertNotNull(classUnderTest.greeting, "app should have a greeting")
    }
}
