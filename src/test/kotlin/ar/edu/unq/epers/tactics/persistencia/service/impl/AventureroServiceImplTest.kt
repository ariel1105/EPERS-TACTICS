package ar.edu.unq.epers.tactics.persistencia.service.impl

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.impl.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import helpers.DataServiceImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions

class AventureroServiceImplTest {

    //DAOS
    private val partyDAO: HibernatePartyDAO = HibernatePartyDAO()
    private val dataDAO: HibernateDataDAO = HibernateDataDAO()
    private val aventureroDAO: HibernateAventureroDAO = HibernateAventureroDAO()
    private val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    private val formacionDAO: FormacionDAO = FormacionDAO()
    private val claseDAO = Neo4JClaseDAO()

    //SERVICES
    private val dataService: DataServiceImpl = DataServiceImpl()
    private val aventureroService: AventureroServiceImpl = AventureroServiceImpl(aventureroDAO, partyDAO)
    private val partyService: PartyServiceImpl = PartyServiceImpl(partyDAO, dataDAO, formacionDAO, neo4JAventureroDAO, claseDAO)

    //MODELO
    private lateinit var roberto: Aventurero
    private lateinit var maria: Aventurero
    private lateinit var partyDeRoberto: Party

    @BeforeEach
    fun setup() {
        partyDeRoberto = partyService.crear(Party("Roberto's Team"))
        roberto = Aventurero("Roberto", 20,15,60,10,"https://www.eperstactics.com/roberto.jpg")
        maria = Aventurero("Maria", 15,20,40,15,"https://www.eperstactics.com/maria.jpg")
        roberto = partyService.agregarAventureroAParty(partyDeRoberto.id!!, roberto)
        maria = partyService.agregarAventureroAParty(partyDeRoberto.id!!, maria)
    }

    @Test
    fun atributosAventureroSinParty(){
        val Josefina = Aventurero("Josefina", 15, 20, 40, 15, "https://www.eperstactics.com/Josefina.jpg")

        Assertions.assertNull(Josefina.party)
        Assertions.assertEquals(15, Josefina.fuerza)
        Assertions.assertEquals(20, Josefina.destreza)
        Assertions.assertEquals(40, Josefina.constitucion)
        Assertions.assertEquals(15, Josefina.inteligencia)

    }

    @Test
    fun aventurerosSonCreadosConLosAtributosCorrectos(){
        Assertions.assertEquals(1, roberto.nivel)
        Assertions.assertEquals(0, roberto.experiencia)
        Assertions.assertEquals(145, roberto.vida)
        Assertions.assertEquals(61, roberto.armadura)
        Assertions.assertEquals(11, roberto.mana)
        Assertions.assertEquals(16, roberto.velocidad)
        Assertions.assertEquals(28, roberto.danioFisico)
        Assertions.assertEquals(11, roberto.danioMagico)
        Assertions.assertEquals(36, roberto.presicionFisica)

        Assertions.assertEquals(1, maria.nivel)
        Assertions.assertEquals(0, maria.experiencia)
        Assertions.assertEquals(100, maria.vida)
        Assertions.assertEquals(41, maria.armadura)
        Assertions.assertEquals(16, maria.mana)
        Assertions.assertEquals(21, maria.velocidad)
        Assertions.assertEquals(26, maria.danioFisico)
        Assertions.assertEquals(16, maria.danioMagico)
        Assertions.assertEquals(36, maria.presicionFisica)
    }

    @Test
    fun recuperar() {
        val robertoRecuperado = aventureroService.recuperar(roberto.id!!)
        Assertions.assertEquals("Roberto", robertoRecuperado.nombre)
    }

    @Test
    fun actualizar(){
        Assertions.assertEquals(145, roberto.vida)

        roberto.vida = 20
        aventureroService.actualizar(roberto)
        val robertoRecuperado = aventureroService.recuperar(roberto.id!!)

        Assertions.assertEquals(20, robertoRecuperado.vida)
    }

    @Test
    fun eliminar() {
        Assertions.assertEquals(2, partyService.recuperar(partyDeRoberto.id!!).numeroDeAventureros )

        aventureroService.eliminar(roberto)
        val robertoRecuperado = aventureroService.recuperar(roberto.id!!)
        Assertions.assertNull(robertoRecuperado)

        Assertions.assertEquals(1, partyService.recuperar(partyDeRoberto.id!!).numeroDeAventureros )
    }

    @Test
    fun tacticaPorPrioridad() {
        roberto.agregarTactica(Tactica(5, TipoDeReceptor.ENEMIGO,TipoDeEstadistica.ARMADURA,Criterio.MAYOR_QUE,50,Accion.ATAQUE_FISICO,maria))
        roberto.agregarTactica(Tactica(9, TipoDeReceptor.ENEMIGO,TipoDeEstadistica.ARMADURA,Criterio.MAYOR_QUE,50,Accion.ATAQUE_FISICO,maria))
        roberto.agregarTactica(Tactica(3, TipoDeReceptor.ENEMIGO,TipoDeEstadistica.ARMADURA,Criterio.MAYOR_QUE,50,Accion.ATAQUE_FISICO,maria))

        val tacticasOrdenadas = roberto.tacticaPorPrioridad()
        Assertions.assertEquals(3, tacticasOrdenadas.size)
        Assertions.assertEquals(3, tacticasOrdenadas[0].prioridad)
        Assertions.assertEquals(5, tacticasOrdenadas[1].prioridad)
        Assertions.assertEquals(9, tacticasOrdenadas[2].prioridad)
    }

    @AfterEach
    fun cleanup() {
        dataService.deleteAll()
    }

}