package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import javax.persistence.*

@Entity
class Aventurero (val nombre: String,
                  var fuerzaBase: Int,
                  var destrezaBase: Int,
                  var constitucionBase: Int,
                  var inteligenciaBase: Int,
                  var imageUrl: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToMany(mappedBy = "aventurero", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var tacticas: MutableList<Tactica> = mutableListOf()

    @ManyToOne
    var party: Party? = null

    var nivel = 1
    var experiencia = 0

    var fuerza: Int
        get() = fuerzaBase + (party?.fuerzaFormacion ?: 0)
        set(value) { fuerzaBase = value }
    var destreza: Int
        get() = destrezaBase + (party?.destrezaFormacion ?: 0)
        set(value) { destrezaBase = value }
    var constitucion: Int
        get() = constitucionBase + (party?.constitucionFormacion ?: 0)
        set(value) { constitucionBase = value }
    var inteligencia: Int
        get() = inteligenciaBase + (party?.inteligenciaFormacion ?: 0)
        set(value) { inteligenciaBase = value }


    /*Estadisticas*/
    val vidaMaxima: Int
        get() = nivel * 5 + constitucion * 2 + fuerza
    val armadura: Int
        get() = nivel + constitucion
    val manaMaxima: Int
        get() = nivel + inteligencia
    val velocidad: Int
        get() = nivel + destreza
    val danioFisico: Int
        get() = nivel + fuerza + destreza / 2
    val danioMagico: Int
        get() = nivel + inteligencia
    val presicionFisica: Int
        get() = nivel + fuerza + destreza
    var vida: Int = vidaMaxima
    var mana: Int = manaMaxima

    val nockeado: Boolean
        get() = vida < 1

    @OneToOne
    var siendoDefendidoPor: Aventurero? = null
    @OneToOne
    var defendiendoA: Aventurero? = null
    var turnosDefendiendo = 0

    fun resetearValores (){
        vida = vidaMaxima
        mana = manaMaxima
        siendoDefendidoPor = null
        defendiendoA = null
        turnosDefendiendo = 0
    }

   fun agregarTactica(tactica: Tactica){
        tacticas.add(tactica)
   }

    fun esElMismo(aventurero: Aventurero): Boolean {
        return this.id == aventurero.id
    }

    fun esAliado(aventurero: Aventurero): Boolean {
        return this.party?.incluye(aventurero) ?: throw Exception("El aventurero no pertenece a una party")
    }

    fun esEnemigo(aventurero: Aventurero): Boolean {
        return !esAliado(aventurero)
    }

    fun tacticaPorPrioridad(): MutableList<Tactica>{
        return tacticas.sortedBy { it.prioridad }.toMutableList()
    }

    //resolverTurno
    fun resolverTurno(): Habilidad {
        if(this.party == null) {
            throw Exception("El aventurero no pertenece a una party")
        }
        //resolver lista de tácticas
        val tacticas:List<Tactica> = this.tacticaPorPrioridad()
        //partys
        val partyEnemiga: Party? = this.party!!.getPartyEnemiga()
        //resolver aliados
        val aliados:List<Aventurero> = this.party!!.aventureros.filter { it.id != this.id }
        //resolver enemigos
        val enemigos:Set<Aventurero> = partyEnemiga!!.aventureros

        var receptorFinal:Aventurero? = null
        var tacticaFinal:Tactica? = null

        //mientras receptor es null
        myloop@ while (receptorFinal == null) {
            for (tactica in tacticas){
                if (tactica.receptor.name.equals("UNO_MISMO")){
                    if (this.matcheaTactica(tactica)){
                        receptorFinal = this
                        tacticaFinal = tactica
                        break@myloop
                    }
                }else if (tactica.receptor.name.equals("ALIADO")){
                    for (aliado in aliados){
                        if (aliado.matcheaTactica(tactica)){
                            receptorFinal = aliado
                            tacticaFinal = tactica
                            break@myloop
                        }
                    }
                }else if (tactica.receptor.name.equals("ENEMIGO")){
                    for (enemigo in enemigos){
                        if (enemigo.matcheaTactica(tactica)){
                            receptorFinal = enemigo
                            tacticaFinal = tactica
                            break@myloop
                        }
                    }
                }
            }
        }
        val habilidadResultante: Habilidad = tacticaFinal!!.habilidadContra(receptorFinal!!)

        actualizarTurnosDefensa()

        return habilidadResultante
    }

    fun ganarPelea(){
        nivel ++
        experiencia ++
    }

    private fun matcheaTactica(tactica:Tactica) : Boolean{
        return tactica.esReceptor(this)
    }

    private fun actualizarTurnosDefensa(){
        if (defendiendoA != null) {
            turnosDefendiendo -=1
            this.dejarDeDefender()
        }
    }

    private fun dejarDeDefender(){
        if (turnosDefendiendo < 1) {
            defendiendoA!!.siendoDefendidoPor = null
            defendiendoA = null
        }
    }

    fun tienePuntosDeExperiencia(): Boolean{
        return experiencia>0
    }

    fun aplicarMejora(mejora: Mejora){
        val atributosAMejorar = mejora.atributos
        for(at in atributosAMejorar){
            subirAtributo(at, mejora.cantidad)
        }
    }

    fun subirAtributo(atributo: Atributo, cantidad:Int){
        when(atributo){
            Atributo.CONSTITUCION -> { constitucion += cantidad }
            Atributo.DESTREZA -> { destreza += cantidad }
            Atributo.FUERZA -> { fuerza += cantidad }
            Atributo.INTELIGENCIA -> { inteligencia += cantidad }
        }
    }

}