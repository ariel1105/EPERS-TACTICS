package ar.edu.unq.epers.tactics.persistencia

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
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GanadorPeleaTest {

    //DAOS
    private val partyDao = HibernatePartyDAO()
    private val peleaDao = HibernatePeleaDAO()
    private val aventureroDao = HibernateAventureroDAO()
    private val dataDAO = HibernateDataDAO()
    private val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    private val formacionDAO = FormacionDAO()
    private val claseDAO = Neo4JClaseDAO()

    //services
    private val dataService: DataServiceImpl = DataServiceImpl()
    private val partyService : PartyServiceImpl = PartyServiceImpl(partyDao, dataDAO, formacionDAO, neo4JAventureroDAO, claseDAO)
    private val peleaService : PeleaServiceImpl = PeleaServiceImpl(peleaDao, partyDao, aventureroDao)
    private val aventureroService: AventureroServiceImpl = AventureroServiceImpl(aventureroDao, partyDao)


    //Datos Necesarios
    private lateinit var pelea: Pelea
    private lateinit var aliado1: Aventurero
    private lateinit var aliado2: Aventurero

    private lateinit var enemigo1: Aventurero
    private lateinit var enemigo2: Aventurero

    private lateinit var partyAliados: Party
    private lateinit var partyEnemigos: Party

    @BeforeAll
    internal fun prepararDatos(){
        //Aventureros aliados
        aliado1 = Aventurero("aliado_1", 100, 100, 50, 30, "aliado_1.img")
        aliado2 = Aventurero("aliado_2", 100, 100, 60, 11, "aliado_2.img")
        aliado2.nivel = 50

        //Aventureros enemigos
        enemigo1 = Aventurero("enemigo_1", 11, 75, 50, 60, "enemigo_1.img")
        enemigo2 = Aventurero("enemigo_2", 32, 11, 20, 12, "enemigo_2.img")

        //Agrego las tácticas
        aliado1.agregarTactica(Tactica(1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0, Accion.ATAQUE_FISICO, aliado1))

        aliado2.agregarTactica(Tactica(1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.MANA, Criterio.MENOR_QUE, 5, Accion.MEDITAR, aliado2))
        aliado2.agregarTactica(Tactica(2, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0, Accion.ATAQUE_MAGICO, aliado2))

        enemigo1.agregarTactica(Tactica(1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0, Accion.ATAQUE_FISICO, enemigo1))

        enemigo2.agregarTactica(Tactica(1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0, Accion.ATAQUE_FISICO, enemigo2))

        //Genero los partys
        partyAliados = Party("ALIADOS")
        partyAliados = partyService.crear(partyAliados)

        aliado1 = partyService.agregarAventureroAParty(partyAliados.id!!, aliado1)
        aliado2 = partyService.agregarAventureroAParty(partyAliados.id!!, aliado2)

        partyEnemigos = Party("ENEMIGOS")
        partyEnemigos = partyService.crear(partyEnemigos)

        enemigo1 = partyService.agregarAventureroAParty(partyEnemigos.id!!, enemigo1)
        enemigo2 = partyService.agregarAventureroAParty(partyEnemigos.id!!, enemigo2)

        //inicio la pelea
        pelea = peleaService.iniciarPelea(partyAliados.id!!, partyEnemigos.id!!)

        partyAliados = partyService.recuperar(partyAliados.id!!)
        partyEnemigos = partyService.recuperar(partyEnemigos.id!!)
    }

    @Test
    fun peleaTest(){
        //Armo una lista con todos los aventureros ordenados por velocidad para simular una pelea
        val todosLosAventureros: List<Aventurero> = (partyAliados.aventureros.toList() + partyEnemigos.aventureros.toList()).sortedByDescending { it.velocidad }

        while (partyService.recuperar(partyAliados.id!!).aventureros.any{ !it.nockeado }
                and
               partyService.recuperar(partyEnemigos.id!!).aventureros.any{ !it.nockeado }) {

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
        }

        pelea = peleaService.terminarPelea(pelea.id!!)

        Assertions.assertEquals(partyAliados.id!!, pelea.partyGanadora!!.id)
    }

    @Test
    fun subidaDeNivelYGanadaDeExp(){
        aliado1 = aventureroService.recuperar(aliado1.id!!)
        aliado2 = aventureroService.recuperar(aliado2.id!!)

        enemigo1 = aventureroService.recuperar(enemigo1.id!!)
        enemigo2 = aventureroService.recuperar(enemigo2.id!!)


        Assertions.assertEquals(2, aliado1.nivel)
        Assertions.assertEquals(1, aliado1.experiencia)
        Assertions.assertEquals(51, aliado2.nivel)
        Assertions.assertEquals(1, aliado2.experiencia)

        Assertions.assertEquals(1, enemigo1.nivel)
        Assertions.assertEquals(0, enemigo1.experiencia)
        Assertions.assertEquals(1, enemigo2.nivel)
        Assertions.assertEquals(0, enemigo2.experiencia)
    }

    @AfterAll
    fun cleanup() {
      dataService.deleteAll()
    }

}