package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import javax.persistence.Entity

@Entity
class Ataque(emisor: Aventurero, receptor: Aventurero) : Habilidad(emisor, receptor) {
    override fun ejecutar() {
        if ( (1..20).random() + emisor.presicionFisica >= receptor.armadura + receptor.velocidad / 2) {
                aplicarDanio(receptor, emisor.danioFisico)
                this.puntosAplicadosPorHabilidad = emisor.danioFisico
        }
    }
}

