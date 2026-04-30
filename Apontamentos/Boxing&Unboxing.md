### Primitivos e Objetos
> **Primitivo**:
> É um valor simples e direto. A JVM conhece-o nativamente e guarda-o diretamente na stack.
> **``int, double, boolean, char...``**
>
> **Objeto**:
> É uma estrutura que vive na heap, tem um cabeçalho com metadados, e é sempre acedida por referência. Tudo o que herda de Object em Java é um objeto.
> **``Integer, String, List, qualquer classe...``**

> #### **Exemplo Concreto**
> **Um int é só um número**. A JVM sabe exactamente o que é e quanto espaço ocupa.
> **Um Integer é uma caixa que contém esse número**, mas também tem:
> - informação sobre o seu tipo
> - métodos (.toString(), .compareTo(), etc.)
> - possibilidade de ser null
---
### Boxing
> Boxing é a conversão de um tipo **`primitivo JVM`** — cujo valor é guardado diretamente no *stack* — para o tipo **`Wrapper (Boxed)`**, onde a *stack* apenas guarda uma referência que aponta para a **`heap`**, onde o objeto com o valor está guardado.
>
``` Kotlin
fun foo(o: Any) {}

fun bar(i: Int) {}

fun inc(nr: Int?) : Int {
    requireNotNull(nr)
    return nr + 1
}

fun main() {
    val n = 7
    val o: Any = n // Boxing
    foo(7) // Boxing
    inc(11)
}
```
> **val o: Any = n** O tipo Any em Kotlin equivale a Object na JVM.
A JVM não pode guardar um int primitivo numa referência Object, então o compilador converte automaticamente:
``int 7  →  Integer(7)``
> **foo(7)** O parâmetro de foo é Any, que é Object na JVM.
Mesmo motivo acima — passar 7 para Any força boxing:
``int 7  →  Integer(7)``

## Tipos que sofrem boxing

| Kotlin    | Primitivo JVM | Wrapper (boxed) |
|-----------|---------------|-----------------|
| `Int`     | `int`         | `Integer`       |
| `Long`    | `long`        | `Long`          |
| `Double`  | `double`      | `Double`        |
| `Float`   | `float`       | `Float`         |
| `Boolean` | `boolean`     | `Boolean`       |
| `Char`    | `char`        | `Character`     |
| `Byte`    | `byte`        | `Byte`          |
| `Short`   | `short`       | `Short`         |

Sempre que um tipo tem `?`, deixa de ser primitivo na JVM e passa a ser um objeto (Wrapper) — logo, sofre boxing.

```kotlin
val a: Int = 42    // primitivo → int na JVM
val b: Int? = 42   // objeto    → Integer na JVM (boxing!)
```
---
### Checkcast
> Checkcast é uma **``instrução da JVM``** que verifica em runtime se um objeto é de um determinado tipo, lançando **``ClassCastException``** se não for. Ocorre em casts explícitos (as) e ao aceder a elementos de coleções genéricas, devido ao type erasure apagar os tipos genéricos em runtime.