package pt.isel

/**
 * Returns a list containing the results of applying the given
 * transform function to each element in the original collection.
 */
fun <T, R> Iterable<T>.eagerMap(transform: (T) -> R): List<R> {
    val destination = mutableListOf<R>()
    for (item in this) {
        destination.add(transform(item))
    }
    return destination
}

/**
 * Returns a list containing only elements matching the given predicate.
 */
fun <T> Iterable<T>.eagerFilter(predicate: (T) -> Boolean): Iterable<T> {
    val destination = mutableListOf<T>()
    for (item in this) {
        if (predicate(item)) {
            destination.add(item)
        }
    }
    return destination
}

/**
 * Returns a list containing only distinct elements from the given collection.
 */
fun <T> Iterable<T>.eagerDistinct(): Iterable<T> {
    val destination = mutableSetOf<T>()
    for (item in this) {
        destination.add(item)
    }
    return destination
}

/**
 * Returns a sequence containing the results of applying the given
 * transform function to each element in the original collection.
 */
fun <T, R> Sequence<T>.lazyMap(transform: (T) -> R): Sequence<R> {
    return object : Sequence<R> {
        override fun iterator(): Iterator<R> {
            val upstream = this@lazyMap.iterator()
            return object : Iterator<R> {

                override fun hasNext(): Boolean {
                    return upstream.hasNext()
                }

                override fun next(): R {
                    return transform(upstream.next())
                }
            }
        }
    }
}

/**
 * Returns a sequence containing only elements matching the given [predicate].
 * Simplistic implementation that does not work with nullable items.
 */
fun <T> Sequence<T>.lazyFilter(predicate: (T) -> Boolean): Sequence<T> {
    return object : Sequence<T> {
        override fun iterator(): Iterator<T> {
            val upstream = this@lazyFilter.iterator()
            return object : Iterator<T> {
                private var nextItem: T? = null
                private var nextReady = false

                override fun hasNext(): Boolean {
                    while (upstream.hasNext()) {
                        val item = upstream.next()
                        if (predicate(item)) {
                            nextItem = item
                            nextReady = true
                            return true
                        }
                    }
                    return false
                }

                override fun next(): T {
                    if (!nextReady && !hasNext()) {
                        throw NoSuchElementException()
                    }
                    nextReady = false
                    return nextItem as T
                }
            }
        }
    }
}

/**
 * Returns a sequence containing only distinct elements from the given collection.
 */
fun <T> Sequence<T>.lazyDistinct(): Sequence<T> {
    return object : Sequence<T> {
        override fun iterator(): Iterator<T> {
            val upstream = this@lazyDistinct.iterator()
            return object : Iterator<T> {
                private val repeat = mutableListOf<T>()
                private var nextItem: T? = null
                private var nextReady = false

                override fun hasNext(): Boolean {
                    while (upstream.hasNext()) {
                        val item = upstream.next()
                        if (!repeat.contains(item)) {
                            repeat.add(item)
                            nextItem = item
                            nextReady = true
                            return true
                        }
                    }
                    return false
                }

                override fun next(): T {
                    if (!nextReady && !hasNext()) {
                        throw NoSuchElementException()
                    }
                    nextReady = false
                    return nextItem as T
                }

            }
        }
    }
}

/**
 * Returns a sequence containing all elements of original sequence and
 * then all elements of the given elements sequence.
 */
fun <T> Sequence<T>.lazyConcat(other: Sequence<T>): Sequence<T> {
    return object : Sequence<T> {
        override fun iterator(): Iterator<T> {
            val upstream = this@lazyConcat.iterator()
            val other = other.iterator()
            return object : Iterator<T> {
                override fun hasNext(): Boolean {
                    return upstream.hasNext() || other.hasNext()
                }

                override fun next(): T {
                    return if (upstream.hasNext()) upstream.next()
                    else other.next()
                }
            }
        }
    }
}

/**
 * Merges series of adjacent elements.
 */
private object NONE
fun <T : Any?> Sequence<T>.lazyCollapse(): Sequence<T> {
    return object : Sequence<T> {
        override fun iterator(): Iterator<T> {
            val upstream = this@lazyCollapse.iterator()
            return object : Iterator<T> {
                private val repeat = mutableListOf<T>()
                private var nextItem: T? = null
                private var nextReady = false
                private var prevItem: Any? = NONE

                override fun hasNext(): Boolean {
                    if (nextReady) return true
                    while (upstream.hasNext()) {
                        val item = upstream.next()
                        if (prevItem === NONE || item != prevItem) {
                            nextItem = item
                            nextReady = true
                            prevItem = item
                            return true
                        }
                    }
                    return false
                }

                override fun next(): T {
                    if (!nextReady && !hasNext()) throw NoSuchElementException()
                    nextReady = false
                    return nextItem as T
                }

            }
        }
    }
}

/**
 * Returns a sequence of values built from the elements of `this` sequence and the [other] sequence with the same index
 * using the provided [transform] function applied to each pair of elements.
 * The resulting sequence ends as soon as the shortest input sequence ends.
 */
fun <T, R, V> Sequence<T>.lazyZip(
    other: Sequence<R>,
    transform: (a: T, b: R) -> V,
): Sequence<V> {
    return object : Sequence<V> {
        override fun iterator(): Iterator<V> {
            val upstream = this@lazyZip.iterator()
            val other = other.iterator()
            return object : Iterator<V> {
                override fun hasNext(): Boolean {
                    return upstream.hasNext() && other.hasNext()
                }

                /**
                 * Returns the next element in the iteration.
                 *
                 * @throws NoSuchElementException if the iteration has no next element.
                 */
                override fun next(): V {
                    return transform(upstream.next(), other.next())
                }
            }
        }
    }
}