package ar.edu.unq.epers.tactics.persistencia.dao.neo4J

import ar.edu.unq.epers.tactics.helpers.convertidor
import ar.edu.unq.epers.tactics.modelo.Atributo
import ar.edu.unq.epers.tactics.modelo.Mejora
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.service.runner.Neo4JTransaction
import org.neo4j.driver.Record

class Neo4JClaseDAO: ClaseDAO {

    override fun crear(nombre: String) {
        val query = """
                    MERGE (b:Clase {name: ${'$'}nombreDeLaClase})
                    """
        Neo4JTransaction.currentTransaction.run(query, mapOf(
            "nombreDeLaClase" to nombre
        ))
    }

    override fun crearMejora(nombreDeClaseA: String, nombreDeClaseB: String, atributos: List<Atributo>, cantidadDeAtributos: Int) {
        val atributosComoString: List<String> = atributos.map{ it.toString() }
        val query = """
                    MATCH (a:Clase), (m:Clase)
                    WHERE a.name = ${'$'}nombreDeClaseA AND m.name = ${'$'}nombreDeClaseB
                    MERGE (a)-[r:HABILITA {listaDeHabilidades: ${'$'}atributos ,cantidad:${'$'}cantidadDeAtributos}]->(m)
                    """
        Neo4JTransaction.currentTransaction.run(query, mapOf(
            "nombreDeClaseA" to nombreDeClaseA
            ,"nombreDeClaseB" to nombreDeClaseB
            ,"atributos" to atributosComoString
            ,"cantidadDeAtributos" to cantidadDeAtributos
        ))
    }

    override fun recuperarMejora(claseInicio: String, claseDestino: String): Mejora {

        val query = """
                    MATCH (a:Clase {name: ${'$'}claseInicio})-[h:HABILITA]->(b:Clase {name: ${'$'}claseDestino})
                    RETURN h
                    """
        val queryResult = Neo4JTransaction.currentTransaction.run(query, mapOf(
            "claseInicio" to claseInicio
            ,"claseDestino" to claseDestino
        ))

        val rs = queryResult.next()

        val listaDeAtributosString = rs.get("h").get("listaDeHabilidades").asList() as MutableList<String>

        return Mejora(claseInicio, claseDestino,
            listaDeAtributosString.map{ convertidor.convertirEnAtributo(it) } as MutableList<Atributo>, rs.get("h").get("cantidad").asInt())

    }

    override fun requerir(nombreDeClase: String, nombreDeClaseRequerida: String)  {

        val query = """
                    MATCH (a:Clase), (b:Clase)
                    WHERE a.name = ${'$'}nombreDeClaseA AND b.name = ${'$'}nombreDeClaseB
                    AND NOT (b)-[:REQUIERE]->(a) 
                    MERGE (a)-[r:REQUIERE]->(b)
                    RETURN r
                    """
        val queryResult = Neo4JTransaction.currentTransaction.run(query, mapOf(
            "nombreDeClaseA" to nombreDeClase
            ,"nombreDeClaseB" to nombreDeClaseRequerida
        ))

        if(!queryResult.hasNext()){throw Exception("No se puede crear relacion bidireccional")}
    }

    override fun puedeMejorar(aventureroId: Long, mejora: Mejora): Boolean {
        //TODO:Intentar hacer una mejor query en neo4j para evitar las llamadas múltiples y manejos de listas en kotlin.
        //1. validar si el aventurero podría mejorar sin tener en cuenta los requerimientos.
        val aventureroPodriaMejorar = aventureroPodriaMejorar(aventureroId, mejora)

        //2. traer las clases requeridas por la claseDestino
        val listaDeClasesRequeridas:MutableList<String> = traerRequerimientosDeClase(mejora.claseDestino)

        //3. traer todas las clases del aventurero
        val clasesDelAventurero:MutableList<String> = traerClasesDeAventurero(aventureroId)

        return aventureroPodriaMejorar && clasesDelAventurero.containsAll(listaDeClasesRequeridas)
    }

    override fun existeMejoraConNombres(nombreDeClaseOrigen: String, nombreDeClaseDestino: String): Boolean {
        val query= """
                   MATCH(claseOrigen:Clase {name: ${'$'}nombreOrigen})-[:HABILITA]->(claseDestino:Clase{name: ${'$'}nombreDestino})  
                   RETURN claseOrigen
                   """
        val result = Neo4JTransaction.currentTransaction.run(query, mapOf(
            "nombreOrigen" to nombreDeClaseOrigen,
            "nombreDestino" to nombreDeClaseDestino
        ))
        val cantidad = result.list{
            record:Record ->
            val actual = record[0]
            val nombre = actual["name"].asString()
            nombre
        }.size
        return cantidad>0
    }

