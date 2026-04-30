fun Foo(x: Int, y: Float, z: Int, w: Float): Float {
    return (x - (x * y) + z) - w
}

fun main() {
    println(Foo(7, 0.03f, 3, 1.25f))
}