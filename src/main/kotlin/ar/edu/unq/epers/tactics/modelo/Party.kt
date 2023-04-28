package ar.edu.unq.epers.tactics.modelo

import javax.persistence.*

@Entity
class Party(var nombre: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var imagenUrl = ""

    @OneToOne
    var pelea:Pelea? = null

    @OneToMany(mappedBy = "party", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var aventureros:MutableSet<Aventurero> = mutableSetOf()

    val numeroDeAventureros
         get() = aventureros.size

    var poder:Long = 0
    var victorias:Int = 0
    var derrotas:Int = 0

    var fuerzaFormacion:Int = 0
    var destrezaFormacion:Int = 0
    var constitucionFormacion:Int = 0
    var inteligenciaFormacion:Int = 0

    fun agregarAventurero(nuevoAventurero: Aventurero){
        if (aventureros.size >= 5) { throw Exception("La party cuenta con el numero maximo de aventureros") }
        if (aventureros.map{it.id}.contains(nuevoAventurero.id)) { throw Exception("El aventurero ya se encuentra en la party") }

        nuevoAventurero.party = this
        aventureros.add(nuevoAventurero)
    }

    fun quitarAventurero(aventurero: Aventurero){
        for (av in aventureros) {
            if(av.id == aventurero.id) {
                aventureros.remove(av)
            }
        }
    }

    fun incluye(aventurero: Aventurero): Boolean {
        return aventureros.map{it.id}.contains(aventurero.id)
    }

    fun incluirAPelea(pelea: Pelea){
        this.pelea = pelea
    }

    fun getPartyEnemiga() : Party?{
        val partyA = this.pelea?.partyA
        val partyB = this.pelea?.partyB
        return if (partyA?.id == this.id){
            partyB
        }else{
            partyA
        }
    }

    fun ganarPelea(){
        this.victorias = this.victorias+1
        for (av in aventureros){ av.ganarPelea()  }
    }

    fun perderPelea(){
        this.derrotas = this.derrotas+1
    }

    fun terminarPelea(){
        this.pelea = null
    }

    fun actualizarAtributosDeFormacion(atributosDeFormacion: List<AtributoDeFormacion>?) {

        if (atributosDeFormacion != null) {
            for (i in atributosDeFormacion) {
                when (i.atributo.toString()) {
                    "FUERZA" -> this.fuerzaFormacion = i.cantidad!!
                    "DESTREZA" -> this.destrezaFormacion = i.cantidad!!
                    "CONSTITUCION" -> this.constitucionFormacion = i.cantidad!!
                    "INTELIGENCIA" -> this.inteligenciaFormacion = i.cantidad!!
                    else -> {
                        throw Exception("No se pudo actualizar el atributo de formación.")
                    }
                }
            }
        }
    }

}