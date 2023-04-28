package ar.edu.unq.epers.tactics.helpers

import ar.edu.unq.epers.tactics.modelo.Atributo
import com.fasterxml.jackson.databind.ObjectMapper

object convertidor {

    fun convertirEnAtributo(nombre: String): Atributo {
        var resultado: Atributo =

            when (nombre) {
                "FUERZA" -> Atributo.FUERZA
                "DESTREZA" -> Atributo.DESTREZA
                "CONSTITUCION" -> Atributo.CONSTITUCION
                "INTELIGENCIA"-> Atributo.INTELIGENCIA
                else -> {
                    throw Exception("El nombre no corresponde a un atributo")
                }
            }

        return resultado
    }

    fun clasesToJson(clases: List<String> ): String {
        val clasesJson: String = ObjectMapper().writer().writeValueAsString(clases.groupBy { it }.mapValues { it.value.size })
        return clasesJson
    }


}