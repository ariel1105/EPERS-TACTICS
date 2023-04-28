package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import javax.persistence.Entity

@Entity
class AtaqueConMagia(emisor: Aventurero, receptor: Aventurero) : Habilidad(emisor, receptor) {

    override fun ejecutar() {
        verificarMana(emisor, 5)

        emisor.mana -= 5
        if ((1..20).random() + emisor.nivel  >= receptor.velocidad / 2) {
            aplicarDanio(receptor, emisor.danioMagico)
            this.puntosAplicadosPorHabilidad = emisor.danioMagico
        }
    }
}