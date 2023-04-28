package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.modelo.Requerimiento

interface FormacionService {
    fun crearFormacion(nombreFormacion: String, requerimientos: List<Requerimiento>, stats: List<AtributoDeFormacion>): Formacion
    fun todasLasFormaciones(): List<Formacion>
    fun atributosQueCorresponden(partyId:Int): List<AtributoDeFormacion>
    fun formacionesQuePosee(partyId:Int): List<Formacion>

}