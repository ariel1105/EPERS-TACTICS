package ar.edu.unq.epers.tactics.persistencia.modelo

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
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
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import helpers.DataServiceImpl

class HabilidadTest {

    //Aventureros
    private lateinit var emisor: Aventurero
    private lateinit var receptorFuerte: Aventurero
    private lateinit var receptorDebilucho: Aventurero
    //Parties
    private lateinit var partyEmisor: Party
    private lateinit var partyReceptor: Party
    //DAOs
    private val partyDao = HibernatePartyDAO()
    private val aventureroDao = HibernateAventureroDAO()
    private val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    private val formacionDAO: FormacionDAO = FormacionDAO()
    private val claseDAO = Neo4JClaseDAO()

    //Services
    private val partyService = PartyServiceImpl(partyDao, HibernateDataDAO(),formacionDAO, neo4JAventureroDAO, claseDAO)
    private val peleaService = PeleaServiceImpl(HibernatePeleaDAO(), partyDao, aventureroDao)
    private val aventureroService = AventureroServiceImpl(aventureroDao,partyDao)
    private val dataService = DataServiceImpl()

    @BeforeEach
    fun setup() {
        emisor = Aventurero("Maria", 15,20,40,15,"https://www.eperstactics.com/maria.jpg")
        receptorFuerte = Aventurero("Fuerte", 1000,1000,1000,1000,"https://www.eperstactics.com/roberto.jpg")
        receptorDebilucho = Aventurero("Debilucho", 1,1,1,1,"https://www.eperstactics.com/roberto.jpg")

        partyEmisor = Party("Party Emisor")
        partyReceptor = Party("Party Receptor")

        partyEmisor = partyService.crear(partyEmisor)
        partyReceptor = partyService.crear(partyReceptor)

        partyService.agregarAventureroAParty(partyEmisor.id!!, emisor)
        partyService.agregarAventureroAParty(partyReceptor.id!!, receptorFuerte)
        partyService.agregarAventureroAParty(partyReceptor.id!!, receptorDebilucho)

        peleaService.iniciarPelea(partyEmisor.id!!, partyReceptor.id!!)

        emisor = aventureroService.recuperar(emisor.id!!)
        receptorFuerte = aventureroService.recuperar(receptorFuerte.id!!)
        receptorDebilucho = aventureroService.recuperar(receptorDebilucho.id!!)
    }

    @Test
    fun atacarTest(){
        //Ataque sobre Fuerte
        val habilidadAtaqueSobreFuerte = Ataque(emisor, receptorFuerte)
        habilidadAtaqueSobreFuerte.ejecutar()
        //El ataque no es exitoso, la vida del receptor se mantiene.
        Assertions.assertEquals(3005, receptorFuerte.vida)

        //Ataque sobre debilucho
        val habilidadAtaqueSobreDebilucho = Ataque(emisor, receptorDebilucho)
        //vida inicial de debilucho = nivel * 5 + constitucion * 2 + fuerza = 5+2+1 = 8
        //daño impactado por emisor = nivel + fuerza + destreza / 2 = 1+15+10 = 26
        //vida de debilucho al terminar la paliza = 8-26 = -18
        habilidadAtaqueSobreDebilucho.ejecutar()
        //El ataque es exitoso (-26)
        Assertions.assertEquals(-18, receptorDebilucho.vida)
    }

    @Test
    fun defenderTest(){
        //Fuerte defiende a Debilucho
        val habilidadDefender = Defensa(receptorFuerte, receptorDebilucho)
        habilidadDefender.ejecutar()

        //Emisor ataca a debilucho
        val habilidadAtaqueSobreFuerte = Ataque(emisor, receptorDebilucho)
        habilidadAtaqueSobreFuerte.ejecutar()

        //El defendido no recibe daño
        Assertions.assertEquals(8, receptorDebilucho.vida)
        //El defensor recibe la mitad (13)
        Assertions.assertEquals(2992, receptorFuerte.vida)
        //El defensor es el receptor fuerte.
        Assert.assertEquals(receptorDebilucho.siendoDefendidoPor!!.id, receptorFuerte.id)
    }

    @Test
    fun curarTest(){
        val habilidadCurar = Curar(emisor, receptorFuerte)
        habilidadCurar.ejecutar()

        //La vida no se modifica por estar al maximo.
        Assertions.assertEquals(3005, receptorFuerte.vida)
        //El mana del emisor disminuye 5 puntos.
        Assertions.assertEquals(11, emisor.mana)

        receptorFuerte.vida = 2000

        habilidadCurar.ejecutar()
        //Cura 16 puntos de vida.
        Assertions.assertEquals(2016, receptorFuerte.vida)
        //El mana del emisor disminuye 5 puntos.
        Assertions.assertEquals(6, emisor.mana)

        emisor.mana = 4
        //Tira excepcion por no tener mana suficiente.
        val e: Exception = assertThrows( { habilidadCurar.ejecutar() } )
        Assertions.assertEquals("Mana insufieciente para realizar la accion solicitada.", e.message)
    }

    @Test
    fun atacarConMagiaOfensivaTest(){
        val ataqueMagicoAFuerte = AtaqueConMagia(emisor, receptorFuerte)
        val ataqueMagicoADebilucho = AtaqueConMagia(emisor, receptorDebilucho)
        ataqueMagicoAFuerte.ejecutar()
        ataqueMagicoADebilucho.ejecutar()

        //El ataque falla
        Assertions.assertEquals(3005, receptorFuerte.vida)
        //El ataque es exitoso (-16)
        Assertions.assertEquals(-8, receptorDebilucho.vida)
        //Se consumieron 10 puntos de mana.
        Assertions.assertEquals(6, emisor.mana)

        emisor.mana = 4
        //Tira excepcion por no tener mana suficiente.
        val e: Exception = assertThrows( { ataqueMagicoAFuerte.ejecutar() } )
        Assertions.assertEquals("Mana insufieciente para realizar la accion solicitada.", e.message)
    }

    @Test
    fun meditarTest(){
        val meditar = Meditacion(emisor, emisor)
        meditar.ejecutar()
        //Como esta al maximo, el mana no aumenta
        Assertions.assertEquals(16, emisor.mana)

        emisor.mana = 0
        meditar.ejecutar()
        //Recupera un punto de mana.
        Assertions.assertEquals(1, emisor.mana)
    }

    @AfterEach
    fun cleanup() {
        dataService.deleteAll()
    }

}