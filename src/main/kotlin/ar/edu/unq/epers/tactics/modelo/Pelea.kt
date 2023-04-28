package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import javax.persistence.*

@Entity
class Pelea() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToOne
    var partyA: Party? = null
    @OneToOne
    var partyB: Party? = null
    @OneToOne
    var partyGanadora: Party? = null

    @OneToMany(mappedBy = "peleaEnQueSeUtiliza", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    val habilidadesUtilizadas: MutableList<Habilidad> = mutableListOf()

    constructor(pA:Party, pB:Party) : this() {
        this.partyA = pA
        this.partyB = pB
    }

    fun agregarHabilidad(habilidad: Habilidad){
        habilidadesUtilizadas.add(habilidad)
    }

}