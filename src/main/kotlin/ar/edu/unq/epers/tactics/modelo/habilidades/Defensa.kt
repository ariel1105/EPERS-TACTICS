package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import javax.persistence.Entity

@Entity
class Defensa(emisor: Aventurero, receptor: Aventurero): Habilidad(emisor, receptor)  {
    override fun ejecutar() {
        emisor.defendiendoA = receptor
        emisor.turnosDefendiendo = 3
        receptor.siendoDefendidoPor = emisor

        //TODO: agregar puntos defendidos
    }
}