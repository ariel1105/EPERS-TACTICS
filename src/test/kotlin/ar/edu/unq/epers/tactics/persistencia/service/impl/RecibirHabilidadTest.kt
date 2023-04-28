package ar.edu.unq.epers.tactics.persistencia.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.modelo.habilidades.*
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
import helpers.DataService
import helpers.DataServiceImpl
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RecibirHabilidadTest {

    //DAOS
    private val partyDao: HibernatePartyDAO = HibernatePartyDAO()
    private val peleaDao: HibernatePeleaDAO = HibernatePeleaDAO()
    private val aventureroDao: HibernateAventureroDAO = HibernateAventureroDAO()
    private val dataDAO: HibernateDataDAO = HibernateDataDAO()
    private val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    private val formacionDAO: FormacionDAO = FormacionDAO()
    private val claseDAO = Neo4JClaseDAO()

    //Services
    private val partyService : PartyServiceImpl = PartyServiceImpl(partyDao, dataDAO, formacionDAO, neo4JAventureroDAO, claseDAO)
    private val aventureroService : AventureroServiceImpl = AventureroServiceImpl(aventureroDao, partyDao)
    private val peleaService : PeleaServiceImpl = PeleaServiceImpl(peleaDao, partyDao, aventureroDao)
    private val dataService : DataService = DataServiceImpl()

    //Aventureros
    lateinit var emisor: Aventurero
    lateinit var receptor: Aventurero

    //Parties
    lateinit var  party1: Party
    lateinit var  party2: Party

    //Pelea
    lateinit var pelea: Pelea

    @BeforeEach
    fun setup(){
        party1 = partyService.crear(Party(""))
        party2 = partyService.crear(Party(""))

        emisor = Aventurero("", 100,100,100,100,"")
        receptor = Aventurero("",1, 1,100,1,"")

        emisor = partyService.agregarAventureroAParty(party1.id!!,emisor)
        receptor = partyService.agregarAventureroAParty(party2.id!!,receptor)

        pelea = peleaService.iniciarPelea(party1.id!!, party2.id!!)

        emisor = aventureroService.recuperar(emisor.id!!)
        receptor = aventureroService.recuperar(receptor.id!!)
    }

    @Test
    fun ataqueFisicoExitoso(){
        val ataque = Ataque(emisor, receptor)
        peleaService.recibirHabilidad(party2.id!!, receptor.id!!, ataque)
        val receptorRecuperado = aventureroService.recuperar(ataque.receptor.id!!)
        Assert.assertEquals(55, receptorRecuperado.vida)
    }

    @Test
    fun ataqueMagicoExitoso(){
        val ataqueMagico = AtaqueConMagia(emisor,receptor)
        peleaService.recibirHabilidad(party2.id!!, receptor.id!!, ataqueMagico)
        val emisorRecuperado = aventureroService.recuperar(ataqueMagico.emisor.id!!)
        val receptorRecuperado = aventureroService.recuperar(ataqueMagico.receptor.id!!)
        Assert.assertEquals(96, emisorRecuperado.mana)
        Assert.assertEquals(105, receptorRecuperado.vida)
    }

    @Test
    fun reiniciarAventurero() {

        //Altero los valores del aventurero
        emisor.defendiendoA = receptor
        emisor.mana = 50
        emisor.vida = 10
        emisor.turnosDefendiendo = 2
        emisor = aventureroService.actualizar(emisor)
        Assert.assertEquals(emisor.defendiendoA!!.id, receptor.id)
        Assert.assertEquals(50, emisor.mana)

        //reseteo aventurero y testeo
        emisor.resetearValores()
        emisor = aventureroService.actualizar(emisor)
        Assert.assertNull(emisor.defendiendoA)
        Assert.assertNull(emisor.siendoDefendidoPor)
        Assert.assertEquals(0, emisor.turnosDefendiendo)
        Assert.assertEquals(emisor.vidaMaxima, emisor.vida)
        Assert.assertEquals(emisor.manaMaxima, emisor.mana)

    }

    @Test
    fun aventureroRecibeHabiidadCurar() {
        //vida maxima receptor = 206
        //Mana emisor = 101
        //Poder magico emisor  = 101

        receptor.vida = 80
        aventureroService.actualizar(receptor)

        val habilidadCurar : Habilidad = Curar(emisor, receptor)
        peleaService.recibirHabilidad(party1.id!!,receptor.id!!, habilidadCurar)

        val emisorRecuperado = aventureroService.recuperar(emisor.id!!)
        val receptorRecuperado = aventureroService.recuperar(receptor.id!!)

        Assert.assertEquals(181, receptorRecuperado.vida)
        Assert.assertEquals(96, emisorRecuperado.mana)
    }

    @Test
    fun aventureroRecibeHabiidadMeditar() {
        //altero mana del aventurero
        emisor.mana = 90
        emisor = aventureroService.actualizar(emisor)
        Assert.assertEquals(90, emisor.mana)

        //emisor recibe habilidad meditacion
        val habilidadMeditar : Habilidad = Meditacion(emisor, emisor)
        peleaService.recibirHabilidad(party1.id!!,emisor.id!!,habilidadMeditar)
        val receptorRecuperado = aventureroService.recuperar(emisor.id!!)

        Assert.assertEquals(91, receptorRecuperado.mana)
    }

    @Test
    fun aventureroRecibeHabiidadDefender() {

        //chequeo valores iniciales
        Assert.assertNull(emisor.siendoDefendidoPor)
        Assert.assertNull(receptor.defendiendoA)

        //Receptor recibe habilidad defensa del emisor
        val habilidadDefensa : Habilidad = Defensa(emisor, receptor)
        peleaService.recibirHabilidad(1,1,habilidadDefensa)
        val emisorRecuperado = aventureroService.recuperar(emisor.id!!)
        val receptorRecuperado = aventureroService.recuperar(receptor.id!!)

        //chequeo los valores nuevamente
        Assert.assertEquals(emisor.id!!, receptorRecuperado.siendoDefendidoPor!!.id)
        Assert.assertEquals(receptor.id!!, emisorRecuperado.defendiendoA!!.id)
    }

    @AfterEach
    fun cleanup() {
        dataService.deleteAll()
    }

}