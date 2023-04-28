package ar.edu.unq.epers.tactics.persistencia.service.impl

import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import helpers.DataServiceImpl
import org.junit.Assert
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PartyServiceRecuperarOrdenadasCasosBordeTest {
    //DAOs
    private val dao: HibernatePartyDAO = HibernatePartyDAO()
    private val dataDAO: HibernateDataDAO = HibernateDataDAO()
    private val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    private val formacionDAO: FormacionDAO = FormacionDAO()
    private val claseDAO = Neo4JClaseDAO()

    //Services
    private val partyService : PartyServiceImpl = PartyServiceImpl(dao, dataDAO, formacionDAO, neo4JAventureroDAO, claseDAO)
    private val dataService: DataServiceImpl = DataServiceImpl()

    @BeforeAll
    fun prepararDatos(){
        dataService.deleteAll()
    }

    @Test
    fun siNoExistenPartiesNoDevuelveNada(){
        val partyPaginadaResult = partyService.recuperarOrdenadas(Orden.VICTORIAS, Direccion.DESCENDENTE, 0)
            Assert.assertTrue(partyPaginadaResult.parties.size == 0)
    }

    @Test
    fun siPidoUnaPaginaInexistenteRetornaException(){
        val e: Exception = assertThrows( { partyService.recuperarOrdenadas(Orden.VICTORIAS, Direccion.DESCENDENTE, 15345) } )
        Assert.assertEquals("La página solicitada no está disponible en el resultado", e.message)
    }

}