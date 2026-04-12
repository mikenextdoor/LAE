package org.example.ui

import org.example.classes.Interacao
import org.example.interfaces.*
import java.sql.Date

class InsertsUI(private val inserts: InsertsInterface) {
    fun insertInteracao() {
        print("ID Interação: ")
        val id = readln().toInt()

        print("Data (YEAR-MONTH-DAY): ")
        val date = Date.valueOf(readln())

        print("Texto: ")
        val text = readln().trim().ifEmpty { null }

        print("Cédula de Moderador: ")
        val cedulaModerador = readln().trim().ifEmpty { null }

        print("ID Utilizador: ")
        val idUtilizador = readln().trim().toInt()

        print("Abusiva? (true/false): ")
        val abusivaInput = readln().trim()
        val abusiva = if (abusivaInput.isEmpty()) null else abusivaInput.toBoolean()

        val interaction = Interacao(
            id = id,
            data = date,
            texto = text,
            cedulaModerador = cedulaModerador,
            idUtilizador = idUtilizador,
            abusiva = abusiva
        )

        inserts.insertInteracao(interaction)

        println("Interação registada!")
    }
}