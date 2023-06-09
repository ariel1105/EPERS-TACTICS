package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas

interface PartyDAO {
    fun crear(party: Party) : Party
    fun actualizar(party: Party)
    fun recuperar(idDeLaParty: Long): Party
    fun recuperarTodas(): List<Party>
    fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina: Int?) : PartyPaginadas
}