package ar.edu.unq.epers.tactics.persistencia.service.impl

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.modelo.habilidades.AtaqueConMagia
import ar.edu.unq.epers.tactics.modelo.habilidades.Curar
import ar.edu.unq.epers.tactics.modelo.habilidades.Defensa
import ar.edu.unq.epers.tactics.modelo.habilidades.Meditacion
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

class PeleaServiceResolverTurnoTest {

    //DAOS
    private val partyDao: HibernatePartyDAO = HibernatePartyDAO()
    private val peleaDao: HibernatePeleaDAO = HibernatePeleaDAO()
    private val aventureroDao: HibernateAventureroDAO = HibernateAventureroDAO()
    private val dataDAO: HibernateDataDAO = HibernateDataDAO()
    private val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    private val formacionDAO: FormacionDAO = FormacionDAO()
    private val claseDAO = Neo4JClaseDAO()

    //services
    private val dataService: DataServiceImpl = DataServiceImpl()
    private val partyService : PartyServiceImpl = PartyServiceImpl(partyDao, dataDAO, formacionDAO, neo4JAventureroDAO, claseDAO)
    private val aventureroService : AventureroServiceImpl = AventureroServiceImpl(aventureroDao, partyDao)
    private val peleaService : PeleaServiceImpl = PeleaServiceImpl(peleaDao, partyDao, aventureroDao)

    //Datos Necesarios
    private lateinit var pelea:Pelea
    private lateinit var aliados1: Aventurero
    private lateinit var aliados2: Aventurero
    private lateinit var aliados3: Aventurero
    private lateinit var aliados4: Aventurero
    private lateinit var aliados5: Aventurero

    private lateinit var enemigos1: Aventurero
    private lateinit var enemigos2: Aventurero
    private lateinit var enemigos3: Aventurero
    private lateinit var enemigos4: Aventurero
    private lateinit var enemigos5: Aventurero

    private lateinit var tacticaDeAliado1De1: Tactica
    private lateinit var tacticaDeAliado1De2: Tactica
    private lateinit var tacticaDeAliado1De3: Tactica
    private lateinit var tacticaDeAliado1De4: Tactica
    private lateinit var tacticaDeAliado1De5: Tactica

    private lateinit var partyAliados: Party
    private lateinit var partyEnemigos: Party

    @BeforeEach
    fun prepararDatos(){
        //Creo el equipo de aliados
        aliados1 = Aventurero("aliados_1", 10, 10, 10, 20, "aliados_1.img")
        aliados2 = Aventurero("aliados_2", 10, 10, 10, 10, "aliados_2.img")
        aliados3 = Aventurero("aliados_3", 10, 10, 10, 10, "aliados_3.img")
        aliados4 = Aventurero("aliados_4", 10, 10, 10, 10, "aliados_4.img")
        aliados5 = Aventurero("aliados_5", 10, 10, 10, 10, "aliados_5.img")

        //Creo el equipo enemigo (axis)
        enemigos1 = Aventurero("enemigos_1", 10, 10, 10, 10, "enemigos_1.img")
        enemigos2 = Aventurero("enemigos_2", 10, 10, 2, 10, "enemigos_2.img")
        enemigos3 = Aventurero("enemigos_3", 10, 10, 18, 10, "enemigos_3.img")
        enemigos4 = Aventurero("enemigos_4", 10, 10, 18, 10, "enemigos_4.img")
        enemigos5 = Aventurero("enemigos_5", 10, -1, 50, 10, "enemigos_5.img")

        //Le genero 3 tácticas a probar al aliados_1
        tacticaDeAliado1De1 = Tactica(1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.MANA, Criterio.MENOR_QUE, 22, Accion.MEDITAR, aliados1)
        tacticaDeAliado1De2 = Tactica(2, TipoDeReceptor.ALIADO,TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 10, Accion.CURAR,aliados1)
        tacticaDeAliado1De3 = Tactica(3, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.ARMADURA, Criterio.MAYOR_QUE, 20, Accion.ATAQUE_MAGICO,aliados1)
        tacticaDeAliado1De4 = Tactica(4, TipoDeReceptor.ALIADO, TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 30, Accion.DEFENDER,aliados1)
        tacticaDeAliado1De5 = Tactica(5, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0, Accion.ATAQUE_FISICO,aliados1)
        aliados1.tacticas.add(tacticaDeAliado1De1)
        aliados1.tacticas.add(tacticaDeAliado1De2)
        aliados1.tacticas.add(tacticaDeAliado1De3)
        aliados1.tacticas.add(tacticaDeAliado1De4)
        aliados1.tacticas.add(tacticaDeAliado1De5)

        //Genero los partys
        partyAliados = Party("ALIADOS")
        partyEnemigos = Party("ENEMIGOS")

        partyAliados = partyService.crear(partyAliados)
            aliados1 = partyService.agregarAventureroAParty(partyAliados.id!!, aliados1)
            aliados2 = partyService.agregarAventureroAParty(partyAliados.id!!, aliados2)
            aliados3 = partyService.agregarAventureroAParty(partyAliados.id!!, aliados3)
            aliados4 = partyService.agregarAventureroAParty(partyAliados.id!!, aliados4)
            aliados5 = partyService.agregarAventureroAParty(partyAliados.id!!, aliados5)

        partyEnemigos = partyService.crear(partyEnemigos)
            enemigos1 = partyService.agregarAventureroAParty(partyEnemigos.id!!, enemigos1)
            enemigos2 = partyService.agregarAventureroAParty(partyEnemigos.id!!, enemigos2)
            enemigos3 = partyService.agregarAventureroAParty(partyEnemigos.id!!, enemigos3)
            enemigos4 = partyService.agregarAventureroAParty(partyEnemigos.id!!, enemigos4)
            enemigos5 = partyService.agregarAventureroAParty(partyEnemigos.id!!, enemigos5)

        //inicio la pelea
        pelea = peleaService.iniciarPelea(partyAliados.id!!, partyEnemigos.id!!)

    }

