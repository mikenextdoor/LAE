package org.example.ui

import org.example.interfaces.*

class QueriesUI(private val queries: QueriesInterface) {
    fun getInteracaoByUser() {
        print("User ID: ")
        val id = readln().trim().toInt()
        val result = queries.getInteracoesByUser(id)
        result.forEach {
            println(it)
        }
    }

    fun getCasosByInteracao() {
        print("Interacao ID: ")
        val id = readln().trim().toInt()
        val result = queries.getCasosByInteracao(id)
        result.forEach {
            println("Casos: $it")
        }
    }

    fun getTodasInteracoes() {
        queries.getTodasInteracoes().forEach {
            println("$it")
        }
    }

    fun getInteracoesByCasosUser() {
        print("User ID: ")
        val id = readln().trim().toInt()
        val result = queries.getInteracoesCasosByUser(id)
        result.forEach {
            println("Interacoes: $it")
        }
    }

    fun getCasoById() {
        println("Caso ID: ")
        val id = readln().trim().toInt()
        val result = queries.getCasoById(id)
        println(result)
    }

    fun getNumeroInteracoesAbusivas() {
        val result = queries.getNumeroInteracoesAbusivas()
        result.forEach {
            println(it)
        }
    }

    fun getCasosPorInteracao() {
        val result = queries.getCasosPorInteracao()
        result.forEach {
            println(it)
        }
    }

    fun getEstatisticasPsicologos() {
        val result = queries.getEstatisticasPsicologos()
        result.forEach {
            println(it)
        }
    }

    fun getEstatisticasPorArea() {
        val result = queries.getEstatisticasPorArea()
        result.forEach {
            println(it)
        }
    }

    fun getUtilizadoresComTodosRecursos() {
        val result = queries.getUtilizadoresComTodosRecursos()
        result.forEach {
            println(it)
        }
    }

    fun getCasosGravidadeSuperiorMedia() {
        val result = queries.getCasosGravidadeSuperiorMedia()
        result.forEach {
            println(it)
        }
    }

    fun getPsiIntervencoes() {
        println("Intervenções Mínimas: ")
        val minInt = readln().trim().toInt()
        println("Gravidade Mínima: ")
        val minGrav = readln().trim().toInt()
        val result = queries.getPsiIntervencoes(minInt, minGrav)
        result.forEach {
            println(it)
        }
    }
}