package ar.edu.unq.epers.tactics.persistencia.dao.mongodb

import ar.edu.unq.epers.tactics.helpers.convertidor
import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacionDTO
import ar.edu.unq.epers.tactics.modelo.Formacion
import com.mongodb.client.model.Aggregates
import org.bson.BsonDocument
import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.*
import com.mongodb.client.model.Projections.fields
import com.mongodb.client.model.Projections.include

class FormacionDAO: GenericMongoDAO<Formacion>(Formacion::class.java) {

    fun recuperarPorNombre(nombre: String): Formacion? {
        return getBy("nombre", nombre)
    }

    fun recuperarTodas(): List<Formacion>{
        val criteria = BsonDocument()
        return find( criteria )
    }

    fun formacionesQuePosee(clasesJson: String): MutableList<Formacion> {
        val cumpleRequisitos = """
                function(){
                    const clases = $clasesJson;
                    return this.requisitos.every(({cantidadDeAventurero, nombreClase}) => clases[nombreClase] >= cantidadDeAventurero)
                }
            """

        return collection.find(where(cumpleRequisitos)).into(mutableListOf())
    }

    fun atributosQueCorresponden(formaciones: List<Formacion>):List<AtributoDeFormacion>{

        val match = Aggregates.match(`in`("nombre", formaciones.map{ it.nombre }))
        val project = Aggregates.project(fields(include("atributos")))
        val unwind = Aggregates.unwind("\$atributos")
        val group = Aggregates.group("\$atributos.atributo", Accumulators.sum("cantidad","\$atributos.cantidad"))

        val queryRes = aggregate(listOf(match, project, unwind, group), AtributoDeFormacionDTO::class.java)
        return queryRes.map{ AtributoDeFormacion(convertidor.convertirEnAtributo(it._id)!!,it.cantidad!!) }
    }

}