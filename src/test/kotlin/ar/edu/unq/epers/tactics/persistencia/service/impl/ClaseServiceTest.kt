package ar.edu.unq.epers.tactics.persistencia.service.impl

import ar.edu.unq.epers.tactics.modelo.Atributo
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import helpers.Neo4JClaseDAOTEST
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.impl.ClaseServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import helpers.DataServiceImpl
import helpers.Neo4JTestService
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.assertThrows

class ClaseServiceTest {

    //DAOs
    private val claseDao: Neo4JClaseDAO = Neo4JClaseDAO()
    private val aventureroDAO: AventureroDAO = HibernateAventureroDAO()
    private val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    private val partyDao: HibernatePartyDAO = HibernatePartyDAO()
    private val dataDAO: HibernateDataDAO = HibernateDataDAO()
    private val formacionDAO: FormacionDAO = FormacionDAO()
    private val claseDAO = Neo4JClaseDAO()

    //SERVICES
    private val claseService: ClaseServiceImpl = ClaseServiceImpl(claseDao, aventureroDAO, neo4JAventureroDAO, formacionDAO, partyDao)
    private val dataService: DataServiceImpl = DataServiceImpl()
    private val partyService : PartyServiceImpl = PartyServiceImpl(partyDao, dataDAO, formacionDAO, neo4JAventureroDAO,claseDAO)

    //DAO TEST
    private val neo4JClaseDAOTEST: Neo4JClaseDAOTEST = Neo4JClaseDAOTEST()

    //SERVICES TEST
    private val neo4JTestService: Neo4JTestService = Neo4JTestService(claseDao,neo4JClaseDAOTEST)

    @Test
    fun crearClaseNombreMago(){
        claseService.crearClase("Mago")

        val nombreClase = neo4JTestService.recuperarNombreDeClase("Mago")

        Assert.assertTrue(claseService.existeClaseConNombre("Mago"))
        Assert.assertEquals("Mago",nombreClase)
    }

    @Test
    fun crearMejora(){
        claseService.crearClase("Aventurero")
        claseService.crearClase("Mago")
        claseService.crearMejora("Aventurero", "Mago", listOf(Atributo.FUERZA, Atributo.DESTREZA), 5)

        val mejoraRecuperada = neo4JTestService.recuperarMejora("Aventurero", "Mago")

        Assert.assertEquals(mejoraRecuperada.claseOrigen,"Aventurero")
        Assert.assertEquals(mejoraRecuperada.claseDestino,"Mago")
        Assert.assertEquals(mejoraRecuperada.atributos,listOf(Atributo.FUERZA, Atributo.DESTREZA))
        Assert.assertEquals(mejoraRecuperada.cantidad, 5)
    }

    @Test
    fun requerir(){
        claseService.crearClase("Aventurero")
        claseService.crearClase("Mago")

        claseService.crearClase("Fisico")
        claseService.requerir("Aventurero", "Mago")
        claseService.requerir("Mago", "Fisico")

        val e: Exception = assertThrows( { claseService.requerir("Mago", "Aventurero") } )
        val requiereRecuperado  = neo4JTestService.recuperarRequiere("Aventurero")

        Assert.assertTrue(requiereRecuperado.contains("Mago"))
        Assert.assertEquals(1, requiereRecuperado.size)
        Assert.assertEquals("No se puede crear relacion bidireccional", e.message)
    }

    @Test
    fun seAgregaUnAventureroAUnaPartyYSeCreaSuClaseYSuRelacionConLaClaseAventurero(){
        claseService.crearClase("Aventurero")
        val aventureroA = Aventurero("aventureroA",1,1,1,1,"")
        val party = Party("party")
        val partyCreada = partyService.crear(party)
        partyService.agregarAventureroAParty(partyCreada.id!!, aventureroA)
        val idAventureroHibernate = aventureroA.id
        val idAventureroNeo4J = neo4JTestService.recuperarAventurero(aventureroA.id!!)
        val arielTieneClaseAventurero = neo4JTestService.aventureroTieneClase(aventureroA.id!!, "Aventurero")
        Assert.assertEquals(idAventureroHibernate,idAventureroNeo4J)
        Assert.assertTrue(arielTieneClaseAventurero)
    }

    @AfterEach
    fun cleanup(){
        dataService.deleteAll()
    }

}