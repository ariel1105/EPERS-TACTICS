package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransaction

class HibernateAventureroDAO: AventureroDAO, HibernateDAO<Aventurero>(Aventurero::class.java) {

    override fun actualizar(aventurero: Aventurero) {
        val session = HibernateTransaction.currentSession
        session.update(aventurero)
    }

    override fun recuperar(idDelAventurero: Long): Aventurero {
        return super.recuperar(idDelAventurero)
    }

    override fun eliminar(aventurero: Aventurero) {
        val session = HibernateTransaction.currentSession
        session.delete(aventurero)
    }

    override fun aventureroHabilidadConMasPuntos (tipo: String): Aventurero {
        val session = HibernateTransaction.currentSession

        val hql = "SELECT emisor.id FROM Habilidad WHERE DTYPE = :tipo GROUP BY emisor.id ORDER BY SUM(puntosAplicadosPorHabilidad) DESC"

        val query = session.createQuery(hql)
        query.setParameter("tipo", tipo)

        val aventureroId: Long

        try {
            aventureroId = query.list()[0] as Long
        } catch (e: Exception){
            throw Exception("No hay un aventurero que cumpla los requisitos")
        }
        return recuperar(aventureroId)
    }
}