package helpers

import ar.edu.unq.epers.tactics.modelo.Mejora
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.service.runner.TransactionRunner

class Neo4JTestService(val claseDAO: ClaseDAO, val neo4JClaseDAOTEST: Neo4JClaseDAOTEST) {

    fun recuperarMejora(nombreClase: String, nombreClaseRequerida: String): Mejora {
        return TransactionRunner.runTrx {
            claseDAO.recuperarMejora(nombreClase, nombreClaseRequerida)
        }
    }
    fun recuperarNombreDeClase(nombre: String): String {
        return TransactionRunner.runTrx {
            neo4JClaseDAOTEST.recuperarClase(nombre)
        }
    }

    fun recuperarRequiere(nombreClase: String): List<String> {
        return TransactionRunner.runTrx{
            neo4JClaseDAOTEST.clasesRequeridas(nombreClase)
        }
    }

    fun recuperarAventurero(idHibernate: Long): Long{
        return TransactionRunner.runTrx {
            neo4JClaseDAOTEST.recuperarAventurero(idHibernate)
        }
    }

    fun crearDatosDePrueba(){
        TransactionRunner.runTrx {
            neo4JClaseDAOTEST.crearSetDatosIniciales()
        }
    }

    fun aventureroTieneClase(idHibernate: Long, clase: String):Boolean {
        return TransactionRunner.runTrx {
            neo4JClaseDAOTEST.aventureroTieneClase(idHibernate, clase)
        }
    }

    fun existeMejoraConNombres(nombreDeClaseOrigen: String, nombreDeClaseDestino: String): Boolean{
        return TransactionRunner.runTrx {
            claseDAO.existeMejoraConNombres(nombreDeClaseOrigen, nombreDeClaseDestino)
        }
    }
}