    override fun existeClaseConNombre(clase: String): Boolean {
        val query = """
                    MATCH (clase:Clase {name: ${'$'}nombreClase})
                    RETURN clase
                    """
        val result = Neo4JTransaction.currentTransaction.run(query, mapOf(
            "nombreClase" to clase
        ))
        return result.hasNext()
    }

    override fun traerPotencialesMejorasDeAventurero(aventureroId:Long): Set<Mejora>{
        val query = """
                    MATCH (av:Aventurero {idHibernate:${'$'}id})
                    MATCH (c:Clase)<-[:es]-(av)
                    WITH c, av
                    MATCH (destinoPosibles:Clase)<-[:HABILITA]-(c)
                    WHERE NOT (av)-[:es]->(destinoPosibles)
                    MATCH (origenPosibles)-[r:HABILITA]->(destinoPosibles)
                    RETURN origenPosibles.name as origen, destinoPosibles.name as destino, type(r) as relacion, r.listaDeHabilidades as listaDeHabilidades, r.cantidad as cantidad 
                    """
        val result = Neo4JTransaction.currentTransaction.run(query, mapOf(
            "id" to aventureroId
        ))

        val listaDePotencialesMejoras:MutableList<Mejora> = mutableListOf()
        for (i in result){
            val listaDeAtributos :MutableList<Atributo> = mutableListOf()
                val itr = i["listaDeHabilidades"].asList().listIterator()
                    while (itr.hasNext()){
                        convertidor.convertirEnAtributo(itr.next().toString())?.let { listaDeAtributos.add(it) }
            }
            val mejora = Mejora(i["origen"].asString(),i["destino"].asString(),listaDeAtributos, i["cantidad"].asInt())
            listaDePotencialesMejoras.add(mejora)
        }
        return listaDePotencialesMejoras.toSet()
    }

    override fun posiblesMejoras(aventureroId:Long): Set<Mejora>{
        val potencialesMejoras = traerPotencialesMejorasDeAventurero(aventureroId)
        val listaDePosiblesMejoras : MutableList<Mejora> = mutableListOf()

        for (i in potencialesMejoras){
            if (puedeMejorar(aventureroId, i)){
                listaDePosiblesMejoras.add(i)
            }
        }
        return listaDePosiblesMejoras.toSet()
    }

    override fun traerClasesDeAventurero(aventureroId:Long): MutableList<String>{
        val query = """
                    MATCH (av:Aventurero {idHibernate:${'$'}id})
                    MATCH (clasesAventurero:Clase)
                        WHERE (av)-[:es]->(clasesAventurero:Clase)
                    RETURN clasesAventurero.name as clase
                    """
        val result = Neo4JTransaction.currentTransaction.run(query, mapOf(
            "id" to aventureroId
        ))

        val listaDeClasesDeAventurero:MutableList<String> = mutableListOf()
        for (i in result){
            listaDeClasesDeAventurero.add(i["clase"].asString())
        }
        return listaDeClasesDeAventurero
    }

    private fun aventureroPodriaMejorar(aventureroId: Long, mejora: Mejora): Boolean {
        val query = """
                    MATCH (av:Aventurero),(claseOrigen:Clase), (claseDestino:Clase)
                    WHERE av.idHibernate = ${'$'}idAventurero 
                        AND (av)-[:es]->(claseOrigen)
                        AND claseOrigen.name = ${'$'}nombreClaseOrigen
                        AND claseDestino.name = ${'$'}nombreClaseDestino
                        AND (claseOrigen)-[:HABILITA]->(claseDestino)
                    RETURN av
                    """
        val result = Neo4JTransaction.currentTransaction.run(query, mapOf(
            "idAventurero" to aventureroId,
            "nombreClaseOrigen" to mejora.claseOrigen,
            "nombreClaseDestino" to mejora.claseDestino
        ))
        val cantidad = result.list{
                record:Record ->
            val av = record[0]
            val name = av["name"].asString()
            name
        }.size

        return cantidad>0
    }

    private fun traerRequerimientosDeClase(claseDestino:String): MutableList<String>{
        val query = """
                    MATCH (claseDestino:Clase {name:${'$'}nombreDeClaseDestino})
                    MATCH (clasesRequeridas:Clase)
                        where (claseDestino)-[:REQUIERE]->(clasesRequeridas:Clase)
                    RETURN clasesRequeridas.name as clase
                    """
        val result = Neo4JTransaction.currentTransaction.run(query, mapOf(
            "nombreDeClaseDestino" to claseDestino
        ))

        val listaDeClasesRequeridas:MutableList<String> = mutableListOf()
        for (i in result){
            listaDeClasesRequeridas.add(i["clase"].asString())
        }

        return listaDeClasesRequeridas
    }

}