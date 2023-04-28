package ar.edu.unq.epers.tactics.persistencia.dao.jdbc
/*
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.jdbc.JDBCConnector.execute
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

class JDBCPartyDAO : PartyDAO {

    override fun crear(party: Party): Party {
        return execute { conn: Connection ->
            var returnID : Long = 0
            val ps =
                conn.prepareStatement("INSERT INTO party (nombre, cantidadDeAventureros) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS)
            ps.setString(1, party.nombre)
            ps.setInt(2, party.numeroDeAventureros)


            ps.execute()

            val keyRS : ResultSet = ps.generatedKeys
            while (keyRS.next()){
                returnID = keyRS.getLong(1)
            }



            if (ps.updateCount != 1) {
                throw RuntimeException("No se inserto el personaje " + party.nombre)
            }
            ps.close()
            party
        }
    }

    override fun actualizar(party: Party) {
        execute { conn: Connection ->
            val ps =
                conn.prepareStatement("UPDATE party " +
                        "SET nombre = ?," +
                        "cantidadDeAventureros = ? " +
                        "WHERE id = ?")
            ps.setString(1, party.nombre)
            ps.setInt(2, party.numeroDeAventureros)
            ps.setLong(3, party.id!!)


            ps.execute()
            if (ps.updateCount != 1) {
                throw RuntimeException("No se actualizó el personaje " + party.nombre)
            }
            ps.close()
        }
    }

    override fun recuperar(idDeLaParty: Long): Party {
        return execute { conn: Connection ->
            val ps = conn.prepareStatement("SELECT nombre, id, cantidadDeAventureros FROM party WHERE id = ?")
            ps.setLong(1, idDeLaParty)
            val resultSet = ps.executeQuery()
            var party: Party? = null
            while (resultSet.next()) {
                if (party != null) {
                    throw RuntimeException("Existe mas de un party con el nombre ${resultSet.getString("nombre")}")
                }
                party = Party(resultSet.getString("nombre"))
                party.id = resultSet.getLong("id")
                //party.numeroDeAventureros = resultSet.getInt("cantidadDeAventureros")
            }
            ps.close()
            party!!
        }
    }

    override fun recuperarTodas(): List<Party> {
        return execute { conn: Connection ->
            val ps = conn.prepareStatement("SELECT nombre, id, cantidadDeAventureros FROM party ORDER BY nombre ASC")
            val listaDeParties : MutableList<Party> = mutableListOf()
            val resultSet = ps.executeQuery()

            while (resultSet.next()) {
                val party = Party(resultSet.getString("nombre"))
                party.id = resultSet.getLong("id")
                //party.numeroDeAventureros = resultSet.getInt("cantidadDeAventureros")
                listaDeParties.add(party)
            }
            ps.close()
            listaDeParties
        }
    }

    override fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina: Int?): PartyPaginadas {
        TODO("Not yet implemented")
    }

}*/