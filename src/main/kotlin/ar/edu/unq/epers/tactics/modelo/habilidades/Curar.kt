package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import javax.persistence.Entity

@Entity
class Curar(emisor: Aventurero, receptor: Aventurero) : Habilidad(emisor, receptor) {
    override fun ejecutar() {
        verificarMana(emisor, 5)

        val vidaInicalReceptor = receptor.vida

        emisor.mana -= 5
        curar(receptor, emisor.danioMagico)

        this.puntosAplicadosPorHabilidad = receptor.vida - vidaInicalReceptor
    }
}