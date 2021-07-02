package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas
import ar.edu.unq.epers.tactics.persistencia.dao.redis.RedisPartyDAO
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx

class PartyServiceImpl(val partyDAO: PartyDAO, val formacionDAO : FormacionDAO) : PartyService {

    private val redisPartyDAO = RedisPartyDAO()

    override fun crear(party: Party): Party {
        return runTrx {partyDAO.crear(party)}
    }

    override fun actualizar(party: Party): Party {
        runTrx {partyDAO.actualizar(party)}
        return party
    }

    override fun recuperar(idDeLaParty: Long): Party {

        return runTrx {
            if(redisPartyDAO.existsParty(idDeLaParty)){
                return@runTrx redisPartyDAO.getParty(idDeLaParty)!!
            }else{
                val party = partyDAO.recuperar(idDeLaParty)
                redisPartyDAO.insertarParty(party)
                return@runTrx party

            }
        }
    }

    override fun recuperarTodas(): List<Party> {
        return runTrx {
            if(redisPartyDAO.estanCacheadasTodasLasPartys()){
                return@runTrx redisPartyDAO.recuperarTodas()
            }else{
                val partys = partyDAO.recuperarTodas()
                redisPartyDAO.insertPartys(partys)
                return@runTrx partys

            }
        }
    }

    override fun agregarAventureroAParty(idDeLaParty: Long, aventurero: Aventurero): Aventurero {
        var party = recuperar(idDeLaParty)
        if(aventurero.party != null || party.numeroDeAventureros >= 5){
            throw Exception("le pifiamos en algo")
        }else {
            party.agregarAventurero(aventurero)
            party = formacionDAO.agregarANuevoAventureroAtributosviejosYActualizarParty(party,aventurero)
            actualizar(party)
            return aventurero
        }
    }

    override fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina: Int?): PartyPaginadas {
        return runTrx {
            if(redisPartyDAO.estaCacheadoPartysOrdenadas(orden,direccion,pagina)){
                return@runTrx redisPartyDAO.recuperarOrdenadas(orden,direccion,pagina)!!
            }else{
                val partysOrdenadas = partyDAO.recuperarOrdenadas(orden,direccion,pagina)
                redisPartyDAO.insertOrdenadas(orden,direccion,pagina,partysOrdenadas)
                return@runTrx partysOrdenadas

            }
        }
    }

}