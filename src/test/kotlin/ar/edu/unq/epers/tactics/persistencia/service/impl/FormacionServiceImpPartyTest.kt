package ar.edu.unq.epers.tactics.persistencia.service.impl

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.impl.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.ClaseServiceImpl
import ar.edu.unq.epers.tactics.service.impl.FormacionServiceImp
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import helpers.DataServiceImpl
import org.junit.Assert
import org.junit.jupiter.api.*

class FormacionServiceImpPartyTest {

    //DAOS
    private val partyDAO = HibernatePartyDAO()
    private val aventureroDAO = HibernateAventureroDAO()
    private val formacionDAO = FormacionDAO()
    private val claseDao: Neo4JClaseDAO = Neo4JClaseDAO()
    private val dataDAO: HibernateDataDAO = HibernateDataDAO()
    private val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    private val claseDAO = Neo4JClaseDAO()

    //Services
    private val claseService = ClaseServiceImpl(claseDao, aventureroDAO, neo4JAventureroDAO, formacionDAO, partyDAO)
    private val formacionService = FormacionServiceImp()
    private val partyService = PartyServiceImpl(partyDAO, dataDAO, formacionDAO, neo4JAventureroDAO, claseDAO)
    private val aventureroService = AventureroServiceImpl(aventureroDAO, partyDAO)
    private val dataService: DataServiceImpl = DataServiceImpl()

    //CREO LA PARTY
    private val party = Party("LosYaNoDamosMas")
    private val partyCreada = partyService.crear(party)

    //DATOS TEST
    private lateinit var aventureroA: Aventurero
    private lateinit var aventureroB: Aventurero
    private lateinit var aventureroC: Aventurero
    private lateinit var aventureroD: Aventurero
    private lateinit var aventureroE: Aventurero

    private lateinit var atributo1a: AtributoDeFormacion
    private lateinit var atributo2a: AtributoDeFormacion
    private lateinit var atributo3a: AtributoDeFormacion
    private lateinit var atributo4a: AtributoDeFormacion
    private lateinit var atributo5a: AtributoDeFormacion
    private lateinit var atributo6a: AtributoDeFormacion

    private lateinit var requerimiento1a: Requerimiento
    private lateinit var requerimiento2a: Requerimiento
    private lateinit var requerimiento3a: Requerimiento
    private lateinit var requerimiento4a: Requerimiento

    private lateinit var formacionA: Formacion
    private lateinit var formacionB: Formacion
    private lateinit var formacionC: Formacion
    private lateinit var formacionD: Formacion

    @BeforeEach
    fun prepararDatos() {

        aventureroA = Aventurero("A", 10, 10, 10, 10, "imgA")
        aventureroB = Aventurero("B", 20, 20, 20, 20, "imgB")
        aventureroC = Aventurero("C", 30, 30, 30, 30, "imgC")
        aventureroD = Aventurero("D", 40, 40, 40, 40, "imgD")
        aventureroE = Aventurero("E", 50, 50, 50, 50, "imgE")

        aventureroA.experiencia = 1000
        aventureroB.experiencia = 1000

        partyCreada.id?.let { partyService.agregarAventureroAParty(it, aventureroA) }
        partyCreada.id?.let { partyService.agregarAventureroAParty(it, aventureroB) }
        partyCreada.id?.let { partyService.agregarAventureroAParty(it, aventureroC) }
        partyCreada.id?.let { partyService.agregarAventureroAParty(it, aventureroD) }
        partyCreada.id?.let { partyService.agregarAventureroAParty(it, aventureroE) }

        claseService.crearClase("Mago")
        claseService.crearClase("Clerigo")
        claseService.crearMejora("Aventurero", "Mago", listOf(Atributo.FUERZA), 5)
        claseService.crearMejora("Aventurero", "Clerigo", listOf(Atributo.INTELIGENCIA), 10)

        atributo1a = AtributoDeFormacion(Atributo.INTELIGENCIA,50)
        atributo2a = AtributoDeFormacion(Atributo.CONSTITUCION,10)
        atributo3a = AtributoDeFormacion(Atributo.FUERZA,20)
        atributo4a = AtributoDeFormacion(Atributo.DESTREZA,30)
        atributo5a = AtributoDeFormacion(Atributo.FUERZA,10)
        atributo6a = AtributoDeFormacion(Atributo.DESTREZA,120)

        requerimiento1a = Requerimiento("Aventurero",5)
        requerimiento2a = Requerimiento("Mago",1)
        requerimiento3a = Requerimiento("Clerigo",5)
        requerimiento4a = Requerimiento("Mago",2)

        formacionA = Formacion("Formacion A", listOf(requerimiento1a), listOf(atributo1a, atributo2a))
        formacionB = Formacion("Formacion B", listOf(requerimiento1a, requerimiento2a), listOf(atributo1a, atributo2a))
        formacionC = Formacion("Formacion C", listOf(requerimiento3a), listOf(atributo3a, atributo4a))
        formacionD = Formacion("Formacion D", listOf(requerimiento4a), listOf(atributo3a, atributo4a))

        formacionService.crearFormacion(formacionA.nombre, formacionA.requisitos, formacionA.atributos)
        formacionService.crearFormacion(formacionB.nombre, formacionB.requisitos, formacionB.atributos)
        formacionService.crearFormacion(formacionC.nombre, formacionC.requisitos, formacionC.atributos)
        formacionService.crearFormacion(formacionD.nombre, formacionD.requisitos, formacionD.atributos)
    }

