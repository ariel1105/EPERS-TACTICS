package ar.edu.unq.epers.tactics.persistencia.service.impl

import ar.edu.unq.epers.tactics.modelo.*
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

class PeleaServiceImplTest {

    //DAOS
    private val partyDao: HibernatePartyDAO = HibernatePartyDAO()
    private val dataDAO: HibernateDataDAO = HibernateDataDAO()
    private val peleaDao: HibernatePeleaDAO = HibernatePeleaDAO()
    private val aventureroDao: HibernateAventureroDAO = HibernateAventureroDAO()
    private val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    private val formacionDAO: FormacionDAO = FormacionDAO()
    private val claseDAO = Neo4JClaseDAO()

    //Services
    private val dataService: DataServiceImpl = DataServiceImpl()
    private val partyService : PartyServiceImpl = PartyServiceImpl(partyDao, dataDAO, formacionDAO, neo4JAventureroDAO, claseDAO)
    private val peleaService : PeleaServiceImpl = PeleaServiceImpl(peleaDao, partyDao, aventureroDao)
    private val aventureroService : AventureroServiceImpl = AventureroServiceImpl(aventureroDao,partyDao)

    //Parties
    lateinit var partyAlfonso: Party
    lateinit var partyBenito: Party
    lateinit var partyCerati: Party
    lateinit var partyZambia: Party
    lateinit var party_neo: Party
    lateinit var party0123: Party

    //Aventureros
    private lateinit var cerati: Aventurero
    private lateinit var zambia: Aventurero
    private lateinit var benito: Aventurero

    @BeforeEach
    fun prepararDatosDePrueba(){

        //creo parties de prueba
        partyAlfonso = Party("Alfonso's Team")
        partyAlfonso.imagenUrl = "https://robohash.org/Alfonso.png"
        partyBenito  = Party("Benito's Team")
        partyBenito.imagenUrl = "https://robohash.org/Benito.png"
        partyCerati  = Party("Cerati's Team")
        partyCerati.imagenUrl = "https://robohash.org/Cerati.png"
        partyZambia  = Party("Zambia's Team")
        partyZambia.imagenUrl = "https://robohash.org/Zambia.png"
        party_neo    = Party("_neo's Team")
        party_neo.imagenUrl = "https://robohash.org/_neo.png"
        party0123    = Party("0123's Team")
        party0123.imagenUrl = "https://robohash.org/0123.png"

        //las persisto
        partyService.crear(partyAlfonso)    //id 1
        partyService.crear(partyBenito)     //id 2
        partyService.crear(partyCerati)     //id 3
        partyService.crear(partyZambia)     //id 4
        partyService.crear(party_neo)       //id 5
        partyService.crear(party0123)       //id 6

        //agrego aventureros a party
        cerati = Aventurero("cerati", 20,15,60,10,"https://www.eperstactics.com/cerati.jpg")
        zambia = Aventurero("zambia", 15,20,40,15,"https://www.eperstactics.com/zambia.jpg")
        benito = Aventurero("benito",12,30,50,15,"https://www.eperstactics.com/benito.jpg")
        cerati = partyService.agregarAventureroAParty(partyCerati.id!!, cerati)
        zambia = partyService.agregarAventureroAParty(partyZambia.id!!, zambia)
        benito = partyService.agregarAventureroAParty(partyBenito.id!!, benito)
    }

    @Test
    fun ningunaPartyEstaEnPeleaInicialmente() {

        //recupero la lista de todas las parties de la base de datos
        val lista : List<Party> = partyService.recuperarTodas()
        Assert.assertTrue(lista.isNotEmpty())
        Assert.assertNull(lista[1].pelea)
        Assert.assertNull(lista[2].pelea)
        Assert.assertNull(lista[3].pelea)
        Assert.assertNull(lista[4].pelea)
        Assert.assertNull(lista[5].pelea)
    }

    @Test
    fun crearPeleaEntreParty1yParty2YRevisarEstado(){
        peleaService.iniciarPelea(partyAlfonso.id!!,partyBenito.id!!)
        val partyA:Party = partyService.recuperar(partyAlfonso.id!!)
        val partyB:Party = partyService.recuperar(partyBenito.id!!)
        Assert.assertNotNull(partyA.pelea)
        Assert.assertNotNull(partyB.pelea)
    }
    @Test
    fun reseteoValoresDeAventurero(){
        //chequeo valores iniciales
        Assert.assertEquals(zambia.inteligencia,15)

        //seteo valores a aventurero
        zambia.mana=30
        zambia.siendoDefendidoPor = cerati
        zambia.defendiendoA = benito
        aventureroService.actualizar(zambia)

        //chequeo valores seteados
        Assert.assertEquals(zambia.mana,30)
        Assert.assertEquals(zambia.siendoDefendidoPor,cerati)
        Assert.assertEquals(zambia.defendiendoA,benito)


        zambia.resetearValores()
        aventureroService.actualizar(zambia)
        val zambiaRecuperado=aventureroService.recuperar(zambia.id!!)

        Assert.assertEquals(zambiaRecuperado.vida,100)
        Assert.assertEquals(zambiaRecuperado.mana,16)
        Assert.assertNull(zambiaRecuperado.siendoDefendidoPor)
        Assert.assertNull(zambiaRecuperado.defendiendoA)
        Assert.assertEquals(zambiaRecuperado.turnosDefendiendo,0)

    }

    @Test
    fun terminarPelea(){

        peleaService.iniciarPelea(partyAlfonso.id!!,partyBenito.id!!)
        val partyA:Party = partyService.recuperar(partyAlfonso.id!!)
        val partyB:Party = partyService.recuperar(partyBenito.id!!)
        Assert.assertNotNull(partyA.pelea)
        Assert.assertNotNull(partyB.pelea)

        //Inicio una pelea
        peleaService.iniciarPelea(partyCerati.id!!,partyZambia.id!!)
        val partyC:Party = partyService.recuperar(partyCerati.id!!)
        val partyZ:Party = partyService.recuperar(partyZambia.id!!)
        cerati.defendiendoA= benito
        aventureroService.actualizar(cerati)

       //Chequeo el estado de la pelea y los aventureros
        Assert.assertNotNull(partyC.pelea)
        Assert.assertNotNull(partyZ.pelea)
        Assert.assertEquals(cerati.defendiendoA,benito)
        Assert.assertNull(zambia.defendiendoA)

        //termino la pelea
        peleaService.terminarPelea(partyC.pelea?.id!!)

        val partyRecuperada= partyService.recuperar(partyC.id!!)
        val aventureroRecuperado=aventureroService.recuperar(cerati.id!!)

        Assert.assertNull(partyRecuperada.pelea)
        Assert.assertNull(aventureroRecuperado.defendiendoA)

    }

    @AfterEach
    fun cleanup() {
        dataService.deleteAll()
    }
}