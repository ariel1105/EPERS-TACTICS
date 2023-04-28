package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import javax.persistence.Entity

@Entity
class Meditacion(emisor: Aventurero, receptor: Aventurero) : Habilidad(emisor, receptor){
    override fun ejecutar() {
        val manaInicialEmisor = emisor.mana
        if (emisor.mana + emisor.nivel >= emisor.manaMaxima){
            emisor.mana = emisor.manaMaxima
        }
        else {
            emisor.mana += emisor.nivel
        }
        this.puntosAplicadosPorHabilidad = emisor.mana - manaInicialEmisor
    }
}