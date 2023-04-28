package ar.edu.unq.epers.tactics.persistencia.dao.mongodb

import ar.edu.unq.epers.tactics.persistencia.dao.DataDAO

class DataMongoDAO(val formacionDAO: FormacionDAO): DataDAO {
    override fun clear() {
        formacionDAO.deleteAll()
    }

}