    @Test
    fun meditarSubeElNivelDeMana(){
        //el Mana de aliados_1 es
        Assert.assertEquals(aliados1.nivel, 1)
        Assert.assertEquals(aliados1.inteligencia, 20)
        Assert.assertEquals(aliados1.mana, 21)

        //le bajo el mana para que no tenga el limite.
        aliados1.mana = 0
        Assert.assertEquals(aliados1.mana, 0)

        //lo actualizo en la DB
        aliados1 = aventureroService.actualizar(aliados1)
        Assert.assertEquals(aliados1.mana, 0)

        //la primer táctica de su lista indica que si mana<30 debe meditar.
        //meditar debería setear su mana = 1 dado que solo recupera el valor de su nivel (1)
        val habilidadResultante = peleaService.resolverTurno(pelea.id!!, aliados1.id!!)
        peleaService.recibirHabilidad(pelea.id!!, habilidadResultante.receptor.id!!, habilidadResultante)

        //la habilidad resultante es
        Assert.assertTrue(habilidadResultante is Meditacion)

        Assert.assertTrue(habilidadResultante.emisor.id == aliados1.id)

        //actualizo el aventurero en la DB
        val aliadoActualizado = aventureroService.actualizar(habilidadResultante.emisor)

        //el mana le debería incrementar de 0 a 1
        Assert.assertEquals(1, habilidadResultante.emisor.mana)
        Assert.assertEquals(1, aliadoActualizado.mana)
    }

    @Test
    fun meditarAlEstarFullNoSubeElNivelDeMana(){
        //el Mana de aliados_1 es
        Assert.assertEquals(aliados1.nivel, 1)
        Assert.assertEquals(aliados1.inteligencia, 20)
        Assert.assertEquals(aliados1.mana, 21)

        //la primer táctica de su lista indica que si mana<30 debe meditar.
        //meditar debería dejar su mana = 21 dado que es su límite según nivel+inteligencia
        val habilidadResultante = peleaService.resolverTurno(pelea.id!!, aliados1.id!!)
        peleaService.recibirHabilidad(pelea.id!!, habilidadResultante.receptor.id!!, habilidadResultante)

        //la habilidad resultante es
        Assert.assertTrue(habilidadResultante is Meditacion)

        Assert.assertTrue(habilidadResultante.emisor.id == aliados1.id)

        //actualizo el aventurero en la DB
        val aliadoActualizado = aventureroService.actualizar(habilidadResultante.emisor)

        //el mana le debería incrementar de 0 a 1
        Assert.assertEquals(21, habilidadResultante.emisor.mana)
        Assert.assertEquals(21, aliadoActualizado.mana)
    }

    @Test
    fun curarAliadoConPocaVidaIncrementaSuVida(){
        //el emisor será aliados_1
        //el receptor debería ser aliados_4

        //Me aseguro de tener maná como para poder curar a alguien.
        aliados1.mana = 50
        aventureroService.actualizar(aliados1)

        //Bajo la vida de aliados_4 para que al resolverTurno matchee curar aliado por táctica 2.
        aliados4.vida = 5
        aventureroService.actualizar(aliados4)

        val habilidadResultante = peleaService.resolverTurno(pelea.id!!, aliados1.id!!)
        peleaService.recibirHabilidad(pelea.id!!, habilidadResultante.receptor.id!!, habilidadResultante)

        //la habilidad resultante es
        Assert.assertTrue(habilidadResultante is Curar)

        //me aseguro que haya seleccionado correctamente al aliado_4
        Assert.assertEquals(habilidadResultante.receptor.nombre,aliados4.nombre)
        Assert.assertEquals(habilidadResultante.receptor.id,aliados4.id)

        val aliado4Actualizado = aventureroService.recuperar(aliados4.id!!)

        //el poder mágico de aliado_1 es 21 con lo cual la vida de aliado_4 debe pasar de 5 a 26
        Assert.assertEquals(26, habilidadResultante.receptor.vida)
        Assert.assertEquals(26, aliado4Actualizado.vida)
    }

