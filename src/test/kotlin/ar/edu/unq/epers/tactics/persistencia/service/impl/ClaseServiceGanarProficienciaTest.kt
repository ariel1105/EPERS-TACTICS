package ar.edu.unq.epers.tactics.persistencia.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import helpers.Neo4JClaseDAOTEST
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.AventureroService
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.impl.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.ClaseServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import helpers.DataServiceImpl
import helpers.Neo4JTestService
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ClaseServiceGanarProficienciaTest {
    //DAOs
    private val claseDao: Neo4JClaseDAO = Neo4JClaseDAO()
    private val aventureroDAO: AventureroDAO = HibernateAventureroDAO()
    private val partyDAO: PartyDAO = HibernatePartyDAO()
    private val dataDAO: HibernateDataDAO = HibernateDataDAO()
    private val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    private val formacionDAO: FormacionDAO = FormacionDAO()
    private val claseDAO = Neo4JClaseDAO()

    //SERVICES
    private val claseService: ClaseServiceImpl = ClaseServiceImpl(claseDao, aventureroDAO, neo4JAventureroDAO, formacionDAO, partyDAO)
    private val dataService: DataServiceImpl = DataServiceImpl()
    private val partyService: PartyService = PartyServiceImpl(partyDAO,dataDAO,formacionDAO, neo4JAventureroDAO, claseDAO)
    private val aventureroService: AventureroService = AventureroServiceImpl(aventureroDAO, partyDAO)

    //DAO TEST
    private val neo4JClaseDAOTEST: Neo4JClaseDAOTEST = Neo4JClaseDAOTEST()

    //SERVICES TEST
    private val neo4JTestService: Neo4JTestService = Neo4JTestService(claseDao,neo4JClaseDAOTEST)

    //Aventureros
    private lateinit var Aventurero1: Aventurero
    private lateinit var Aventurero2: Aventurero
    private lateinit var Aventurero3: Aventurero
    private lateinit var Aventurero4: Aventurero

    //Party
    private lateinit var party: Party

    @BeforeEach
    fun crearDatosDePrueba(){
        party = Party("party")

        Aventurero1 = Aventurero("Aventurero1",1,1,1,1,"")
        Aventurero2 = Aventurero("Aventurero2",1,1,1,1,"")
        Aventurero3 = Aventurero("Aventurero3",1,1,1,1,"")
        Aventurero4 = Aventurero("Aventurero4",1,1,1,1,"")

        Aventurero1.experiencia = 1
        Aventurero2.experiencia = 5
        Aventurero3.experiencia = 2
        Aventurero4.experiencia = 3

        partyService.crear(party)
        partyService.agregarAventureroAParty(party.id!!, Aventurero1)
        partyService.agregarAventureroAParty(party.id!!, Aventurero2)
        partyService.agregarAventureroAParty(party.id!!, Aventurero3)
        partyService.agregarAventureroAParty(party.id!!, Aventurero4)

        neo4JTestService.crearDatosDePrueba()
    }

    @Test
    fun existeMejoraConNombres(){
        val existe = neo4JTestService.existeMejoraConNombres("Aventurero", "Mago")
        Assert.assertTrue(existe)
    }

    @Test
    fun noExisteMejoraConNombres(){
        val existe = neo4JTestService.existeMejoraConNombres("Aventurero", "Clerigo")
        Assert.assertFalse(existe)
    }

    @Test
    fun ganarProficiencia(){
        claseService.ganarProficiencia(Aventurero2.id!!, "Aventurero", "Pokemon")
        val Aventurero2Recuperado = aventureroService.recuperar(Aventurero2.id!!)
        Assert.assertEquals(Aventurero2Recuperado.destreza, 6)
    }

    @Test
    fun noSeGanaProficienciaPorMejoraInexistente(){
        val e: Exception =
            org.junit.jupiter.api.assertThrows { claseService.ganarProficiencia(Aventurero2.id!!, "Aventurero", "Salamin") }
        Assert.assertEquals("No existe mejora de clase origen a clase destino", e.message)
    }

    @Test
    fun noSeGanaProficienciaPorqueElAventureroNoPuedeMejorar(){
        val e: Exception =
            org.junit.jupiter.api.assertThrows { claseService.ganarProficiencia(Aventurero2.id!!, "Naruto", "Salamin") }
        Assert.assertEquals("El aventurero no puede aplicar esta mejora", e.message)
    }

    @Test
    fun cuandoSeGanaLaProficienciaElAventureroGanaLaClase(){
        claseService.ganarProficiencia(Aventurero2.id!!, "Aventurero", "Pokemon")
        val esDeClase = neo4JTestService.aventureroTieneClase(Aventurero2.id!!, "Pokemon")
        Assert.assertTrue(esDeClase)
    }

    @AfterEach
    fun cleanup(){
        dataService.deleteAll()
    }

}