package ar.edu.unq.epers.tactics.persistencia.dao.neo4J

import ar.edu.unq.epers.tactics.persistencia.dao.DataDAO
import ar.edu.unq.epers.tactics.service.runner.Neo4JTransaction

class Neo4JDataDAO: DataDAO {
    override fun clear() {
        val query = """
                    MATCH (n) DETACH DELETE n
                    """
        Neo4JTransaction.currentTransaction.run(query)
    }
}