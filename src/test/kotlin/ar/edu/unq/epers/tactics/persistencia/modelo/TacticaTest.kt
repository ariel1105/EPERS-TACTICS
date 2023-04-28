import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.impl.AventureroServiceImpl
import helpers.DataServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TacticaTest {

    //DAOS
    private val dao: HibernatePartyDAO = HibernatePartyDAO()
    private val dataDAO: HibernateDataDAO = HibernateDataDAO()
    private val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    private val formacionDAO: FormacionDAO = FormacionDAO()
    private val claseDAO = Neo4JClaseDAO()

    //SERVICES
    private val dataService: DataServiceImpl = DataServiceImpl()
    private val partyService : PartyServiceImpl = PartyServiceImpl(dao, dataDAO, formacionDAO, neo4JAventureroDAO, claseDAO)
    private val aventureroDAO: HibernateAventureroDAO = HibernateAventureroDAO()
    private val aventureroService: AventureroServiceImpl = AventureroServiceImpl(aventureroDAO, dao)

    //AVENTUREROS
    private val lalo: Aventurero = Aventurero("Lalo", 20, 30, 10, 35, "")
    private val lolo: Aventurero = Aventurero("Lolo", 40, 10, 50, 10, "")

    private val tacticaDeLalo1: Tactica = Tactica(1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.MANA, Criterio.MENOR_QUE, 10, Accion.MEDITAR, lalo)
    private val tacticaDeLalo2: Tactica = Tactica(2, TipoDeReceptor.ALIADO,TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 10, Accion.CURAR,lalo)
    private val tacticaDeLalo3: Tactica = Tactica(3, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.ARMADURA, Criterio.MAYOR_QUE, 20, Accion.ATAQUE_MAGICO,lalo)

    private val partyDeLalo: Party = Party("lalo's")
    private val partyDeLolo: Party = Party("Lolo's")

    @BeforeEach
    fun agregarTacticas(){
        lalo.tacticas.add(tacticaDeLalo1)
        lalo.tacticas.add(tacticaDeLalo2)
        lalo.tacticas.add(tacticaDeLalo3)

        partyService.crear(partyDeLalo)
        partyService.crear(partyDeLolo)

        // persisto aventureros para generar el ID
        partyService.agregarAventureroAParty(partyDeLalo.id!!, lalo)
        partyService.agregarAventureroAParty(partyDeLolo.id!!, lolo)
    }

    @Test
    fun tipoDeReceptor(){
        val laloRecuperado: Aventurero = aventureroService.recuperar(lalo.id!!)
        val esTipoCorrectoDeReceptor: Boolean = tacticaDeLalo1.esTacticaSegunTipoDeReceptor(laloRecuperado)
        Assert.assertTrue(esTipoCorrectoDeReceptor)
    }

    @Test
    fun valorDeEstadisticaRetornaDatosCorrectos(){
        //la vida de lalo es 45
        val laloRecuperado: Aventurero = aventureroService.recuperar(lalo.id!!)
        //el tipo de estadistica es MANA
        val manaDeLalo = tacticaDeLalo1.valorDeEstadistica(laloRecuperado)
        Assert.assertEquals(36, manaDeLalo)
    }

    @AfterEach
    fun cleanup() {
        dataService.deleteAll()
    }

}