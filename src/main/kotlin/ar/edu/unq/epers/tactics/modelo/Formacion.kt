package ar.edu.unq.epers.tactics.modelo

class Formacion {

    lateinit var nombre: String
    lateinit var requisitos: List<Requerimiento>
    lateinit var atributos: List<AtributoDeFormacion>

    protected constructor() {}

    constructor(nombre: String, requisitos: List<Requerimiento>, atributos: List<AtributoDeFormacion>){
        this.nombre = nombre
        this.requisitos = requisitos
        this.atributos = atributos
    }

}