package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Atributo
import ar.edu.unq.epers.tactics.modelo.Mejora

interface ClaseDAO {
    fun crear(nombre: String)
    fun crearMejora(nombreDeClaseA: String, nombreDeClaseB: String, atributos: List<Atributo>, cantidadDeAtributos: Int)
    fun recuperarMejora(claseInicio: String, claseDestino: String): Mejora
    fun requerir(nombreDeClase: String, nombreDeClaseRequerida: String)
    fun puedeMejorar(aventureroId: Long, mejora: Mejora): Boolean
    fun existeMejoraConNombres(nombreDeClaseOrigen: String, nombreDeClaseDestino: String): Boolean
    fun existeClaseConNombre(nombreClase: String): Boolean
    fun traerPotencialesMejorasDeAventurero(aventureroId: Long):Set<Mejora>
    fun posiblesMejoras(aventureroId: Long):Set<Mejora>
    fun traerClasesDeAventurero(aventureroId:Long): MutableList<String>

}