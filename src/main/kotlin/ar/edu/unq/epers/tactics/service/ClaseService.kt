package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Atributo
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Mejora

interface ClaseService {
    fun crearClase(nombreClase: String)
    fun crearMejora(nombreDeClaseA:String, nombreDeClaseB:String, atributos:List<Atributo>, cantidadDeAtributos:Int)
    fun requerir(nombreDeClase :String, nombreDeClaseRequerida :String)
    fun puedeMejorar(aventureroId:Long, mejora:Mejora): Boolean
    fun ganarProficiencia(aventureroId:Long, nombreDeClase: String, nombreDeClaseRequerida: String): Aventurero
    fun posiblesMejoras(aventureroId:Long): Set<Mejora>
    fun existeClaseConNombre(clase: String): Boolean
}