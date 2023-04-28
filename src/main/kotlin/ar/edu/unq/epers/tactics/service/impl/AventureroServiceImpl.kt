package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.AventureroService
import ar.edu.unq.epers.tactics.service.runner.TransactionRunner.runTrx

class AventureroServiceImpl(val aventureroDAO: AventureroDAO, val partyDAO: PartyDAO): AventureroService {

    override fun actualizar(aventurero: Aventurero): Aventurero {
        runTrx { aventureroDAO.actualizar(aventurero) }
        return aventurero
    }

    override fun recuperar(idDelAventurero: Long): Aventurero {
        return runTrx { aventureroDAO.recuperar(idDelAventurero) }
    }

    override fun eliminar(aventurero: Aventurero) {
        runTrx {
            if (aventurero.party != null) {
                aventurero.party!!.quitarAventurero(aventurero)
            }
            aventureroDAO.eliminar(aventurero)
        }
    }

}