    @Test
    fun atacarAEnemigoSegunTacticaResuelveOK(){
        //el emisor del ataque es aliado_1
        //el receptor por condiciones debería ser enemigo_5 es el único enemigo con armadora = 51 (mayor que 20)
        //la velocidad del receptor es 0 para que todos los tests pasen debido al random de atacarConMagia
        //el ataque del aliado_1 vale 21... debería decrementar 21 de la vida del receptor.
        //la vida del receptor al iniciar es: 115. El resultado debería ser 115-21  = 94

        //le doy 100 de mana a aliado 1 para que no entre en la táctica 1
        aliados1.mana = 100
        aventureroService.actualizar(aliados1)

        val habilidadResultante = peleaService.resolverTurno(pelea.id!!, aliados1.id!!)
        peleaService.recibirHabilidad(pelea.id!!, habilidadResultante.receptor.id!!, habilidadResultante)

        //la habilidad resultante es
        Assert.assertTrue(habilidadResultante is AtaqueConMagia)

        //me aseguro que haya seleccionado correctamente al enemigo5
        Assert.assertEquals(habilidadResultante.receptor.nombre,enemigos5.nombre)
        Assert.assertEquals(habilidadResultante.receptor.id,enemigos5.id)

        //actualizo el aventurero en la DB
        aventureroService.actualizar(habilidadResultante.receptor)

        val enemigos5Recuperado = aventureroService.recuperar(enemigos5.id!!)

        //el poder mágico de aliado_1 es 21 con lo cual la vida de aliado_4 debe pasar de 5 a 26
        Assert.assertEquals(94, habilidadResultante.receptor.vida)
        Assert.assertEquals(94, enemigos5Recuperado.vida)
    }

    @Test
    fun defenderAliado(){
        //voy a usar aliado_2 para defender dado que necesito setearle distinta lista de tácticas.

        //actualizo la lista de tacticas de aliado_1 para que pueda defender
        val tacticadealiado2Uno = Tactica(2, TipoDeReceptor.ALIADO,TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 10, Accion.CURAR,aliados2)
        val tacticadealiado2Dos = Tactica(4, TipoDeReceptor.ALIADO, TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 30, Accion.DEFENDER,aliados2)

        aliados2.tacticas.add(tacticadealiado2Uno)
        aliados2.tacticas.add(tacticadealiado2Dos)

        aventureroService.actualizar(aliados2)

        //seteo la vida de todos mis aliados a 50, menos aliado_3 quien usaremos para defender con la táctica2
        aliados1.vida = 150
        aliados2.vida = 150
        aliados4.vida = 150
        aliados5.vida = 150

        aliados3.vida = 23

        aventureroService.actualizar(aliados1)
        aventureroService.actualizar(aliados2)
        aventureroService.actualizar(aliados3)
        aventureroService.actualizar(aliados4)
        aventureroService.actualizar(aliados5)

        //el aliado a defender es aliados_3
        val habilidadResultante = peleaService.resolverTurno(pelea.id!!, aliados2.id!!)
        peleaService.recibirHabilidad(pelea.id!!, habilidadResultante.receptor.id!!, habilidadResultante)

        //la habilidad resultante es
        Assert.assertTrue(habilidadResultante is Defensa)

        //me aseguro que haya seleccionado correctamente al aliado que quiero defender
        Assert.assertEquals(habilidadResultante.receptor.nombre,aliados3.nombre)
        Assert.assertEquals(habilidadResultante.receptor.id,aliados3.id)

        //chequeo si el receptor está siendo defendido por emisor
        Assert.assertEquals(habilidadResultante.receptor.siendoDefendidoPor?.id,aliados2.id)

        aventureroService.actualizar(habilidadResultante.receptor)
        val aventurero3recuperado:Aventurero = aventureroService.recuperar(aliados3.id!!)
        //vuelvo a chequear lo mismo... parece que no anda.
        Assert.assertEquals(aventurero3recuperado.siendoDefendidoPor?.nombre,aliados2.nombre)
    }

    @AfterEach
    fun cleanup() {
        dataService.deleteAll()
    }
}