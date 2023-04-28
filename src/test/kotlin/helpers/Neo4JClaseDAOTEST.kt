package helpers

import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.service.runner.Neo4JTransaction
import ar.edu.unq.epers.tactics.service.runner.TransactionRunner
import org.neo4j.driver.Record

class Neo4JClaseDAOTEST() {

    fun recuperarClase(nombre: String):String {
        val query = """
                    MATCH (b:Clase {name: ${'$'}nombreDeLaClase})
                    RETURN b.name
                    """
        val result = Neo4JTransaction.currentTransaction.run(query, mapOf(
            "nombreDeLaClase" to nombre
        ))

        return result.next().get("b.name").asString()
    }

    fun clasesRequeridas(nombreClase: String):List<String> {
        val query = """
                    MATCH (b:Clase {name: ${'$'}nombreDeLaClase})-[:REQUIERE]->(c:Clase)
                
                    RETURN COLLECT(c.name) AS cList
                    """
        val result = Neo4JTransaction.currentTransaction.run(query, mapOf(
            "nombreDeLaClase" to nombreClase
        ))

        return result.next().get("cList").asList() as MutableList<String>
    }


    fun recuperarClases(nombre: String):List<Clase> {
        val query = """
                    MATCH (b:Clase {name: ${'$'}nombreDeLaClase})
                    RETURN b
                    """
        val result = Neo4JTransaction.currentTransaction.run(query, mapOf(
            "nombreDeLaClase" to nombre
        ))

        val res = result.list { record: Record ->
            val actual = record[0]
            val nombre = actual["name"].asString()
            Clase(nombre)
        }

        return res
    }

    fun recuperarAventurero(idHibernate: Long):Long {
        val query = """
                    MATCH (b:Aventurero {idHibernate: ${'$'}id})
                    RETURN b.idHibernate
                    """
        val result = Neo4JTransaction.currentTransaction.run(query, mapOf(
            "id" to idHibernate
        ))

        return result.next().get("b.idHibernate").asLong()
    }

    fun aventureroTieneClase(idHibernate: Long, clase: String):Boolean {
        val query = """
                    MATCH (av: Aventurero{idHibernate: ${'$'}id})-[:es]->(cl: Clase{name: ${'$'}nombreClase})
                    RETURN av
                    """
        val result = Neo4JTransaction.currentTransaction.run(query, mapOf(
            "id" to idHibernate,
            "nombreClase" to clase
        ))
        val cantidadDeResultados = result.list{
                record: Record ->
            val actual = record[0]
            val nombre = actual["name"].asString()
        }.size
        return cantidadDeResultados>0
    }

    fun crearSetDatosIniciales(){
        val query = """
                    //Macheo los aventureros que deben existir ya en neo4J
                    //al igual que la clase "Aventurero"
                    MATCH (av1:Aventurero)
                    WHERE av1.name = "Aventurero1"
                    
                    MATCH (av2:Aventurero)
                    WHERE av2.name = "Aventurero2"
                    
                    MATCH (av3:Aventurero)
                    WHERE av3.name = "Aventurero3"
                    
                    MATCH (av4:Aventurero)
                    WHERE av4.name = "Aventurero4"
                    
                    //Creo las clases
                    MERGE (av:Clase {name:"Aventurero"})
                    MERGE (ma:Clase {name:"Mago"})
                    MERGE (cl:Clase {name:"Clerigo"})
                    MERGE (po:Clase {name:"Pokemon"})
                    MERGE (na:Clase {name:"Naruto"})
                    MERGE (sa:Clase {name:"Salamin"})
                    
                    //Creo los Habilita
                    MERGE (av)-[:HABILITA {listaDeHabilidades:["INTELIGENCIA","DESTREZA"],cantidad:1}]->(ma)
                    MERGE (av)-[:HABILITA {listaDeHabilidades:["DESTREZA"],cantidad:5}]->(po)
                    MERGE (ma)-[:HABILITA {listaDeHabilidades:["INTELIGENCIA","DESTREZA"],cantidad:2}]->(cl)
                    MERGE (ma)-[:HABILITA {listaDeHabilidades:["FUERZA","DESTREZA"],cantidad:3}]->(na)
                    MERGE (na)-[:HABILITA {listaDeHabilidades:["FUERZA","INTELIGENCIA","DESTREZA"],cantidad:4}]->(sa)
                    
                    //Creo los Requiere
                    MERGE (sa)-[:REQUIERE]->(na)
                    MERGE (sa)-[:REQUIERE]->(po)
                    
                    //Creo las relaciones de los aventureros
                    MERGE (av1)-[:es]->(ma)
                    MERGE (av1)-[:es]->(na)
                    MERGE (av1)-[:es]->(cl)
                    MERGE (av1)-[:es]->(po)
                    MERGE (av1)-[:es]->(sa)
                    
                    MERGE (av2)-[:es]->(na)
                    MERGE (av2)-[:es]->(ma)
                    
                    MERGE (av3)-[:es]->(ma)
                    MERGE (av3)-[:es]->(na)
                    MERGE (av3)-[:es]->(po)
                    
                    MERGE (av4)-[:es]->(po)
                    """
        Neo4JTransaction.currentTransaction.run(query)
    }

}