package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.modelo.habilidades.*
import javax.persistence.*

@Entity
class Tactica(){

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    //@Column(unique = true, nullable = false)
    var prioridad: Int? = null

    @Column (nullable = false)
    lateinit var receptor: TipoDeReceptor
    lateinit var tipoDeEstadistica: TipoDeEstadistica
    lateinit var criterio: Criterio
    var valor: Int = 0
    lateinit var accion: Accion

    @ManyToOne
    lateinit var aventurero: Aventurero

    constructor(prioridad: Int, receptor: TipoDeReceptor, tipoDeEstadistica: TipoDeEstadistica, criterio: Criterio, valor: Int, accion: Accion, av:Aventurero):this(){
        this.prioridad = prioridad
        this.receptor = receptor
        this.tipoDeEstadistica = tipoDeEstadistica
        this.criterio = criterio
        this.valor = valor
        this.accion = accion
        this.aventurero=av

    }

    fun habilidadContra(receptor: Aventurero) : Habilidad{
        when (accion) {
            Accion.ATAQUE_FISICO -> {return Ataque(aventurero, receptor)}
            Accion.DEFENDER -> {return Defensa(aventurero, receptor)}
            Accion.ATAQUE_MAGICO -> {return AtaqueConMagia(aventurero, receptor)}
            Accion.MEDITAR -> {return Meditacion(aventurero, aventurero)}
            Accion.CURAR -> {return Curar(aventurero, receptor)}
        }
    }

    fun esTacticaSegunTipoDeReceptor(posibleReceptor: Aventurero): Boolean{
        when (receptor) {
            TipoDeReceptor.UNO_MISMO -> {return posibleReceptor.esElMismo(aventurero)}
            TipoDeReceptor.ALIADO -> {return posibleReceptor.esAliado(aventurero)}
            TipoDeReceptor.ENEMIGO -> {return posibleReceptor.esEnemigo(aventurero)}
        }
    }

    fun valorDeEstadistica(posibleReceptor: Aventurero): Int {
        when (tipoDeEstadistica) {
            TipoDeEstadistica.VIDA -> {return posibleReceptor.vida}
            TipoDeEstadistica.ARMADURA -> {return posibleReceptor.armadura}
            TipoDeEstadistica.MANA -> {return posibleReceptor.mana}
            TipoDeEstadistica.VELOCIDAD -> {return posibleReceptor.velocidad}
            TipoDeEstadistica.DAÑO_FISICO -> {return posibleReceptor.danioFisico}
            TipoDeEstadistica.DAÑO_MAGICO -> {return posibleReceptor.danioMagico}
            TipoDeEstadistica.PRECISION_FISICA -> {return posibleReceptor.presicionFisica}
        }
    }

    fun esTacticaSegunCriterio(posibleReceptor: Aventurero): Boolean {
        when (criterio) {
            Criterio.IGUAL -> {return valorDeEstadistica(posibleReceptor) == valor}
            Criterio.MAYOR_QUE -> {return valorDeEstadistica(posibleReceptor) > valor}
            Criterio.MENOR_QUE -> {return valorDeEstadistica(posibleReceptor) < valor}
        }
    }

    fun esReceptor(posibleReceptor: Aventurero): Boolean{
        return (esTacticaSegunTipoDeReceptor(posibleReceptor) && esTacticaSegunCriterio(posibleReceptor))
    }
}