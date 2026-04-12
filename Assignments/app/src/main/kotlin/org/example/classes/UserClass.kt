package org.example.classes

sealed class User(
    open val id: Int,
    open val email: String,
    open val nickName: String
)