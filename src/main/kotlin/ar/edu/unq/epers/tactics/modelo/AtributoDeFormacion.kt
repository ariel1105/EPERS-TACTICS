package ar.edu.unq.epers.tactics.modelo

class AtributoDeFormacion {
    lateinit var atributo: Atributo
    var cantidad: Int? = null

    protected constructor()

    constructor(atributo: Atributo, cantidad: Int){
        this.atributo = atributo
        this.cantidad = cantidad
    }

}