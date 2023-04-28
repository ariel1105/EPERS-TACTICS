package ar.edu.unq.epers.tactics.persistencia.service.impl

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.*
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.impl.AventureroLeaderboardServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PeleaServiceImpl
import helpers.DataServiceImpl
import org.junit.Assert
import org.junit.jupiter.api.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AventureroLaderboardServiceTest {

    //DAOS
    private val partyDao: HibernatePartyDAO = HibernatePartyDAO()
    private val peleaDao: HibernatePeleaDAO = HibernatePeleaDAO()
    private val aventureroDao: HibernateAventureroDAO = HibernateAventureroDAO()
    private val dataDAO: HibernateDataDAO = HibernateDataDAO()
    private val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    private val formacionDAO: FormacionDAO = FormacionDAO()
    private val claseDAO = Neo4JClaseDAO()

    //services
    private val dataService : DataServiceImpl = DataServiceImpl()
    private val partyService : PartyServiceImpl = PartyServiceImpl(partyDao, dataDAO, formacionDAO, neo4JAventureroDAO, claseDAO)
    private val peleaService : PeleaServiceImpl = PeleaServiceImpl(peleaDao, partyDao, aventureroDao)
    private val aventureroLeaderboardService : AventureroLeaderboardServiceImpl = AventureroLeaderboardServiceImpl(aventureroDao)

    //Datos Necesarios
    private lateinit var pelea: Pelea
    private lateinit var aliado1: Aventurero
    private lateinit var aliado2: Aventurero
    private lateinit var mejorBuda: Aventurero
    private lateinit var peorCurandero: Aventurero

    private lateinit var enemigo1: Aventurero
    private lateinit var enemigo2: Aventurero
    private lateinit var peorBuda: Aventurero
    private lateinit var mejorCurandero: Aventurero

    private lateinit var partyAliados: Party
    private lateinit var partyEnemigos: Party

    @BeforeAll
    internal fun prepararDatos(){
        //Aventureros aliados
        aliado1 = Aventurero("aliado_1", 100, 100, 50, 30, "aliado_1.img")
        aliado2 = Aventurero("aliado_2", 100, 100, 60, 11, "aliado_2.img")
        aliado2.nivel = 50
        mejorBuda = Aventurero("budita", 100,100,100,100,"")
        mejorBuda.nivel = 10
        mejorBuda.mana = 0
        peorCurandero = Aventurero("clerigo", 100,100,30,10,"")
        peorCurandero.vida = 50

        //Aventureros enemigos
        enemigo1 = Aventurero("enemigo_1", 11, 75, 50, 60, "enemigo_1.img")
        enemigo2 = Aventurero("enemigo_2", 32, 11, 20, 12, "enemigo_2.img")
        peorBuda = Aventurero("malBuda", 100,100,100,100,"")
        peorBuda.mana = 0
        mejorCurandero = Aventurero("buenClerigo", 100,100,50,10,"")
        mejorCurandero.nivel = 3
        mejorCurandero.vida = 20

        //Agrego las tácticas
        aliado1.agregarTactica(Tactica(1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0, Accion.ATAQUE_FISICO, aliado1))

        aliado2.agregarTactica(Tactica(1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.MANA, Criterio.MENOR_QUE, 5, Accion.MEDITAR, aliado2))
        aliado2.agregarTactica(Tactica(2, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0, Accion.ATAQUE_MAGICO, aliado2))

        mejorBuda.agregarTactica(Tactica(1,TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0,Accion.MEDITAR, mejorBuda))

        peorCurandero.agregarTactica(Tactica(1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.MANA, Criterio.MENOR_QUE, 5, Accion.MEDITAR, peorCurandero))
        peorCurandero.agregarTactica(Tactica(1, TipoDeReceptor.UNO_MISMO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.CURAR, peorCurandero))

        enemigo1.agregarTactica(Tactica(1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0, Accion.ATAQUE_FISICO, enemigo1))
        enemigo2.agregarTactica(Tactica(1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0, Accion.ATAQUE_FISICO, enemigo2))

        peorBuda.agregarTactica(Tactica(1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0,Accion.MEDITAR, peorBuda))

        mejorCurandero.agregarTactica(Tactica(1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.MANA, Criterio.MENOR_QUE, 5, Accion.MEDITAR, mejorCurandero))
        mejorCurandero.agregarTactica(Tactica(1, TipoDeReceptor.UNO_MISMO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.CURAR, mejorCurandero))

        //Genero los partys
        partyAliados = Party("ALIADOS")
        partyAliados = partyService.crear(partyAliados)

        aliado1 = partyService.agregarAventureroAParty(partyAliados.id!!, aliado1)
        aliado2 = partyService.agregarAventureroAParty(partyAliados.id!!, aliado2)

        mejorBuda = partyService.agregarAventureroAParty(partyAliados.id!!, mejorBuda)

        peorCurandero = partyService.agregarAventureroAParty(partyAliados.id!!, peorCurandero)

        partyEnemigos = Party("ENEMIGOS")
        partyEnemigos = partyService.crear(partyEnemigos)

        enemigo1 = partyService.agregarAventureroAParty(partyEnemigos.id!!, enemigo1)
        enemigo2 = partyService.agregarAventureroAParty(partyEnemigos.id!!, enemigo2)

        peorBuda = partyService.agregarAventureroAParty(partyEnemigos.id!!, peorBuda)
        mejorCurandero = partyService.agregarAventureroAParty(partyEnemigos.id!!, mejorCurandero)

        //inicio la pelea
        pelea = peleaService.iniciarPelea(partyAliados.id!!, partyEnemigos.id!!)

        partyAliados = partyService.recuperar(partyAliados.id!!)
        partyEnemigos = partyService.recuperar(partyEnemigos.id!!)

        //simulacion pelea
        //Armo una lista con todos los aventureros ordenados por velocidad para simular una pelea
        val todosLosAventureros: List<Aventurero> = (partyAliados.aventureros.toList() + partyEnemigos.aventureros.toList()).sortedByDescending { it.velocidad }

        for (av in todosLosAventureros){
            if (!av.nockeado
                and
                partyService.recuperar(partyAliados.id!!).aventureros.any{ !it.nockeado }
                and
                partyService.recuperar(partyEnemigos.id!!).aventureros.any{ !it.nockeado }) {

                val habilidad = peleaService.resolverTurno(pelea.id!!, av.id!!)
                peleaService.recibirHabilidad(pelea.id!!, habilidad.receptor.id!!, habilidad)
            }
        }

        peleaService.terminarPelea(pelea.id!!)

    }

    @Test
    @Order(1)
    fun mejorGuerreroTest(){
        val mejorGuerrero = aventureroLeaderboardService.mejorGuerrero()
        Assert.assertEquals(aliado1.id, mejorGuerrero.id)
    }

    @Test
    @Order(2)
    fun curanderoTopTest(){
        val curanderoTop = aventureroLeaderboardService.mejorCurandero()
        Assert.assertEquals(mejorCurandero.id, curanderoTop.id)
    }

    @Test
    @Order(3)
    fun budaTest(){
        val buda = aventureroLeaderboardService.buda()
        Assert.assertEquals(buda.id, mejorBuda.id)
    }

    @Test
    @Order(4)
    fun mejorMago(){
        val merlin = aventureroLeaderboardService.mejorMago()
        Assert.assertEquals(merlin.nombre, aliado2.nombre )
    }

    @Test
    @Order(5)
    fun laBusquedaNoTraeNingunAventureroTest(){
        dataService.deleteAll()

        val eMejorGuerrero: Exception = assertThrows( { aventureroLeaderboardService.mejorGuerrero() } )
        val eMejorCurandero: Exception = assertThrows( { aventureroLeaderboardService.mejorCurandero() } )
        val eBuda: Exception = assertThrows( { aventureroLeaderboardService.buda() } )
        val eMejorMago: Exception = assertThrows( { aventureroLeaderboardService.mejorMago() } )

        Assert.assertEquals("No hay un aventurero que cumpla los requisitos", eMejorGuerrero.message)
        Assert.assertEquals("No hay un aventurero que cumpla los requisitos", eMejorCurandero.message)
        Assert.assertEquals("No hay un aventurero que cumpla los requisitos", eBuda.message)
        Assert.assertEquals("No hay un aventurero que cumpla los requisitos", eMejorMago.message)
    }

    @AfterAll
    fun cleanup() {
        dataService.deleteAll()
    }

}
