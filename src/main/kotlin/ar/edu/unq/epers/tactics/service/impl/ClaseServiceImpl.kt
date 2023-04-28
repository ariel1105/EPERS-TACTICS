package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.helpers.convertidor
import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JAventureroDAO
import ar.edu.unq.epers.tactics.service.ClaseService
import ar.edu.unq.epers.tactics.service.runner.TransactionRunner
import com.fasterxml.jackson.databind.ObjectMapper

class ClaseServiceImpl(val claseDAO: ClaseDAO,
                       val aventureroDAO: AventureroDAO,
                       val neo4JAventureroDAO: Neo4JAventureroDAO,
                       val formacionDAO: FormacionDAO,
                       val partyDAO: PartyDAO
                       ): ClaseService {

    override fun crearClase(nombreClase: String) {
        TransactionRunner.runTrx{
            claseDAO.crear(nombreClase)
        }
    }

    override fun crearMejora(nombreDeClaseA: String, nombreDeClaseB: String, atributos: List<Atributo>, cantidadDeAtributos: Int) {
        TransactionRunner.runTrx{
            claseDAO.crearMejora(nombreDeClaseA, nombreDeClaseB, atributos, cantidadDeAtributos)
        }
    }

    override fun requerir(nombreDeClase: String, nombreDeClaseRequerida: String) {
        TransactionRunner.runTrx{
           claseDAO.requerir(nombreDeClase, nombreDeClaseRequerida)
        }
    }

    override fun puedeMejorar(aventureroId: Long, mejora: Mejora): Boolean {
        return TransactionRunner.runTrx{
            puedeMejorar_aux(aventureroId, mejora)
        }

    }

    override fun ganarProficiencia(aventureroId: Long, nombreDeClaseOrigen: String, nombreDeClaseDestino: String): Aventurero {
        return TransactionRunner.runTrx {
            if (! claseDAO.existeMejoraConNombres(nombreDeClaseOrigen, nombreDeClaseDestino)) {
                throw Exception("No existe mejora de clase origen a clase destino") }
            val mejora = claseDAO.recuperarMejora(nombreDeClaseOrigen, nombreDeClaseDestino)
            if (! puedeMejorar_aux(aventureroId, mejora)) {
                throw Exception("El aventurero no puede aplicar esta mejora") }
            neo4JAventureroDAO.ganarClase(aventureroId, mejora.claseDestino)
            aplicarMejora(aventureroId, mejora)
        }
    }

    override fun posiblesMejoras(aventureroId: Long): Set<Mejora> {
        return TransactionRunner.runTrx{
            claseDAO.posiblesMejoras(aventureroId)
        }
    }

    override fun existeClaseConNombre(clase: String): Boolean {
        return TransactionRunner.runTrx {
            claseDAO.existeClaseConNombre(clase)
        }
    }

    private fun aplicarMejora(aventureroId: Long, mejora: Mejora): Aventurero{
        val aventurero = aventureroDAO.recuperar(aventureroId)
        //actualizo aventurero
        aventurero.aplicarMejora(mejora)
        aventureroDAO.actualizar(aventurero)
        //recalculo boosters de party
        val party = partyDAO.recuperar(aventurero.party!!.id!!)
        val formaciones = formacionesDeLaParty(party)
        val atributosDeFormacion = formacionDAO.atributosQueCorresponden(formaciones)
        party.actualizarAtributosDeFormacion(atributosDeFormacion)
        partyDAO.actualizar(aventurero.party!!)
        return aventurero
    }

    private fun puedeMejorar_aux(aventureroId: Long, mejora: Mejora): Boolean {
        val aventurero = aventureroDAO.recuperar(aventureroId)
        val tienePuntosDisponibles = aventurero.tienePuntosDeExperiencia()
        val puedeMejorarPorClases = claseDAO.puedeMejorar(aventureroId, mejora)
        return tienePuntosDisponibles && puedeMejorarPorClases
    }

    private fun formacionesDeLaParty(party: Party): MutableList<Formacion>{
        val res = mutableListOf<String>()
        for (av in party.aventureros){
            res += claseDAO.traerClasesDeAventurero(av.id!!)
        }
        return formacionDAO.formacionesQuePosee(convertidor.clasesToJson(res))
    }

}