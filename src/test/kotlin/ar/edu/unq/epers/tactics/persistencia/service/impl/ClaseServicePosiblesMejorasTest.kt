package ar.edu.unq.epers.tactics.persistencia.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Mejora
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
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.impl.ClaseServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import helpers.DataServiceImpl
import helpers.Neo4JTestService
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ClaseServicePosiblesMejorasTest {

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

    //DAO TEST
    private val neo4JClaseDAOTEST: Neo4JClaseDAOTEST = Neo4JClaseDAOTEST()

    //SERVICES TEST
    private val neo4JTestService: Neo4JTestService = Neo4JTestService(claseDao,neo4JClaseDAOTEST)

    //Aventureros
    private lateinit var aventurero1: Aventurero
    private lateinit var aventurero2: Aventurero
    private lateinit var aventurero3: Aventurero
    private lateinit var aventurero4: Aventurero

    //Party
    private var party = Party("party")

    @BeforeEach
    fun crearDatosDePrueba(){
        aventurero1 = Aventurero("Aventurero1",1,1,1,1,"")
        aventurero2 = Aventurero("Aventurero2",1,1,1,1,"")
        aventurero3 = Aventurero("Aventurero3",1,1,1,1,"")
        aventurero4 = Aventurero("Aventurero4",1,1,1,1,"")

        aventurero1.experiencia = 2
        aventurero2.experiencia = 0
        aventurero3.experiencia = 1
        aventurero4.experiencia = 3

        partyService.crear(party)
        aventurero1 = partyService.agregarAventureroAParty(party.id!!, aventurero1)
        aventurero2 = partyService.agregarAventureroAParty(party.id!!, aventurero2)
        aventurero3 = partyService.agregarAventureroAParty(party.id!!, aventurero3)
        aventurero4 = partyService.agregarAventureroAParty(party.id!!, aventurero4)

        neo4JTestService.crearDatosDePrueba()
    }

    @Test
    fun traerPotencialesMejorasCantidadTest(){
        //Posibles mejoras devuelve la cantiad correcta
        val resultado: Set<Mejora> = claseService.posiblesMejoras(aventurero3.id!!)
        Assert.assertTrue(resultado.size == 2)
    }

    @Test
    fun posiblesMejorasRequerimientosCumplidosTest(){
        val resultado: Set<Mejora> = claseService.posiblesMejoras(aventurero3.id!!)
        Assert.assertEquals(2, resultado.size)

        val mejoraMagoAClerigo:Mejora = resultado.filter{ it.claseDestino == "Clerigo"}.first()
        val mejoraNarutoASalamin:Mejora = resultado.filter{ it.claseDestino == "Salamin"}.first()

        Assert.assertEquals("Mago", mejoraMagoAClerigo.claseOrigen)
        Assert.assertEquals("Clerigo", mejoraMagoAClerigo.claseDestino)

        Assert.assertEquals("Naruto", mejoraNarutoASalamin.claseOrigen)
        Assert.assertEquals("Salamin", mejoraNarutoASalamin.claseDestino)
    }

    @Test
    fun posiblesMejorasRequerimientosNoCumplidosTest(){
        val resultado: Set<Mejora> = claseService.posiblesMejoras(aventurero2.id!!)
        Assert.assertEquals(2, resultado.size)

        val mejoraAventureroAPokemon:Mejora = resultado.filter{ it.claseDestino == "Pokemon"}.first()
        val mejoraMagoAClerigo:Mejora = resultado.filter{ it.claseDestino == "Clerigo"}.first()

        Assert.assertEquals("Aventurero", mejoraAventureroAPokemon.claseOrigen)
        Assert.assertEquals("Pokemon", mejoraAventureroAPokemon.claseDestino)

        Assert.assertEquals("Mago", mejoraMagoAClerigo.claseOrigen)
        Assert.assertEquals("Clerigo", mejoraMagoAClerigo.claseDestino)
    }

    @Test
    fun posiblesMejorasSoloUnaDisponibleTest(){
        val resultado: Set<Mejora> = claseService.posiblesMejoras(aventurero4.id!!)
        Assert.assertEquals(1, resultado.size)

        val mejoraAventureroAMago:Mejora = resultado.first()

        Assert.assertEquals("Aventurero", mejoraAventureroAMago.claseOrigen)
        Assert.assertEquals("Mago", mejoraAventureroAMago.claseDestino)
    }

    @Test
    fun posiblesMejorasSinMejorasPosiblesTest(){
        val resultado: Set<Mejora> = claseService.posiblesMejoras(aventurero1.id!!)
        Assert.assertTrue(resultado.size == 0)
    }

    @AfterEach
    fun cleanup(){
      dataService.deleteAll()
    }

}