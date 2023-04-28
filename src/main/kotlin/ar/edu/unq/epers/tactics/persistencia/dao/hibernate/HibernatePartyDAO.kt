package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.exceptions.NoPageFoundException
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas
import ar.edu.unq.epers.tactics.service.runner.HibernateTransaction

class HibernatePartyDAO: HibernateDAO<Party>(Party::class.java),PartyDAO {

    override fun crear(party: Party): Party {
        guardar(party)
        return party
    }

    override fun actualizar(party: Party) {
        val session = HibernateTransaction.currentSession
        session.update(party)
    }

    override fun recuperar(idDeLaParty: Long): Party {
        return super.recuperar(idDeLaParty)
    }

    override fun recuperarTodas(): List<Party> {
        val session = HibernateTransaction.currentSession
        val hql = """
                    from Party p
                    order by p.nombre asc
        """

        val query = session.createQuery(hql, Party::class.java)

        return query.resultList
    }

    override fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina: Int?): PartyPaginadas {
        //Switcheo direccion solicitada.
        var dir = "DESC"
        if (direccion == Direccion.ASCENDENTE){
            dir = "ASC"
        }

        val totalParties = obtenerTotalDeParties()

        //traigo la página solicitada
        val session = HibernateTransaction.currentSession
        val hql = "from Party order by "+orden.toString()+" "+dir

        val query = session.createQuery(hql, Party::class.java)

        if (pagina != null) { query.setFirstResult(pagina*10) }
        query.setMaxResults(10)

        val resultPartyPaginada = PartyPaginadas(query.resultList,totalParties.toInt())

        if (totalParties.toInt()/10 < pagina!!){
            throw NoPageFoundException("La página solicitada no está disponible en el resultado")
        }

        return resultPartyPaginada
    }

    fun obtenerTotalDeParties():Long {
        val session1 = HibernateTransaction.currentSession
        val partiesQuery = "Select count (1) from Party"
        val countQuery = session1.createQuery(partiesQuery)
        return countQuery.uniqueResult() as Long
    }

}