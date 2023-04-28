package ar.edu.unq.epers.tactics.service.runner

import org.hibernate.Session

object HibernateTransaction: Transaction {
    private var session: Session? = null
    private var transaction: org.hibernate.Transaction? = null

    val currentSession: Session
        get() {
            if (session == null) {
                TransactionRunner.addTransaction(HibernateTransaction)
                start()
            }
            return session!!
        }

    override fun start() {
        session = HibernateSessionFactoryProvider.instance.createSession()
        transaction =  session!!.beginTransaction()
    }

    override fun commit() {
        transaction?.commit()
        close()
    }

    override fun rollback() {
        transaction?.rollback()
        close()
    }

    private fun close(){
        session?.close()
        session = null
        transaction = null
    }

}