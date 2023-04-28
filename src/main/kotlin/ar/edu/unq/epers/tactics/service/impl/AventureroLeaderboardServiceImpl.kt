package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.service.AventureroLeaderboardService
import ar.edu.unq.epers.tactics.service.runner.TransactionRunner.runTrx

class AventureroLeaderboardServiceImpl(val aventureroDAO: AventureroDAO) : AventureroLeaderboardService {

    override fun mejorGuerrero(): Aventurero {
        return runTrx {
            aventureroDAO.aventureroHabilidadConMasPuntos("Ataque")
        }
    }

    override fun mejorMago(): Aventurero {
        return runTrx {
            aventureroDAO.aventureroHabilidadConMasPuntos("AtaqueConMagia")
        }
    }

    override fun mejorCurandero(): Aventurero {
        return runTrx {
            aventureroDAO.aventureroHabilidadConMasPuntos("Curar")
        }
    }

    override fun buda(): Aventurero {
        return runTrx {
            aventureroDAO.aventureroHabilidadConMasPuntos("Meditacion")
        }
    }
}