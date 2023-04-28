package ar.edu.unq.epers.tactics.persistencia.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.impl.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PeleaServiceImpl
import helpers.DataServiceImpl
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PeleaServiceRecuperarOrdenadasTest {

    //DAOS
    private val partyDao: HibernatePartyDAO = HibernatePartyDAO()
    private val dataDAO: HibernateDataDAO = HibernateDataDAO()
    private val peleaDao: HibernatePeleaDAO = HibernatePeleaDAO()
    private val aventureroDao: HibernateAventureroDAO = HibernateAventureroDAO()
    private val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    private val formacionDAO: FormacionDAO = FormacionDAO()
    private val claseDAO = Neo4JClaseDAO()


    //services
    private val dataService: DataServiceImpl = DataServiceImpl()
    private val partyService : PartyServiceImpl = PartyServiceImpl(partyDao, dataDAO, formacionDAO, neo4JAventureroDAO, claseDAO)
    private val peleaService : PeleaServiceImpl = PeleaServiceImpl(peleaDao, partyDao, aventureroDao)
    private val aventureroService : AventureroServiceImpl = AventureroServiceImpl(aventureroDao,partyDao)

    //Parties
    var party1 = Party("1")
    var party2 = Party("2")
    var party3 = Party("3")
    var party4 = Party("4")
    var party5 = Party("5")
    var party6 = Party("6")
    var party7 = Party("7")
    var party8 = Party("8")
    var party9 = Party("9")
    var party10 = Party("10")
    var party11 = Party("11")
    var party12 = Party("12")
    var party13 = Party("13")

    //Aventureros
    var av1: Aventurero = Aventurero("1",10,10,10,10,"")
    var av2: Aventurero = Aventurero("2",10,10,10,10,"")
    var av3: Aventurero = Aventurero("3",10,10,10,10,"")
    var av4: Aventurero = Aventurero("4",10,10,10,10,"")
    var av5: Aventurero = Aventurero("5",10,10,10,10,"")
    var av6: Aventurero = Aventurero("6",10,10,10,10,"")
    var av7: Aventurero = Aventurero("7",10,10,10,10,"")
    var av8: Aventurero = Aventurero("8",10,10,10,10,"")
    var av9: Aventurero = Aventurero("9",10,10,10,10,"")
    var av10: Aventurero = Aventurero("10",10,10,10,10,"")
    var av11: Aventurero = Aventurero("11",10,10,10,10,"")
    var av12: Aventurero = Aventurero("12",10,10,10,10,"")
    var av13: Aventurero = Aventurero("13",10,10,10,10,"")

    @BeforeEach
    fun prepararDatosDePrueba() {
        //persisto parties
        partyService.crear(party1)
        partyService.crear(party2)
        partyService.crear(party3)
        partyService.crear(party4)
        partyService.crear(party5)
        partyService.crear(party6)
        partyService.crear(party7)
        partyService.crear(party8)
        partyService.crear(party9)
        partyService.crear(party10)
        partyService.crear(party11)
        partyService.crear(party12)
        partyService.crear(party13)

        //persisto aventureros
        partyService.agregarAventureroAParty(party1.id!!, av1)
        partyService.agregarAventureroAParty(party2.id!!, av2)
        partyService.agregarAventureroAParty(party3.id!!, av3)
        partyService.agregarAventureroAParty(party4.id!!, av4)
        partyService.agregarAventureroAParty(party5.id!!, av5)
        partyService.agregarAventureroAParty(party6.id!!, av6)
        partyService.agregarAventureroAParty(party7.id!!, av7)
        partyService.agregarAventureroAParty(party8.id!!, av8)
        partyService.agregarAventureroAParty(party9.id!!, av9)
        partyService.agregarAventureroAParty(party10.id!!, av10)
        partyService.agregarAventureroAParty(party11.id!!, av11)
        partyService.agregarAventureroAParty(party12.id!!, av12)
        partyService.agregarAventureroAParty(party13.id!!, av13)

        //creo las peleas
        var pelea1 = peleaService.iniciarPelea(party1.id!!, party2.id!!)
        peleaService.terminarPelea(pelea1.id!!)
        var pelea2 = peleaService.iniciarPelea(party1.id!!, party3.id!!)
        peleaService.terminarPelea(pelea2.id!!)
        var pelea3 = peleaService.iniciarPelea(party1.id!!, party4.id!!)
        peleaService.terminarPelea(pelea3.id!!)
        var pelea4 = peleaService.iniciarPelea(party1.id!!, party5.id!!)
        peleaService.terminarPelea(pelea4.id!!)
        var pelea5 = peleaService.iniciarPelea(party1.id!!, party6.id!!)
        peleaService.terminarPelea(pelea5.id!!)
        var pelea6 = peleaService.iniciarPelea(party1.id!!, party7.id!!)
        peleaService.terminarPelea(pelea6.id!!)
        var pelea7 = peleaService.iniciarPelea(party1.id!!, party8.id!!)
        peleaService.terminarPelea(pelea7.id!!)
        var pelea8 = peleaService.iniciarPelea(party1.id!!, party9.id!!)
        peleaService.terminarPelea(pelea8.id!!)
        var pelea9 = peleaService.iniciarPelea(party1.id!!, party10.id!!)
        peleaService.terminarPelea(pelea9.id!!)
        var pelea10 = peleaService.iniciarPelea(party1.id!!, party11.id!!)
        peleaService.terminarPelea(pelea10.id!!)
        var pelea11 = peleaService.iniciarPelea(party1.id!!, party12.id!!)
        peleaService.terminarPelea(pelea11.id!!)
        var pelea12 = peleaService.iniciarPelea(party1.id!!, party13.id!!)
        peleaService.terminarPelea(pelea12.id!!)
    }

    @Test
    fun cantidadDePeleasEs10EnPagina0(){
        var cantidadDePeleas = peleaService.recuperarOrdenadas(party1.id!!, 0).peleas.size
        Assert.assertEquals(cantidadDePeleas ,10)
    }

    @Test
    fun cantidadDePeleasEs2EnPagina1(){
        var cantidadDePeleas = peleaService.recuperarOrdenadas(party1.id!!, 1).peleas.size
        Assert.assertEquals(cantidadDePeleas ,2)
    }

    @Test
    fun cantidadDePeleasEs1ComoPartyB(){
        var cantidadDePeleas = peleaService.recuperarOrdenadas(party3.id!!,0).peleas.size
        Assert.assertEquals(cantidadDePeleas ,1)
    }

    @Test
    fun elOrdenEstaRegidoPorElIDDeLaPelea(){
        var primeroDePagina = peleaService.recuperarOrdenadas(party1.id!!,0).peleas.get(0)
        var ultimoDePagina = peleaService.recuperarOrdenadas(party1.id!!,0).peleas.get(9)
        Assert.assertEquals(primeroDePagina.id, 12.toLong())
        Assert.assertEquals(ultimoDePagina.id, 3.toLong())
    }

    @AfterEach
    fun cleanup() {
        dataService.deleteAll()
    }
}