package org.example.classes

data class Moderator(
    override val id: Int,
    override val email: String,
    override val nickName: String,
    val professionalCard: String,
    val specializationArea: String,
    val seniorLevel: Int
) : User(id, email, nickName)