package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Aventurero

interface AventureroDAO {
    fun actualizar(aventurero: Aventurero)
    fun recuperar(idDelAventurero: Long): Aventurero
    fun eliminar(aventurero: Aventurero)
    fun aventureroHabilidadConMasPuntos(tipo: String): Aventurero
}