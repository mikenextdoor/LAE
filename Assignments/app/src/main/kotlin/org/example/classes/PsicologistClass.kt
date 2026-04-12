package org.example.classes

data class Psicologist(
    override val id: Int,
    override val email: String,
    override val nickName: String,
    val professionalCard: String,
    val specializationArea: String
) : User(id, email, nickName)