    @Test
    fun partyNoRecibeFormacion() {
        val formacionesPartyCreada: List<Formacion> =
            formacionService.formacionesQuePosee(partyCreada.id!!.toInt())

        Assert.assertFalse( formacionesPartyCreada.contains(formacionB) )
        Assert.assertFalse( formacionesPartyCreada.contains(formacionC) )
    }

    @Test
    fun atributosQueCorresponden(){
        val atributosQueCorresponden = formacionService.atributosQueCorresponden(partyCreada.id!!.toInt())
        val valorDeInteligencia = atributosQueCorresponden.find{it.atributo == Atributo.INTELIGENCIA }!!.cantidad
        val valorDeConsitucion = atributosQueCorresponden.find{it.atributo == Atributo.CONSTITUCION }!!.cantidad

        Assert.assertEquals(10, valorDeConsitucion)
        Assert.assertEquals(50, valorDeInteligencia)
        Assert.assertEquals(2, atributosQueCorresponden.size)
    }

    @Test
    fun elCalculoDeAtributosDeFormacionSeHaceCorrectamenteYLaPartyLoMuestra(){
        val atributosQueCorresponden = formacionService.atributosQueCorresponden(partyCreada.id!!.toInt())
        val partyRecuperada = partyService.recuperar(partyCreada.id!!)

        Assert.assertEquals(2, atributosQueCorresponden.size)
        Assert.assertEquals(50, partyRecuperada.inteligenciaFormacion)
        Assert.assertEquals(10, partyRecuperada.constitucionFormacion)
    }

    @Test
    fun atributosSeActualizanAlGanarProficiencia(){
        claseService.ganarProficiencia(aventureroA.id!!, "Aventurero", "Mago")

        val atributosQueCorresponden = formacionService.atributosQueCorresponden(partyCreada.id!!.toInt())
        val valorDeInteligencia = atributosQueCorresponden.find{it.atributo == Atributo.INTELIGENCIA }!!.cantidad
        val valorDeConsitucion = atributosQueCorresponden.find{it.atributo == Atributo.CONSTITUCION }!!.cantidad

        Assert.assertEquals(20, valorDeConsitucion)
        Assert.assertEquals(100, valorDeInteligencia)
        Assert.assertEquals(2, atributosQueCorresponden.size)
    }

