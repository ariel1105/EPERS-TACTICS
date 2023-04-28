package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.helpers.convertidor
import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Requerimiento
import ar.edu.unq.epers.tactics.modelo.exceptions.RequisitosInvalidosException
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.FormacionService
import ar.edu.unq.epers.tactics.service.runner.TransactionRunner.runTrx
import com.fasterxml.jackson.databind.ObjectMapper

class FormacionServiceImp(): FormacionService {

    val formacionDAO = FormacionDAO()
    val partyDAO = HibernatePartyDAO()
    val claseDAO = Neo4JClaseDAO()

    override fun crearFormacion(nombreFormacion: String, requerimientos: List<Requerimiento>, stats: List<AtributoDeFormacion>): Formacion {
        return runTrx{
            if (! sonRequisitosValidos(requerimientos)){ throw RequisitosInvalidosException("Los requerimientos no son validos") }
            val formacion = Formacion(nombreFormacion, requerimientos, stats)
            formacionDAO.save(formacion)
            actualizarLasParties()
            formacion
        }
    }

    private fun sonRequisitosValidos(requisitos: List<Requerimiento>): Boolean{
        return requisitos.map{it.nombreClase }.all { claseDAO.existeClaseConNombre(it) }
    }

    override fun todasLasFormaciones(): List<Formacion> {
        return runTrx{
            formacionDAO.recuperarTodas()
        }
    }

    override fun atributosQueCorresponden(partyId: Int): List<AtributoDeFormacion> {
        return runTrx{
            val party = partyDAO.recuperar(partyId.toLong())
            val formaciones = formacionDAO.formacionesQuePosee(convertidor.clasesToJson(clasesDeLaParty(party)))
            formacionDAO.atributosQueCorresponden(formaciones)
        }
    }

    override fun formacionesQuePosee(partyId: Int): List<Formacion> {
        return runTrx{
            val party = partyDAO.recuperar(partyId.toLong())
            formacionDAO.formacionesQuePosee(convertidor.clasesToJson(clasesDeLaParty(party)))
        }
    }

    private fun actualizarLasParties(){
        val parties = partyDAO.recuperarTodas()
        for (p in parties) {
            val formaciones = formacionDAO.formacionesQuePosee(convertidor.clasesToJson(clasesDeLaParty(p)))
            val atributosDeFormacion = formacionDAO.atributosQueCorresponden(formaciones)
            p.actualizarAtributosDeFormacion(atributosDeFormacion)
            partyDAO.actualizar(p)
        }
    }

    private fun clasesDeLaParty(party: Party): MutableList<String>{
        val res = mutableListOf<String>()
        for (av in party.aventureros){
            res += claseDAO.traerClasesDeAventurero(av.id!!)
        }
        return res
    }
}