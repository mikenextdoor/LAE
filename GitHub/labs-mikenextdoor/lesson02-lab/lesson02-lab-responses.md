# lesson02-lab Answers

# Part 1: Simple Java & Kotlin Interoperability

## Step 1: Java classes

1. **Compile Foo.java using javac Foo.java.**
    * How many .class files are generated? Why?  
R -> 3 .class files are generated: `X.class`, `Y.class` and `Z.class`. This happens because inside Foo.java there are 3 top-level types.
2. **Compile App.java using javac App.java.**
    * How many .class files are generated? Why?  
R -> Only 1 .class file is generated: App.class. There is only 1 top-level type declared inside App.java: `public class App`.
3. **Remove the file Y.class, then compile App.java again using javac App.java.**
    * Do you observe any errors?
    * If so, why does the error occur? If not, explain why.  
R -> No errors are observed. The `Y class` is not called inside the `App class`, so deleting the `Y.class` makes no impact in the compile process.
4. **Remove the file Z.class, then compile App.java again using javac App.java.**
    * Do you observe any errors?
    * If so, why does the error occur? If not, explain why.  
R -> Yes, one error occurs. This happens because the `Z class` is called inside the `App class` but it's `.class` does not exist.
        ```
        App.java:9: error: cannot find symbol
                new Z().print();
                    ^
        symbol:   class Z
        location: class App
        ```
5. **Compile both Foo.java and App.java again.**  
Then remove the files Y.class and Z.class.  
Run the application using java App.
    * Do you observe any errors? Why?  
R -> No errors are observed. Using `java App` only the `main` method runs, this means the only class called is the `X` class. So, no errors occur.
6. **Remove the file X.class. Then run the application using java App.** 
    * At what moment do you observe an error (if any)?
    * Why does this error occur?  
R -> 1 errors occurs after the `Press ENTER to proceed` String. It occurs, because in the `main` method after the `println` the `X` class is called. Since it was deleted before using `java App` it gives an error.
        ```
        public static void main(String[] args) {
            System.out.println("Press ENTER to proceed.");
            System.console().readLine();
            new X().print();
        }

        CMD: Press ENTER to proceed.

        Error: Exception in thread "main" java.lang.NoClassDefFoundError: X
        at App.main(App.java:6)
        ```
7. **Compile Foo.java again and run App without recompiling it.**  
Then modify the print method of class X so that it prints "I am X version 2".  
Recompile only Foo.java, and run the application again using java App without recompiling App.java.
    * Explain the observed output.  
R -> The observed output is: 
        ```
        Press ENTER to proceed.

        I am X version 2
        ```
        This output is the result of modifying the print method of the ``X class``.

## Step 2: Kotlin main file
1. **Compile Kotlin using kotlinc App.kt.**
    * Do you observe any errors?
    * If so, why does the error occur? If not, explain why.  
R -> Yes, one error is observed:
        ```
        App.kt:4:5: error: unresolved reference 'X'.
            X().print()    // Using Java class
            ^
        ```
        This error occurs because no classpath is declared, so, the ``Kotlin Compiler`` doesn't know about the ``X class`` existence. So it says ``unresolved reference 'X'``.
2. **Compile Kotlin again, specifying classpath with kotlinc -cp . App.kt.**
    * What .class file does Kotlin generate for the top-level function?  
R -> Kotlin generates the `AppKt.class` for App.kt.
3. **Run the application using kotlin AppKt.**  
Run the application again but now using java AppKt.
    * Explain the reason of the observed error?  
R -> An error only occurs when using `java AppKt`. This happens because `kotlin` already includes the Standard Kotlin library, otherwise `java` doesn't.
4. **Look for the location folder of the Kotlin SDK installation and include the path to the kotlin-stdlib.jar in classpath (through -cp option) to execute AppKt in command line with the java tool successfully.**  
Note you should include both in the classpath, the current folder and the kotlin-stdlib.jar, such as -cp .:kotlin-stdlib.jar or -cp ".;kotlin-stdlib.jar", separating with : or ; for windows based OS (replace kotlin-stdlib.jar with the full path).
    * Explain what the kotlin tool does automatically when running the program, and why this step is necessary when using java directly.  
