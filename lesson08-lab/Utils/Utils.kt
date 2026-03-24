fun cross(source: Iterable<Any>, transform: (Any) -> Int): Iterable<Int> {
    return source.map {
        println("Crossing $it")
        transform(it)
    }
}

fun main() {
    cross(listOf("ola", "isel", "super")) { it.toString().length }
        .forEach(::println)
}