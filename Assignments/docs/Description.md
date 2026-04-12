# Assignment 1 - Kotlin Reflection - Description

_LEIC 41D - Group 11_

## Project overview

The main objective of this assignment is to eliminate duplicated code in the classes responsible for database access, specifically in the following files:


- `inserts.ks`
- `queries.kt`  

Each of these classes currently contains very similar methods for **inserting** and **queries** methods. The goal is to **centralize** this logic into a **generic, reflection‑based layer** that can work with any domain class.

***

## Centralized generic data‑access layer

Instead of multiple classes doing almost the same thing, you will:

- define a **single generic interface** that describes all common operations,
- provide **two implementations**:
  - a **direct JDBC implementation** (close to the original code),
  - a **reflection‑based implementation**

For each entity, there is a corresponding domain class:
- **User**,
- **Psichologist** (subtype of User),
- **Moderator** (subtype of User)

Additionally, there is a main interface called `UserData`.
***

## JDBC Implementation

The first implementation will maintain the original logic, but structured according to the interface described above.

```kotlin
class UserDataJDBC: UserData {
    //Example
    override fun getUserById(id: Int): User? {
        TODO()
    }
}
```

***

### Reflection Implementation

Otherwise, the second implementation will use the same interface but calling the Reflection functions from the Utility route.

For this to work, the following steps are required:

- **Inspect class properties** (KClass)
- **Automatically generate SQL queries** (INSERT, SELECT)

***

## Changes in the existing code

- Removal of duplicated code in inserts.kt and queries.kt,
- Replacement of those functions with interface calls,
- Centralization of data access logic into a single interface,
- Reduction of entity-specific code.

***

## Use of Reflection API

This API will be used to:

- Retrieve the names and types of class properties,
- Build SQL queries based on data types,
- Eliminate entity-specific code.
