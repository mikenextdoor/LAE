fun cross(source: Iterable<Any>, transform: (Any) -> Int): Iterable<Int> {
    return source.map {
        println("Crossing $it")
        transform(it)
    }
}

fun main() {
    cross(listOf("ola", "isel", "super")) { it.toString().length }
        .forEach(::println)

    println("6th Step")

    cross(listOf(true, false, true)) {
      if (it == true) 1 else 0
    }
   .forEach(::println)
}

// Replace <Name of first lambda> with the actual name of the member resulting 
// from the first lambda expression.
//
val funLength = Class
   .forName("UtilsKt")
   .getDeclaredMethod("<Name of first lambda>", Any::class.java) 
   .apply { isAccessible = true }

val funBoolean = Class
    .forName("UtilsKt")
    .getDeclaredMethod("main$lambda$2", java.lang.Boolean.TYPE)
    .apply { isAccessible = true }

val invoke = funBoolean.invoke(null, true)