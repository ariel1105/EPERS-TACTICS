package ar.edu.unq.epers.tactics.service.runner

import org.neo4j.driver.Session

object Neo4JTransaction: Transaction {
    private var sessionThreadLocal: ThreadLocal<Session?> = ThreadLocal()
    private var transaction: ThreadLocal<org.neo4j.driver.Transaction?> = ThreadLocal()

    val currentTransaction: org.neo4j.driver.Transaction
        get() {
            if (transaction.get() == null) {
                TransactionRunner.addTransaction(Neo4JTransaction)
                start()
            }
            return transaction.get()!!
        }

    override fun start() {
        val session = Neo4JSessionFactoryProvider.createSession()
        sessionThreadLocal.set(session)
        transaction.set(session.beginTransaction())
    }

    override fun commit() {
        transaction.get()?.commit()
        close()
    }

    override fun rollback() {
        transaction.get()?.rollback()
        close()
    }

    private fun close(){
        sessionThreadLocal.get()?.close()
        sessionThreadLocal.set(null)
        transaction.set(null)
    }

}