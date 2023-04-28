package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Pelea
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Habilidad(
        @OneToOne
        val emisor: Aventurero,
        @OneToOne
        val receptor: Aventurero) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToOne
    val peleaEnQueSeUtiliza: Pelea = emisor.party!!.pelea!!
    var puntosAplicadosPorHabilidad: Int = 0

    abstract fun ejecutar()

    internal fun verificarMana(emisor: Aventurero, cantidad: Int){
        if (emisor.mana < cantidad) { throw Exception("Mana insufieciente para realizar la accion solicitada.")  }
    }

    internal fun aplicarDanio(receptor: Aventurero, danio: Int) {
        var receptorFinal: Aventurero = receptor
        if( receptor.siendoDefendidoPor != null ) {
            receptorFinal = receptor.siendoDefendidoPor!!
        }
        //Si es el primer turno en que lo defiende, recibe la mitad de danio
        if (receptorFinal.turnosDefendiendo == 3) {
            receptorFinal.vida -= danio / 2
        }
        //Sino, recibe el total
        else {
            receptorFinal.vida -= danio
        }
    }

    internal fun curar(receptor: Aventurero, vidaACurar: Int) {
        if (receptor.vida + vidaACurar >= receptor.vidaMaxima){
            receptor.vida = receptor.vidaMaxima
        }
        else {
            receptor.vida += vidaACurar
        }
    }

}