    @Test
    fun atributosDeFormacionDeLaPartySeActualizanAlGanarUnaClase(){
        claseService.ganarProficiencia(aventureroA.id!!, "Aventurero", "Mago")

        val partyRecuperada = partyService.recuperar(partyCreada.id!!)
        val listaFormaciones: List<Formacion> =
            formacionService.formacionesQuePosee(partyCreada.id!!.toInt())

        Assert.assertEquals("Formacion A", listaFormaciones.get(0).nombre)
        Assert.assertEquals("Formacion B", listaFormaciones.get(1).nombre)
        Assert.assertEquals(100, partyRecuperada.inteligenciaFormacion)
        Assert.assertEquals(20, partyRecuperada.constitucionFormacion)
    }

    @Test
    fun atributosDeFormacionDeLaPartySeAlCrearseUnaFormacion(){
        var party = partyService.recuperar(partyCreada.id!!)

        Assert.assertEquals(50, party.inteligenciaFormacion)
        Assert.assertEquals(10, party.constitucionFormacion)
        Assert.assertEquals(0, party.fuerzaFormacion)
        Assert.assertEquals(0, party.destrezaFormacion)

        formacionService.crearFormacion("FormacionExtra",
                                        listOf(Requerimiento("Aventurero",2)),
                                        listOf(AtributoDeFormacion(Atributo.FUERZA, 10),
                                            AtributoDeFormacion(Atributo.DESTREZA, 50),
                                            AtributoDeFormacion(Atributo.CONSTITUCION, 20)))

        party = partyService.recuperar(partyCreada.id!!)

        Assert.assertEquals(50, party.inteligenciaFormacion)
        Assert.assertEquals(30, party.constitucionFormacion)
        Assert.assertEquals(10, party.fuerzaFormacion)
        Assert.assertEquals(50, party.destrezaFormacion)
    }

    @Test
    fun atributosDeFormacionesSeVenReflejadosEnLosAventureros(){
        claseService.ganarProficiencia(aventureroA.id!!, "Aventurero", "Mago")

        val aventureroArec = aventureroService.recuperar(aventureroA.id!!)
        val aventureroBrec = aventureroService.recuperar(aventureroB.id!!)
        val aventureroCrec = aventureroService.recuperar(aventureroC.id!!)
        val aventureroDrec = aventureroService.recuperar(aventureroD.id!!)
        val aventureroErec = aventureroService.recuperar(aventureroE.id!!)

        val avAvalorConstitucion = aventureroArec.constitucion
        val avBvalorConstitucion = aventureroBrec.constitucion
        val avCvalorConstitucion = aventureroCrec.constitucion
        val avDvalorConstitucion = aventureroDrec.constitucion
        val avEvalorConstitucion = aventureroErec.constitucion

        val avAvalorInteligencia = aventureroArec.inteligencia
        val avBvalorInteligencia = aventureroBrec.inteligencia
        val avCvalorInteligencia = aventureroCrec.inteligencia
        val avDvalorInteligencia = aventureroDrec.inteligencia
        val avEvalorInteligencia = aventureroErec.inteligencia

        Assert.assertEquals(30 ,avAvalorConstitucion)
        Assert.assertEquals(40 ,avBvalorConstitucion)
        Assert.assertEquals(50 ,avCvalorConstitucion)
        Assert.assertEquals(60 ,avDvalorConstitucion)
        Assert.assertEquals(70 ,avEvalorConstitucion)

        Assert.assertEquals(110, avAvalorInteligencia)
        Assert.assertEquals(120, avBvalorInteligencia)
        Assert.assertEquals(130, avCvalorInteligencia)
        Assert.assertEquals(140, avDvalorInteligencia)
        Assert.assertEquals(150, avEvalorInteligencia)
    }

    @Test
    fun formacionesQuePosee() {
        claseService.ganarProficiencia(aventureroA.id!!, "Aventurero", "Mago")
        claseService.ganarProficiencia(aventureroB.id!!, "Aventurero", "Mago")
        val formaciones = formacionService.formacionesQuePosee(partyCreada.id!!.toInt()).map{ it.nombre }

        Assert.assertTrue(formaciones.contains("Formacion A"))
        Assert.assertTrue(formaciones.contains("Formacion B"))
        Assert.assertFalse(formaciones.contains("Formacion C"))
    }

    @AfterEach
    fun cleanup(){
        dataService.deleteAll()
    }

}
