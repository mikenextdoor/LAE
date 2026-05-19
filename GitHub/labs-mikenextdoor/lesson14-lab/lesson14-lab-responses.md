# lesson14-lab Answers

# Part 1: Custom Property Names
To allow renaming properties in the generated JSON using annotations two implementations were needed: The creation of `ToJsonPropName.kt` and a change inside `KotlinReflection.kt`.
### Implementations
1. **ToJsonPropName.kt**:
    ``` Kotlin
    // By default an Annotation in Kotlin is already Runtime Retention
    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.PROPERTY)
    annotation class ToJsonPropName(
        val propName: String
    )
    ```

2. **KotlinReflection.kt**:
    ``` Kotlin
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

            val annotation = it.findAnnotation<ToJsonPropName>()
            val jsonName = annotation?.propName ?: it.name

            "\"${jsonName}\":${value.membersToJson()}"
        }

        val functions = KClass.memberFunctions
            .filter { it.parameters.size == 1 }
            .filter { it.returnType.toString() != "kotlin.Unit" }
            .filter { it.name != "toString" }
            .filter { it.name != "hashCode" }
            .filter { it.name != "equals" }
            .map {
                val value = it.call(this)
                "\"${it.name}\":${value.membersToJson()}"
            }

        val all = (functions + properties).sorted()

        return "{${all.joinToString(",")}}"
    }
    ```
    The properties was changed from:
    ``` Kotlin
    val properties = KClass.memberProperties.map {
        val value = it.call(this)
        "\"${it.name}\":${value.membersToJson()}"
    }
    ```
    To:
    ``` Kotlin
    val properties = KClass.memberProperties.map {
        val value = it.call(this)

        val annotation = it.findAnnotation<ToJsonPropName>()
        val jsonName = annotation?.propName ?: it.name

        "\"${jsonName}\":${value.membersToJson()}"
    }
    ```
    These changes were needed to check if a property has the ToJsonPropName annotation, if so, use the custom name defined in the annotation. Otherwise, use the original property name.

    To verify that the implementation behaves as expected, the following tests were performed expecting the output `{ "first_name": "John", "last_name": "Doe", "age": 30 }`:
    ``` Kotlin
    @Test //Custom Property Name
    fun testCustomPropName() {
        data class Person(
            @ToJsonPropName("first_name") val firstName: String,
            @ToJsonPropName("last_name") val lastName: String,
            val age: Int
        )
        val person = Person("John", "Doe", 30)
        val json = person.membersToJson()
        assertEquals("{\"age\":30,\"first_name\":\"John\",\"last_name\":\"Doe\"}", json)
    }
    ``` 

# Part 2: Custom Value Formatters
### Implementations
These changes were needed to check if a property has a ToJsonFormatter annotation. 
If so, the formatter class is instantiated to produce a custom JSON representation. Otherwise, the default recursive serialization is used.
1. **ToJsonFormatter.kt**:
    ``` Kotlin
    // By default an Annotation in Kotlin is already Runtime Retention
    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.PROPERTY)
    annotation class ToJsonFormatter(
        val formatter: KClass<out (Any) -> String>
    )
    ```

2. **KotlinReflection.kt**:
The only change mande was in the ``val properties``:
    ``` Kotlin
    val properties = KClass.memberProperties.map {
        val annotation = it.findAnnotation<ToJsonFormatter>()
        val value = it.call(this)
        val jsonValue = if (annotation != null) {
            if (value == null) "null"
            else {
                val formatterClass = annotation.formatter
                val instance = formatterClass.constructors.first().call() as (Any) -> String
                "\"${instance(value)}\""
            }
        } else {
            value?.membersToJson() ?: "null"
        }
        "\"${it.name}\":${jsonValue}"
    }
    ```
### Tests
1. **To match the expected output by the ``Lab Guide`` the following test was made**:
    ``` Kotlin
    class DateFormatter : (Any) -> String {
        override fun invoke(value: Any): String {
            val date = value as LocalDate
            return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }
    }

    data class Student(
        val name: String,
        val age: Int,
        @ToJsonFormatter(DateFormatter::class)
        val enrollmentDate: LocalDate
    )

    fun main() {
        val student = Student("ZE", 20, LocalDate.of(2023, 3, 15))
        println(student.membersToJson())
    }
    ```
    #### Expected Output:
    `{"name": "ZE", "age": 20, "enrollmentDate": "2023-03-15"}`
    #### Output Obtained:
    `{"age":20,"enrollmentDate":"2023-03-15","name":"ZE"}`
    The order of the instances is not the same due to the ``sorted()`` function called in the `membersToJson` function.

2. **As asked in the `Lab Guide` we should implement a different unit test for a different kind of formatter.**
    ``` Kotlin
    class UpperCaseFormatter : (Any) -> String {
        override fun invoke(value: Any): String {
            val text = value.toString().uppercase()
            return text
        }
    }

    data class Person(
        @ToJsonFormatter(UpperCaseFormatter::class) val firstName: String,
        val lastName: String
    )

    fun main() {
        val person = Person("Miguel", "Silva")
        println(person.membersToJson())
    }
    ```

    As expected, the output matches the expected one:
    #### Expected Output:
    `{"firstName":"MIGUEL","lastName":"Silva"}`
    #### Output Obtained:
    `{"firstName":"MIGUEL","lastName":"Silva"}`