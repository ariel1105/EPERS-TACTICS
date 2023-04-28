package ar.edu.unq.epers.tactics.modelo

class AtributoDeFormacionDTO {
    lateinit var _id: String
    var cantidad: Int? = null

    protected constructor()

    constructor(atributo: String, cantidad: Int){
        this._id = atributo
        this.cantidad = cantidad
    }

}