package ar.edu.unq.epers.tactics.persistencia.service.impl

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import helpers.DataServiceImpl
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PartyServiceRecuperarOrdenadasTest {
    //DAOs
    private val dao: HibernatePartyDAO = HibernatePartyDAO()
    private val dataDAO: HibernateDataDAO = HibernateDataDAO()
    private val neo4JAventureroDAO: Neo4JAventureroDAO = Neo4JAventureroDAO()
    private val formacionDAO: FormacionDAO = FormacionDAO()
    private val claseDAO = Neo4JClaseDAO()

    //Services
    private val partyService : PartyServiceImpl = PartyServiceImpl(dao, dataDAO, formacionDAO, neo4JAventureroDAO, claseDAO)
    private val dataService: DataServiceImpl = DataServiceImpl()

    @BeforeEach
    fun prepararDatos(){

        //creo parties de prueba
        val party1 = Party("V200_D100_P50")
            party1.victorias = 200
            party1.derrotas = 100
            party1.poder = 50
        val party2  = Party("V180_D120_P48")
            party2.victorias = 180
            party2.derrotas = 120
            party2.poder = 48
        val party3  = Party("V30_D140_P60")
            party3.victorias = 30
            party3.derrotas = 140
            party3.poder = 60
        val party4  = Party("V80_D40_P20")
            party4.victorias = 80
            party4.derrotas = 40
            party4.poder = 20
        val party5  = Party("V90_D60_P30")
            party5.victorias = 90
            party5.derrotas = 60
            party5.poder = 30
        val party6  = Party("V10_D190_P70")
            party6.victorias = 10
            party6.derrotas = 190
            party6.poder = 70
        val party7  = Party("V100_D10_P10")
            party7.victorias = 100
            party7.derrotas = 10
            party7.poder = 10
        val party8  = Party("V100_D20_P30")
            party8.victorias = 100
            party8.derrotas = 20
            party8.poder = 30
        val party9  = Party("V300_D70_P120")
            party9.victorias = 300
            party9.derrotas = 70
            party9.poder = 120
        val party10  = Party("V280_D20_P160")
            party10.victorias = 280
            party10.derrotas = 20
            party10.poder = 160
        val party11  = Party("V30_D280_P40")
            party11.victorias = 30
            party11.derrotas = 280
            party11.poder = 40
        val party12  = Party("V180_D20_P150")
            party12.victorias = 180
            party12.derrotas = 20
            party12.poder = 150
        val party13  = Party("V190_D30_P160")
            party13.victorias = 190
            party13.derrotas = 30
            party13.poder = 160
        val party14  = Party("V190_D30_P160")
            party14.victorias = 190
            party14.derrotas = 30
            party14.poder = 160
        val party15  = Party("V5_D9_P2")
            party15.victorias = 5
            party15.derrotas = 9
            party15.poder = 2
        val party16  = Party("V15_D10_P5")
            party16.victorias = 15
            party16.derrotas = 10
            party16.poder = 5
        val party17  = Party("V60_D70_P40")
            party17.victorias = 60
            party17.derrotas = 70
            party17.poder = 40
        val party18  = Party("V35_D10_P20")
            party18.victorias = 35
            party18.derrotas = 10
            party18.poder = 20
        val party19  = Party("V65_D15_P22")
            party19.victorias = 65
            party19.derrotas = 15
            party19.poder = 22
        val party20  = Party("V170_D60_P80")
            party20.victorias = 170
            party20.derrotas = 60
            party20.poder = 80
        val party21  = Party("V0_D10_P1")
            party21.victorias = 0
            party21.derrotas = 10
            party21.poder = 1
        val party22  = Party("V0_D0_P1")
            party22.victorias = 0
            party22.derrotas = 0
            party22.poder = 1


        //las persisto
        partyService.crear(party1)
        partyService.crear(party2)
        partyService.crear(party3)
        partyService.crear(party4)
        partyService.crear(party5)
        partyService.crear(party6)
        partyService.crear(party7)
        partyService.crear(party8)
        partyService.crear(party9)
        partyService.crear(party10)
        partyService.crear(party11)
        partyService.crear(party12)
        partyService.crear(party13)
        partyService.crear(party14)
        partyService.crear(party15)
        partyService.crear(party16)
        partyService.crear(party17)
        partyService.crear(party18)
        partyService.crear(party19)
        partyService.crear(party20)
        partyService.crear(party21)
        partyService.crear(party22)

    }

    @Test
    fun existiendo22PartiesSeRecuperan2PaginasDe10y1PaginaDe2(){
        val partyPaginadaResult = partyService.recuperarOrdenadas(Orden.VICTORIAS, Direccion.DESCENDENTE, 0)
            Assert.assertTrue(partyPaginadaResult.parties.size == 10)
        val partyPaginadaResultPage2 = partyService.recuperarOrdenadas(Orden.VICTORIAS, Direccion.DESCENDENTE, 1)
            Assert.assertTrue(partyPaginadaResultPage2.parties.size == 10)
        val partyPaginadaResultPage3 = partyService.recuperarOrdenadas(Orden.VICTORIAS, Direccion.DESCENDENTE, 2)
            Assert.assertTrue(partyPaginadaResultPage3.parties.size == 2)
    }

    @Test
    fun lasPaginasVienenOrdenadasPorVictoriasAscendente(){
        val partyPaginadaResult = partyService.recuperarOrdenadas(Orden.VICTORIAS, Direccion.ASCENDENTE, 0)
        Assert.assertEquals(partyPaginadaResult.parties[0].victorias, 0)
        Assert.assertEquals(partyPaginadaResult.parties[9].victorias, 65)
        Assert.assertEquals(partyPaginadaResult.parties[9].nombre, "V65_D15_P22")
        val partyPaginadaResultPage2 = partyService.recuperarOrdenadas(Orden.VICTORIAS, Direccion.ASCENDENTE, 1)
        Assert.assertEquals(partyPaginadaResultPage2.parties[0].victorias, 80)
        Assert.assertEquals(partyPaginadaResultPage2.parties[9].victorias, 200)
        Assert.assertEquals(partyPaginadaResultPage2.parties[9].nombre, "V200_D100_P50")
        val partyPaginadaResultPage3 = partyService.recuperarOrdenadas(Orden.VICTORIAS, Direccion.ASCENDENTE, 2)
        Assert.assertEquals(partyPaginadaResultPage3.parties[0].victorias, 280)
        Assert.assertEquals(partyPaginadaResultPage3.parties[1].nombre, "V300_D70_P120")
    }


    @Test
    fun lasPaginasVienenOrdenadasPorVictoriasDescendiente(){
        val partyPaginadaResult = partyService.recuperarOrdenadas(Orden.VICTORIAS, Direccion.DESCENDENTE, 0)
            Assert.assertEquals(partyPaginadaResult.parties[0].victorias, 300)
            Assert.assertEquals(partyPaginadaResult.parties[9].victorias, 100)
            Assert.assertEquals(partyPaginadaResult.parties[9].nombre, "V100_D20_P30")
        val partyPaginadaResultPage2 = partyService.recuperarOrdenadas(Orden.VICTORIAS, Direccion.DESCENDENTE, 1)
            Assert.assertEquals(partyPaginadaResultPage2.parties[0].victorias, 90)
            Assert.assertEquals(partyPaginadaResultPage2.parties[9].victorias, 5)
            Assert.assertEquals(partyPaginadaResultPage2.parties[9].nombre, "V5_D9_P2")
        val partyPaginadaResultPage3 = partyService.recuperarOrdenadas(Orden.VICTORIAS, Direccion.DESCENDENTE, 2)
            Assert.assertEquals(partyPaginadaResultPage3.parties[0].victorias, 0)           //La anteúltima tiene cero victorias.
            Assert.assertEquals(partyPaginadaResultPage3.parties[1].victorias, 0)       //la última también tiene cero victorias.
    }

    @Test
    fun lasPaginasVienenOrdenadasPorDerrotasAscendente(){
        val partyPaginadaResult = partyService.recuperarOrdenadas(Orden.DERROTAS, Direccion.ASCENDENTE, 0)
        Assert.assertEquals(partyPaginadaResult.parties[0].derrotas, 0)
        Assert.assertEquals(partyPaginadaResult.parties[9].derrotas, 20)
        Assert.assertEquals(partyPaginadaResult.parties[9].nombre, "V180_D20_P150")
        val partyPaginadaResultPage2 = partyService.recuperarOrdenadas(Orden.DERROTAS, Direccion.ASCENDENTE, 1)
        Assert.assertEquals(partyPaginadaResultPage2.parties[0].derrotas, 30)
        Assert.assertEquals(partyPaginadaResultPage2.parties[9].derrotas, 140)
        Assert.assertEquals(partyPaginadaResultPage2.parties[9].nombre, "V30_D140_P60")
        val partyPaginadaResultPage3 = partyService.recuperarOrdenadas(Orden.DERROTAS, Direccion.ASCENDENTE, 2)
        Assert.assertEquals(partyPaginadaResultPage3.parties[0].derrotas, 190)
        Assert.assertEquals(partyPaginadaResultPage3.parties[1].nombre, "V30_D280_P40")
    }

    @Test
    fun lasPaginasVienenOrdenadasPorDerrotasDescendente(){
        val partyPaginadaResult = partyService.recuperarOrdenadas(Orden.DERROTAS, Direccion.DESCENDENTE, 0)
        Assert.assertEquals(partyPaginadaResult.parties[0].derrotas, 280)
        Assert.assertEquals(partyPaginadaResult.parties[9].derrotas, 40)
        Assert.assertEquals(partyPaginadaResult.parties[9].nombre, "V80_D40_P20")
        val partyPaginadaResultPage2 = partyService.recuperarOrdenadas(Orden.DERROTAS, Direccion.DESCENDENTE, 1)
        Assert.assertEquals(partyPaginadaResultPage2.parties[0].derrotas, 30)
        Assert.assertEquals(partyPaginadaResultPage2.parties[9].derrotas, 10)
        Assert.assertEquals(partyPaginadaResultPage2.parties[9].nombre, "V0_D10_P1")
        val partyPaginadaResultPage3 = partyService.recuperarOrdenadas(Orden.DERROTAS, Direccion.DESCENDENTE, 2)
        Assert.assertEquals(partyPaginadaResultPage3.parties[0].derrotas, 9)
        Assert.assertEquals(partyPaginadaResultPage3.parties[1].nombre, "V0_D0_P1")
    }

    @Test
    fun lasPaginasVienenOrdenadasPorPoderAscendente(){
        val partyPaginadaResult = partyService.recuperarOrdenadas(Orden.PODER, Direccion.ASCENDENTE, 0)
        Assert.assertEquals(partyPaginadaResult.parties[0].poder, 1)
        Assert.assertEquals(partyPaginadaResult.parties[9].poder, 30)
        Assert.assertEquals(partyPaginadaResult.parties[9].nombre, "V100_D20_P30")
        val partyPaginadaResultPage2 = partyService.recuperarOrdenadas(Orden.PODER, Direccion.ASCENDENTE, 1)
        Assert.assertEquals(partyPaginadaResultPage2.parties[0].poder, 40)
        Assert.assertEquals(partyPaginadaResultPage2.parties[9].poder, 160)
        Assert.assertEquals(partyPaginadaResultPage2.parties[9].nombre, "V280_D20_P160")
        val partyPaginadaResultPage3 = partyService.recuperarOrdenadas(Orden.PODER, Direccion.ASCENDENTE, 2)
        Assert.assertEquals(partyPaginadaResultPage3.parties[0].poder, 160)
        Assert.assertEquals(partyPaginadaResultPage3.parties[1].nombre, "V190_D30_P160")
    }

    @Test
    fun lasPaginasVienenOrdenadasPorPoderDescendente(){
        val partyPaginadaResult = partyService.recuperarOrdenadas(Orden.PODER, Direccion.DESCENDENTE, 0)
        Assert.assertEquals(partyPaginadaResult.parties[0].poder, 160)
        Assert.assertEquals(partyPaginadaResult.parties[9].poder, 48)
        Assert.assertEquals(partyPaginadaResult.parties[9].nombre, "V180_D120_P48")
        val partyPaginadaResultPage2 = partyService.recuperarOrdenadas(Orden.PODER, Direccion.DESCENDENTE, 1)
        Assert.assertEquals(partyPaginadaResultPage2.parties[0].poder, 40)
        Assert.assertEquals(partyPaginadaResultPage2.parties[9].poder, 2)
        Assert.assertEquals(partyPaginadaResultPage2.parties[9].nombre, "V5_D9_P2")
        val partyPaginadaResultPage3 = partyService.recuperarOrdenadas(Orden.PODER, Direccion.DESCENDENTE, 2)
        Assert.assertEquals(partyPaginadaResultPage3.parties[0].poder, 1)
        Assert.assertEquals(partyPaginadaResultPage3.parties[1].nombre, "V0_D0_P1")
    }

    @AfterEach
    fun cleanup() {
       dataService.deleteAll()
    }

}