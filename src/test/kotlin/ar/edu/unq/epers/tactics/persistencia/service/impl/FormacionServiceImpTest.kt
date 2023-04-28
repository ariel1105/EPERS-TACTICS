package ar.edu.unq.epers.tactics.persistencia.service.impl

import ar.edu.unq.epers.tactics.modelo.Atributo
import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.modelo.Requerimiento
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.impl.ClaseServiceImpl
import ar.edu.unq.epers.tactics.service.impl.FormacionServiceImp
import helpers.DataServiceImpl
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FormacionServiceImpTest {

    val formacionDAO = FormacionDAO()
    val formacionService = FormacionServiceImp()
    val claseDao: Neo4JClaseDAO = Neo4JClaseDAO()
    val aventureroDAO: AventureroDAO = HibernateAventureroDAO()
    val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    val partyDao: HibernatePartyDAO = HibernatePartyDAO()

    private val dataService: DataServiceImpl = DataServiceImpl()
    private val claseService = ClaseServiceImpl(claseDao, aventureroDAO, neo4JAventureroDAO, formacionDAO, partyDao)

    @Test
    fun crearFormacion(){
        claseService.crearClase("Mago")
        claseService.crearClase("Buda")

        val atributo1 = AtributoDeFormacion(Atributo.INTELIGENCIA,50)
        val atributo2 = AtributoDeFormacion(Atributo.CONSTITUCION,10)
        val requerimiento1 = Requerimiento("Mago",2)
        val requerimiento2 = Requerimiento("Buda",1)

        formacionService.crearFormacion("MagiaPotenciada", listOf(requerimiento1, requerimiento2), listOf(atributo1, atributo2))

        val formacionRecuperada = formacionDAO.recuperarPorNombre( "MagiaPotenciada")

        Assertions.assertEquals("MagiaPotenciada", formacionRecuperada!!.nombre)
        Assertions.assertEquals(2, formacionRecuperada!!.atributos.size)
        Assertions.assertEquals(2, formacionRecuperada!!.requisitos.size)
    }

    @Test
    fun recuperarTodasLasFormacionesTest(){
        //creo formaciones de prueba
        val atributo1a = AtributoDeFormacion(Atributo.INTELIGENCIA,50)
        val atributo2a = AtributoDeFormacion(Atributo.CONSTITUCION,10)
        val requerimiento1a = Requerimiento("Mago",2)
        val requerimiento2a = Requerimiento("Buda",1)
        val formacionA = Formacion("Formacion A", listOf(requerimiento1a, requerimiento2a), listOf(atributo1a, atributo2a))

        val atributo1b = AtributoDeFormacion(Atributo.FUERZA,50)
        val atributo2b = AtributoDeFormacion(Atributo.CONSTITUCION,20)
        val requerimiento1b = Requerimiento("Aventurero",2)
        val requerimiento2b = Requerimiento("Pokemon",1)
        val formacionB = Formacion("Formacion B", listOf(requerimiento1b, requerimiento2b), listOf(atributo1b, atributo2b))

        val atributo1c = AtributoDeFormacion(Atributo.INTELIGENCIA,50)
        val atributo2c = AtributoDeFormacion(Atributo.DESTREZA,10)
        val requerimiento1c = Requerimiento("Mago",2)
        val requerimiento2c = Requerimiento("Pokemon",1)
        val formacionC = Formacion("Formacion C", listOf(requerimiento1c, requerimiento2c), listOf(atributo1c, atributo2c))

        formacionDAO.save(formacionA)
        formacionDAO.save(formacionB)
        formacionDAO.save(formacionC)

        val resultado = formacionService.todasLasFormaciones()

        Assert.assertEquals(3, resultado.size) //existen 3 Formaciones
    }

    @Test
    fun noSeCreaFormacionUnaClaseInexistenteComoRequisito(){
        val atributo = AtributoDeFormacion(Atributo.INTELIGENCIA,50)
        val requerimientoInvalido = Requerimiento("Payaso",2)

        val e:Exception = assertThrows(
            {formacionService.crearFormacion(
                "Formacion falsa",
                listOf(requerimientoInvalido),
                listOf(atributo)) } )
        Assert.assertEquals(e.message, "Los requerimientos no son validos")
    }

    @AfterEach
    fun cleanup(){
        dataService.deleteAll()
    }

}