R -> This Kotlin tool automatically sets the classpath to include the ``Kotlin Standard Library``, otherwise it wouldn't work, like the last question. It is necessary for the `java` command to work with the Kotlin Standard Library: `C:\kotlinc\libkotlin-stdlib.jar` - and it is required for functions like: `readLine() and print()` - included in the code.
        ```
        fun main() {
            println("Press ENTER to proceed.")
            readLine()
            X().print()    // Using Java class
        }
        ```

# Part 2: Gradle Build Tool
## Step 1: Multi-module Project
### Run 1:
5. **How many JAR files are in the decompressed lib folder?**  
R -> 9 ``.jar`` files are inside the ``lib`` folder.
6. **Which of those JAR files contains the application entry point? What is its complete content?**  
R -> It's the ``app.jar`` file. Inside it theres 2 folders: 
    - `META-INF` wich contains 2 files: `app.kotlin_module` and `MANIFEST.MF`;
    - `org` wich contains a folder called `example` that contains `App.class` and `AppKt.class`.

    
### Run 2 (Repeat instructions 3 to 6):
5. **How many JAR files are in the decompressed lib folder?**  
R -> Only 3 `.jar` files are inside the ``lib`` folder this time.
6. **Which of those JAR files contains the application entry point? What is its complete content?**  
R -> The application entry point is still the ``app.jar`` file. And it's content is still the same:
    - `META-INF` wich contains 2 files: `app.kotlin_module` and `MANIFEST.MF`;
    - `org` wich contains a folder called `example` that contains `App.class` and `AppKt.class`.  

9\. **Repeat instructions 3 to 6 and record your observations. Justify the differences.**  
R -> The difference in the number of the ``.jar`` files happens due to the removal of the ``guava dependency``.
In the first run, Gradle includes all the ``guava`` dependencies, wich result in more ``.jar`` files. In the second run, with the removal of the dependency, fewer ``.jar`` files are created.
## Step 2: Add a Java module
10. **Navigate to the lib directory inside the decompressed archive and list all JAR files.**
    - How many JAR files are now in the lib folder?
    - Identify which JAR file belongs to the app module and which belongs to the demo module.  
    - What are the names of these JAR files?  
R -> There are 4 `.jar` files inside the `lib folder`. The `app.jar` belongs to the `app module` and the `demo.jar` belongs to the `demo module`.
11. **Execute the application from the bin directory by running ./app (Linux/macOS) or app (Windows).**
    - What output do you observe?
    - Explain how Gradle packaged both modules into the distribution.  
R -> The output is: `Hello from demo module, World!`. Gradle compiled both modules into their own JAR file: `app.jar` and `demo.jar` - placing them both inside the `lib` folder of the distribution.
11. **Compare the contents of app.jar and demo.jar:**
    - Use jar tf app.jar and jar tf demo.jar to list their contents.
    - Which .class files are in each JAR?
    - Explain the relationship between the two modules based on their contents.  
R -> Contents:  
        - **app.jar:**
            ```
            META-INF/
            META-INF/MANIFEST.MF
            META-INF/app.kotlin_module
            org/
            org/example/
            org/example/AppKt.class
            ```  
        - **demo.jar:**
            ```
            META-INF/
            META-INF/MANIFEST.MF
            org/
            org/example/
            org/example/Greeter.class
            ```
    In the `app.jar` there's the `AppKt.class` and in the `demo.jar` there is the `Greeter.class`.  
    The relationship between the two modules is that the `app` module depends on the `demo` module. This means that the `AppKt.class` uses the `Greeter` class to produce an output. Even tho they are compiled separately, during runtime they are linked through the classpath.