package ar.edu.unq.epers.tactics.service.runner

interface Transaction {
    fun start()
    fun commit()
    fun rollback()
}