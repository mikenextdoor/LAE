2 - São gerados 3 ficheiros do tipo class, para a open class (Problem.class) e para os 2 objetos declarados dentro do ficheiro Problems.kt (ProblemEmailAlreadyInUse.class e ProblemUserOrPasswordAreInvalid.class).

Which members are generated in the ProblemEmailAlreadyInUse class?
Identify each kind of member and explain its purpose.

3 - 
public static final ProblemEmailAlreadyInUse INSTANCE
    Kind: static field.
    Purpose:
    Stores the single instance of the object.
    Kotlin object declarations are singletons, so the JVM representation exposes the instance through a static field named INSTANCE.
static { }
    Kind: static initializer block (<clinit> in JVM bytecode).
Constructor <init>() (implicit)
    Not shown by default javap output, but it exists.
    Kind: instance constructor.

5 - 
Output class kotlin:
C:\Users\migue\Documents\Pessoal\ISEL\4º Semestre\LAE\lesson08-lab>javap -p -c ProblemEmailAlreadyInUse.class
Compiled from "Problems.kt"
public final class ProblemEmailAlreadyInUse extends Problem {
  public static final ProblemEmailAlreadyInUse INSTANCE;

  private ProblemEmailAlreadyInUse();
    Code:
       0: aload_0
       1: ldc           #8                  // String Email Already In Use
       3: ldc           #10                 // String There is already a user with given email
       5: invokespecial #13                 // Method Problem."<init>":(Ljava/lang/String;Ljava/lang/String;)V
       8: return

  static {};
    Code:
       0: new           #2                  // class ProblemEmailAlreadyInUse
       3: dup
       4: invokespecial #18                 // Method "<init>":()V
       7: putstatic     #21                 // Field INSTANCE:LProblemEmailAlreadyInUse;
      10: return
}

Output class java:
C:\Users\migue\Documents\Pessoal\ISEL\4º Semestre\LAE\lesson08-lab>javap -p -c ProblemEmailAlreadyInUse.class
Compiled from "ProblemEmailAlreadyInUse.java"
public final class ProblemEmailAlreadyInUse extends Problem {
  public static final ProblemEmailAlreadyInUse INSTANCE;

  private ProblemEmailAlreadyInUse();
    Code:
       0: aload_0
       1: ldc           #1                  // String Email Already In Use
       3: ldc           #3                  // String There is already a user with given email
       5: invokespecial #5                  // Method Problem."<init>":(Ljava/lang/String;Ljava/lang/String;)V
       8: return

  static {};
    Code:
       0: new           #11                 // class ProblemEmailAlreadyInUse
       3: dup
       4: invokespecial #13                 // Method "<init>":()V
       7: putstatic     #16                 // Field INSTANCE:LProblemEmailAlreadyInUse;
      10: return
}
Semelhantes.

6 -
    kotlinc Problems.kt
    
    javac TestMain.java
    
    kotlin TestMain

    o output foi o esperado: Problem(title='Email Already In Use', desc='There is already a user with given email')


Part 2:
2 - São criados 2 ficheiros .class: Dice.class e Dice$Companion.class.

3 - 
private final int lastRoll
Kind: instance field

public final int getLastRoll()
Kind: instance method (getter)

public static final Dice$Companion Companion
Kind: static field

public Dice()
Kind: constructor (<init>)

static { }
Kind: static initializer (<clinit>)

4 - Seria: Dice.Companion.sharedRandom

Part 3:
1 - kotlin UtilsKt:
    Crossing ola
    Crossing isel
    Crossing super
    3
    4
    5

2 - O código tem 2 tipos lambda em uso:
source.map {
    println("Crossing $it")
    transform(it)
}
{
  println("Crossing $it")
  transform(it)
}

e 

cross(listOf("ola", "isel", "super")) { it.toString().length }
{ it.toString().length }

3 - 

public final class UtilsKt {
  public static final java.lang.Iterable<java.lang.Integer> cross(java.lang.Iterable<? extends java.lang.Object>, kotlin.jvm.functions.Function1<java.lang.Object, java.lang.Integer>);
  public static final void main();
  public static void main(java.lang.String[]);
  private static final int main$lambda$1(java.lang.Object);
}

transform: (Any) -> Int , recebe um object e returna um Int

4 - 