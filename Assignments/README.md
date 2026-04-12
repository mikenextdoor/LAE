# LAE 2026 Assignments

Languages and Managed Runtimes 2026 Assignments

## Assignments

**Deadlines**:
* [Assignment 1 - March 30 - Kotlin Reflection - Description](#assignment-1---kotlin-reflection---description)
* [Assignment 2 - April 13 - Kotlin Reflection - Implementation](#assignment-2---kotlin-reflection---implementation)
* [Assignment 3 - May 4 - Dynamic generation of bytecode](#assignment-3---dynamic-generation-of-bytecode)
* [Assignment 4 - May 25 - Lazy Sequences](#assignment-4---lazy-sequences)

## Scope

Along these assignments, you will be working on a library for managing access to
a relational database using JDBC based on the domain entities definition.
This library should be managed on the `utility` module of this repository and
will be used by the `app` module to perform operations on the database.
**All modules should include unit tests to verify the correct behavior of the
implemented features**.

The `bench` module is intended to perform micro-benchmarks to compare the
performance of the original ad hoc implementation with the reflective
(Assignment 2) and the dynamic implementation (Assignment 3).

The first commits in this repository should consist of copying the baseline
implementation of the Software Laboratory project into the `app` module.

Students who have neither completed nor are currently working on the Software
Laboratory project should propose an alternative application from another
curricular unit, such as ISI or TDS, that includes a data access layer and
domain entities.

## Assignment 1 - Kotlin Reflection - Description

Make a description of which part of the code of your `app` should be refactored
to use reflection, so that it can be reused for different domain entities
without the need for code duplication.  
Identify the files, classes, methods and/or functions that will be affected by
this refactoring, and describe the changes that will be made to each of them.

For example, you should take advantage of the Reflect API to build SQL statements
and to parse the `ResultSet` into an instance of the domain class, without the need to
write specific code for each domain class (e.g., `User`, `Channel`, etc.).

You should also provide instructions on how to configure, setup and run the
application.

NOTE all instructions should be limited to the use of terminal and standard
command line tools, like `./gradlew`, without the need for using an IDE.

## Assignment 2 - Kotlin Reflection - Implementation

### 2.1. - Refactor the code to use Kotlin Reflection

Implement the refactoring described in Assignment 1, using the Kotlin Reflection
API to create a generic implementation that can manage different domain entities
without code duplication, according to the next requirements:
1. The use of reflection should be limited to the module `utility`, and the
   `app` module should only depend on the `utility` module, without any direct
   dependency on the reflection API.
2. The data access layer of the `app` module should be instantiated in such a
   way that it can be easily switched between the ad hoc implementation and the
   new reflective implementation.
3. Both the `utility` and `app` modules should include unit tests to verify the
   correct behavior of the implemented features, and to ensure that the
   refactoring does not introduce any regressions.

### 2.2 - Micro-benchmarking

Implement a micro-benchmarking in `Bench.kt` of the `bench` module to compare
the performance of the new Reflect Based implementation with the original ad hoc
implementation.

Your analysis should include different domain entities with different
characteristics, such as regarding the number and types of properties.

Note that the `bench` module should work with an in memory database to avoid
the overhead of IO operations, and to ensure that the performance measurements
are focused on the performance of the data access layer, rather than on the
performance of the database itself.
To that end the `bench` module is already configured to use H2 in-memory database.

Run the micro-benchmarking and register the slowdown of the new Reflect Based
implementation compared to the ad hoc implementation.

To run the benchmark on a local machine you may use the following command:

```
./gradlew jmhJar
```

And then, for example:

```
java -jar bench/build/libs/bench-jmh.jar -i 4 -wi 4 -f 1 -r 2 -w 2
```

* `-i`  4 iterations
* `-wi` 4 warmup iterations
* `-f`  1 fork
* `-r`  2 run each iteration for 2 seconds
* `-w`  2 run each warmup iteration for 2 seconds.


## Assignment 3 - Dynamic generation of bytecode

Following up on Assignment 2 you should implement a new solution that **avoids** the
use of reflection in certain operations such as:
* reading a `ResultSet`;
* reading properties of a domain class;
* instantiating a domain class; 
* ...

To that end, you should instead generate bytecode at runtime through the Class-File
API to replace the use of reflection in these operations. 
etc).

**NOTE** that reflection will continue to be used to read metadata, only ceasing
to be used in operations such as `ctor.callBy(...)`. The instantiation of a
domain class will now be done directly based on code generated at runtime
through the
[Class-File API](https://docs.oracle.com/en/java/javase/25/vm/jvm-apis.html).

**NOTE you will need at least JDK 22 to use the Class-File API.**

Update the `bench` module to include performance measurements for a dynamically
generated data access layer, and compare its performance with the ad hoc and
reflective implementations.

Your goal should be to reduce the slowdown of the reflective implementation, and to
achieve a performance that is as close as possible to the ad hoc implementation.

## Assignment 4 - Lazy Sequences

In this assignment, you should update your implementation to support
building and executing **lazy SQL queries** through a new `findAll()` method.

To achieve this, allow query clauses to be added incrementally, producing **new
query objects** based on existing ones. The actual SQL execution should only
happen when the result is iterated.

In the following example, note two important aspects:

* You can add additional clauses to an existing query, which **produces a new
  query**.
* The SQL statement is **only executed when you start iterating** over the result
  (e.g., using `forEach`). At that moment, the query will reflect any changes
  made to the database in the meantime, such as newly inserted entries.


```kotlin
val channelsPublicAndReadOnly =
      channels
         .findAll()
         .whereEquals(Channel::type, ChannelType.PUBLIC)
         .whereEquals(Channel::isReadOnly, true)
         .iterator()

// Insert a new public and read-only channel before iterating over the result
ChannelRepositoryJdbc(connection).insert(
   Channel("Surf", ChannelType.PUBLIC, System.currentTimeMillis(), false, 400, 50, true, 0L),
)

// The newly inserted channel will appear during iteration, even though the query was defined earlier
assertEquals("Support", channelsPublicAndReadOnly.next().name)
assertEquals("Surf", channelsPublicAndReadOnly.next().name)
assertFalse { channelsPublicAndReadOnly.hasNext() }
```

This new behavior is defined by the `Queryable` interface shown in the following
listing.  
**Note** that `whereEquals` and `orderBy` can be chained in any order to
build a new query.  
Also note that `Queryable` implements the `Sequence` interface.
["_Unlike collections, sequences don't contain elements, they produce them while
iterating._"](https://kotlinlang.org/docs/sequences.html)

```kotlin
interface Queryable<T> : Sequence<T> {
    fun <V> whereEquals(prop: KProperty1<T, V>, value: V): Queryable<T>

    fun <V> orderBy(prop: KProperty1<T, V>): Queryable<T>
}
```

The `findAll()` should be implemented following the approach below, where all
query-building logic is encapsulated in an auxiliary class named
`QueryableBuilder`, which you must implement as part of this assignment.
The `QueryableBuilder` can implement the *iterator protocol* either by
explicitly implementing the `Iterator` interface or by using the `sequence`
generator function.
You can **optionally** implement **both** approaches.

Note that you should provide to the `QueryableBuilder` any information necessary
to support this functionality, such as metadata or helper functions.
In the following example, `properties` contains all the necessary information
about the properties of the given entity `T`.

```kotlin
fun findAll(): Queryable<T> {
   val sql = "SELECT ... FROM ..."
   return QueryableBuilder(connection, sql, properties)
}
```

**You must also implement unit tests to verify the correct behavior of
`whereEquals` and `orderBy`, including their lazy evaluation semantics.**

You should ensure that closeable resources, such as `PreparedStatement` and
`ResultSet`, are automatically closed when iteration over the returned result
reaches the end.
