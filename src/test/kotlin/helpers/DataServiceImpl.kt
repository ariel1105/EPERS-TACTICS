package helpers

import ar.edu.unq.epers.tactics.persistencia.dao.DataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.DataMongoDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JDataDAO
import ar.edu.unq.epers.tactics.service.runner.TransactionRunner.runTrx

class DataServiceImpl : DataService {

    private val dataDAOHibernate: DataDAO = HibernateDataDAO()
    private val dataDAONeo4J: DataDAO = Neo4JDataDAO()
    private val dataDAOMongo: DataDAO = DataMongoDAO(FormacionDAO())

    override fun deleteAll() {
        runTrx {
            dataDAONeo4J.clear()
            dataDAOHibernate.clear()
            dataDAOMongo.clear()
        }
    }

    override fun crearSetDatosIniciales() {
        TODO("Not yet implemented")
    }

}