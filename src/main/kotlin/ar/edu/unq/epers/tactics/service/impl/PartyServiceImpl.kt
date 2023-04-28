package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.helpers.convertidor
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.persistencia.dao.DataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4J.Neo4JAventureroDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.runner.TransactionRunner.runTrx
import com.fasterxml.jackson.databind.ObjectMapper

class PartyServiceImpl(val partyDAO: PartyDAO,
                       val dataDAO: DataDAO,
                       val formacionDAO: FormacionDAO,
                       val neo4JAventureroDAO: Neo4JAventureroDAO,
                       val claseDAO: ClaseDAO): PartyService {

    override fun crear(party: Party): Party {
        return runTrx { partyDAO.crear(party) }
    }

    override fun actualizar(party: Party): Party {
        runTrx { partyDAO.actualizar(party) }
        return party
    }

    override fun recuperar(idDeLaParty: Long): Party {
        return runTrx { partyDAO.recuperar(idDeLaParty) }
    }

    override fun recuperarTodas(): List<Party> {
        return runTrx { partyDAO.recuperarTodas() }
    }

    override fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina: Int?): PartyPaginadas {
        return runTrx { partyDAO.recuperarOrdenadas(orden, direccion, pagina) }
    }

    override fun agregarAventureroAParty(idDeLaParty: Long, aventurero: Aventurero): Aventurero {

        return runTrx {
            val aventureroRecuperado = agregarAventureroAParty_Aux(idDeLaParty, aventurero)
            neo4JAventureroDAO.crear(aventureroRecuperado.id!!, aventureroRecuperado.nombre)
            partyDAO.actualizar(aventureroRecuperado.party!!)
            aventureroRecuperado
        }
        runTrx {
            val party = partyDAO.recuperar(idDeLaParty)
            val formaciones = formacionesDeLaParty(party)
            val atributosDeFormacion = formacionDAO.atributosQueCorresponden(formaciones)
            party.actualizarAtributosDeFormacion(atributosDeFormacion)
            partyDAO.actualizar(party)
        }
    }

    private fun agregarAventureroAParty_Aux(idDeLaParty: Long, aventurero: Aventurero): Aventurero {
        return runTrx{
            val partyRecuperada = partyDAO.recuperar(idDeLaParty)
            partyRecuperada.agregarAventurero(aventurero)
            partyDAO.actualizar(partyRecuperada)
            aventurero
        }
    }

    private fun formacionesDeLaParty(party: Party): MutableList<Formacion>{
        val res = mutableListOf<String>()
        for (av in party.aventureros){
            res += claseDAO.traerClasesDeAventurero(av.id!!)
        }
        return formacionDAO.formacionesQuePosee(convertidor.clasesToJson(res))
    }

}