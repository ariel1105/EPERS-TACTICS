package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.service.runner.HibernateTransaction

open class HibernateDAO<T>(private val entityType: Class<T>) {

    open fun guardar(entity: T) {
        val session = HibernateTransaction.currentSession
        session.save(entity)
    }

    fun recuperar(id: Long?): T {
        val session = HibernateTransaction.currentSession
        return session.get(entityType, id)
    }
}