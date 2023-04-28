package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO
import ar.edu.unq.epers.tactics.service.PeleaService
import ar.edu.unq.epers.tactics.service.PeleasPaginadas
import ar.edu.unq.epers.tactics.service.runner.TransactionRunner.runTrx


class PeleaServiceImpl(val peleaDAO: PeleaDAO, val partyDAO: PartyDAO, val aventureroDAO: AventureroDAO): PeleaService {

    override fun iniciarPelea(idPartyA: Long, idPartyB: Long): Pelea {
        return runTrx {
            //busco a las partys involucradas
            val partyA: Party = partyDAO.recuperar(idPartyA)
            val partyB: Party = partyDAO.recuperar(idPartyB)
            //creo la pelea
            val pelea = Pelea(partyA, partyB)
            peleaDAO.crear(pelea)
            //relaciono cada party a la pelea
            partyA.incluirAPelea(pelea)
            partyB.incluirAPelea(pelea)
            //actualizo la db de las partys
            partyDAO.actualizar(partyA)
            partyDAO.actualizar(partyB)
            pelea
        }
    }

    override fun estaEnPelea(partyId: Double): Boolean {
        return runTrx {
            val party: Party = partyDAO.recuperar(partyId.toLong())
            party.pelea != null
        }
    }

    override fun resolverTurno(idPelea:Long, idAventurero:Long): Habilidad {
        return runTrx {
            val aventurero: Aventurero = aventureroDAO.recuperar(idAventurero)
            val habilidadResultante = aventurero.resolverTurno()

            aventurero.party!!.pelea!!.agregarHabilidad(habilidadResultante)

            peleaDAO.actualizar(habilidadResultante.peleaEnQueSeUtiliza)

            habilidadResultante
        }
    }

    override fun recibirHabilidad(idPelea:Long, idAventurero:Long, habilidad: Habilidad): Aventurero {
        return runTrx {
            habilidad.ejecutar()
            aventureroDAO.actualizar(habilidad.receptor)
            aventureroDAO.actualizar(habilidad.emisor)
            peleaDAO.actualizar(habilidad.peleaEnQueSeUtiliza)
            habilidad.receptor
        }
    }

    override fun terminarPelea(idDeLaPelea: Long): Pelea {
        return runTrx {
            val pelea: Pelea = peleaDAO.recuperar(idDeLaPelea)
            val partyA: Party = partyDAO.recuperar(pelea.partyA!!.id!!)
            val partyB: Party = partyDAO.recuperar(pelea.partyB!!.id!!)

            //Defino la party ganadora
            if (partyA.aventureros.any {!it.nockeado}) {
                pelea.partyGanadora = partyA
                partyA.ganarPelea()
                partyB.perderPelea()
            } else {
                pelea.partyGanadora = partyB
                partyB.ganarPelea()
                partyA.perderPelea()
            }

            //esto termina la pelea
            partyA.terminarPelea()
            partyB.terminarPelea()

            resetearAventureros(partyA.aventureros)
            resetearAventureros(partyB.aventureros)

            partyDAO.actualizar(partyA)
            partyDAO.actualizar(partyB)
            peleaDAO.actualizar(pelea)
            pelea
        }

    }

    override fun recuperarOrdenadas(partyId: Long, pagina: Int?): PeleasPaginadas {
        return runTrx {
            peleaDAO.recuperarOrdenadas(partyId.toInt(), pagina)
        }
    }

    private fun resetearAventureros(aventureros: MutableSet<Aventurero>) {
        for (aventurero: Aventurero in aventureros) {
            aventurero.resetearValores()
        }
    }

}