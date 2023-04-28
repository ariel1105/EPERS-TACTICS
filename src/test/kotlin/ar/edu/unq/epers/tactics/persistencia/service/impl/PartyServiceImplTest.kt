package ar.edu.unq.epers.tactics.persistencia.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import helpers.Neo4JClaseDAOTEST
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import helpers.DataServiceImpl
import helpers.Neo4JTestService
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PartyServiceImplTest {

    //DAOs
    private val dao: HibernatePartyDAO = HibernatePartyDAO()
    private val dataDAO: HibernateDataDAO = HibernateDataDAO()
    private val claseDao: ClaseDAO = Neo4JClaseDAO()
    private val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    private val formacionDAO: FormacionDAO = FormacionDAO()
    private val neo4JClaseDAOTEST: Neo4JClaseDAOTEST = Neo4JClaseDAOTEST()
    private val claseDAO = Neo4JClaseDAO()

    //SERVICES
    private val partyService : PartyServiceImpl = PartyServiceImpl(dao, dataDAO, formacionDAO, neo4JAventureroDAO, claseDAO)
    private val dataService: DataServiceImpl = DataServiceImpl()
    private val neo4JTestService: Neo4JTestService = Neo4JTestService(claseDao, neo4JClaseDAOTEST)

    //variables
    private lateinit var partyAlfonso: Party
    private lateinit var partyBenito: Party
    private lateinit var partyCerati: Party
    private lateinit var partyZambia: Party
    private lateinit var party_neo: Party
    private lateinit var party0123: Party

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
        partyAlfonso = partyService.crear(partyAlfonso)
        partyBenito = partyService.crear(partyBenito)
        partyCerati = partyService.crear(partyCerati)
        partyZambia = partyService.crear(partyZambia)
        party_neo = partyService.crear(party_neo)
        party0123 = partyService.crear(party0123)
    }

    @Test
    fun alAgregarUnaPartyYRecuperarlaVuelveOK() {
        //Genero party y la persisto para obtener el id
        val party = Party("party 3")
        val createdParty = partyService.crear(party)

        //la cantidad de aventureros arranca en 0
        Assert.assertEquals(party.numeroDeAventureros, 0)

        //comparar todos los atributos
        Assert.assertEquals(party.id, createdParty.id)
        Assert.assertEquals(party.nombre, createdParty.nombre)
        Assert.assertEquals(party.imagenUrl, createdParty.imagenUrl)
    }

    @Test
    fun recuperadaVieneOK() {
        //recupero la primer party que había insertado en el prepararDatos
        val partyRecuperada: Party = partyService.recuperar(partyAlfonso.id!!)

        //la cantidad de aventureros arranca en 0
        Assert.assertEquals(0, partyRecuperada.numeroDeAventureros)

        //comparar todos los atributos
        Assert.assertEquals("Alfonso's Team", partyRecuperada.nombre)
        Assert.assertEquals("https://robohash.org/Alfonso.png", partyRecuperada.imagenUrl)
        Assert.assertTrue(true)
    }

    @Test
    fun alActualizarPartyModificaCorrectamente() {
        //creo una party inicial
        val partyOriginal = Party("Party Original")
        partyService.crear(partyOriginal)

        //recupero la party que persistí
        val partyOriginalRecuperada:Party = partyService.recuperar(partyOriginal.id!!)

        //el nombre y ID de la partyOriginal persistida es igual al nombre de la partyOriginalRecuperada
        Assert.assertEquals("Party Original", partyOriginalRecuperada.nombre)
        Assert.assertEquals(partyOriginal.id, partyOriginalRecuperada.id)

        //Modifico la party original y le mando update.
        partyOriginal.nombre = "Party Original V2"
        partyService.actualizar(partyOriginal)

        //vuelvo a traer otra copia de la db para asegurarme de que los nombres se modificaron ok.
        val partyRecuperadaDespuesDeCambioDeNombre:Party = partyService.recuperar(partyOriginal.id!!)

        //el ID tiene que ser igual, pero el nombre del recuperado tiene que ser Party Original V2
        Assert.assertEquals(partyRecuperadaDespuesDeCambioDeNombre.nombre, "Party Original V2")
        Assert.assertEquals(partyOriginal.id, partyRecuperadaDespuesDeCambioDeNombre.id)
    }

    @Test
    fun recuperarTodos(){
        //recupero la lista de todas las parties de la base de datos
        val lista : List<Party> = partyService.recuperarTodas()
        //me aseguro que venga en order alfanumérico
        Assert.assertTrue(lista.isNotEmpty())
        Assert.assertEquals(lista[0].nombre, "_neo's Team")
        Assert.assertEquals(lista[1].nombre, "0123's Team")
        Assert.assertEquals(lista[2].nombre, "Alfonso's Team")
        Assert.assertEquals(lista[3].nombre, "Benito's Team")
        Assert.assertEquals(lista[4].nombre, "Cerati's Team")
        Assert.assertEquals(lista[5].nombre, "Zambia's Team")
    }

    @Test
    fun agregarAventureroALaPartyGeneraIDEnAventuroYSubeLaCantidadDeAventurerosALaParty() {
        var nuevoAventurero = Aventurero("Carlos", 20,20,20,20,"")
        var nuevoAventurero2 = Aventurero("Pedro", 20, 30, 40, 50, "")
        nuevoAventurero = partyService.agregarAventureroAParty(partyAlfonso.id!!, nuevoAventurero)
        nuevoAventurero2 = partyService.agregarAventureroAParty(partyBenito.id!!, nuevoAventurero2)

        val partyRecuperada = partyService.recuperar(partyAlfonso.id!!)
        val idNodoAventurero = neo4JTestService.recuperarAventurero(nuevoAventurero.id!!)

        Assert.assertFalse(nuevoAventurero.id == nuevoAventurero2.id)
        Assert.assertTrue(nuevoAventurero.id != null)
        Assert.assertTrue(partyRecuperada.incluye(nuevoAventurero))
        Assert.assertEquals(1, partyRecuperada.numeroDeAventureros)
        Assert.assertEquals(nuevoAventurero.id!!, idNodoAventurero)
    }

    @Test
    fun noSeAgregaElAventureroALaPartySiYaTiene5AventurerosYNoSeGeneraElIDDelAventurero(){
        val av1 = Aventurero("1", 10,20,20,20,"")
        val av2 = Aventurero("2", 20,20,20,20,"")
        val av3 = Aventurero("3", 30,20,20,20,"")
        val av4 = Aventurero("4", 40,20,20,20,"")
        val av5 = Aventurero("5", 50,20,20,20,"")
        val av6 = Aventurero("6", 60,20,20,20,"")
        partyService.agregarAventureroAParty(partyAlfonso.id!!, av1)
        partyService.agregarAventureroAParty(partyAlfonso.id!!, av2)
        partyService.agregarAventureroAParty(partyAlfonso.id!!, av3)
        partyService.agregarAventureroAParty(partyAlfonso.id!!, av4)
        partyService.agregarAventureroAParty(partyAlfonso.id!!, av5)
        val exception: Exception = assertThrows {
            partyService.agregarAventureroAParty(partyAlfonso.id!!, av5) }
        Assert.assertEquals("La party cuenta con el numero maximo de aventureros", exception.message)
        Assert.assertNull(av6.id)
    }

    @AfterEach
    fun cleanup() {
        dataService.deleteAll()
    }

}