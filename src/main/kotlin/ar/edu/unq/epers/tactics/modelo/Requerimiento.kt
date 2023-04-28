package ar.edu.unq.epers.tactics.modelo

class Requerimiento {
    lateinit var nombreClase: String
    var cantidadDeAventurero: Int? = null

    protected constructor()

    constructor(nombreClase: String, cantidadDeAventurero: Int){
        this.nombreClase = nombreClase
        this.cantidadDeAventurero = cantidadDeAventurero
    }

}