package ar.edu.unq.epers.tactics.persistencia.dao.neo4J

import ar.edu.unq.epers.tactics.service.runner.Neo4JTransaction

class Neo4JAventureroDAO {

    fun crear(idHibernate: Long, name: String) {
        val query = """
                    MERGE (av: Aventurero {idHibernate: ${'$'}id, name: ${'$'}nombre})
                    MERGE (cl: Clase{name: "Aventurero"})
                    MERGE (av)-[:es]->(cl)
                    """
        Neo4JTransaction.currentTransaction.run(query, mapOf(
            "id" to idHibernate,
            "nombre" to name
        ))
    }

    fun ganarClase(idHibernate: Long, clase: String) {
        val query = """
                    MATCH (av:Aventurero{idHibernate: ${'$'}id})
                    MATCH (cl:Clase{name: ${'$'}nombreClase})
                    MERGE (av)-[:es]->(cl) 
                    """
        Neo4JTransaction.currentTransaction.run(query, mapOf(
            "id" to idHibernate,
            "nombreClase" to clase
        ))
    }

}