package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO
import ar.edu.unq.epers.tactics.service.PeleasPaginadas
import ar.edu.unq.epers.tactics.service.runner.HibernateTransaction

class HibernatePeleaDAO: HibernateDAO<Pelea>(Pelea::class.java), PeleaDAO {

    override fun crear(pelea: Pelea): Pelea {
        guardar(pelea)
        return pelea
    }

    override fun actualizar(pelea: Pelea) {
        val session = HibernateTransaction.currentSession
        session.update(pelea)
    }

    override fun recuperar(idDeLaPelea: Long): Pelea {
        return super.recuperar(idDeLaPelea)
    }

    override fun recuperarOrdenadas(partyId: Int, pagina: Int?): PeleasPaginadas {
        val totalPeleas = totalPeleas()
        val listaPaginada = listaDePeleasPaginadas(partyId, pagina)
        return PeleasPaginadas(listaPaginada, totalPeleas.toInt())
    }

    private fun totalPeleas(): Long{
        val session1 = HibernateTransaction.currentSession
        val peleasQuery = "Select count (1) from Party"
        val countQuery = session1.createQuery(peleasQuery)
        return countQuery.uniqueResult() as Long
    }

    private fun listaDePeleasPaginadas(partyId: Int, pagina:Int?): List<Pelea>{
        val session = HibernateTransaction.currentSession
        val hql = "from Pelea p where p.partyA.id = :idParametro or p.partyB.id = :idParametro order by p.id desc"

        val query = session.createQuery(hql, Pelea::class.java)
        query.setParameter("idParametro", partyId.toLong())

        if (pagina != null) {
            query.setFirstResult(pagina * 10)
        }
        query.setMaxResults(10)
        return query.resultList
    }

}