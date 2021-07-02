package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Habilidad
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.modelo.exception.IsFighting
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO
import ar.edu.unq.epers.tactics.service.PeleaService
import ar.edu.unq.epers.tactics.service.PeleasPaginadas
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx


class PeleaServiceImpl(val peleaDAO: PeleaDAO,
                       val partyDAO: PartyDAO,
                       val aventureroDAO: AventureroDAO): PeleaService {


    override fun iniciarPelea(idDeLaParty: Long,partyEnemiga:String): Pelea {
        var pelea : Pelea? = null
        var party : Party? = null
        if (estaEnPelea(idDeLaParty)) {
            throw IsFighting(idDeLaParty)
        }
        runTrx {
            party = partyDAO.recuperar(idDeLaParty)
            pelea = peleaDAO.crear(Pelea(party!!,partyEnemiga))
            party!!.sePoneAPelear()
            partyDAO.actualizar(party!!)
            }

        return pelea!!
    }

    fun actualizar(pelea: Pelea): Pelea {
        runTrx{peleaDAO.actualizar(pelea)}
        return pelea
    }

    fun recuperar(idDeLaPelea: Long): Pelea {
        return runTrx { peleaDAO.recuperar(idDeLaPelea) }
    }

    override fun estaEnPelea(partyId: Long): Boolean {
        return runTrx {
            var party = partyDAO.recuperar(partyId)
            return@runTrx party.estaEnPelea()
        }
    }
 

    override fun resolverTurno(peleaId: Long,
                               aventureroId: Long,
                               enemigos: List<Aventurero>): Habilidad {

        return runTrx {
            val emisor = aventureroDAO.recuperar(aventureroId)
            val pelea = peleaDAO.recuperar(peleaId)

            val habilidad = emisor.generarHabilidad(enemigos)
            habilidad.actualizarEmisor(emisor)
            pelea.guardarHabilidadRealizada(habilidad)
            peleaDAO.actualizar(pelea)
            aventureroDAO.actualizar(emisor)


            return@runTrx habilidad
        }

    }

    override fun recibirHabilidad(idPelea:Long,
                                  aventureroId: Long,
                                  habilidadId: Habilidad): Aventurero {
       runTrx {
           val pelea = peleaDAO.recuperar(idPelea)
           habilidadId.actualizarObjetivo(aventureroDAO.recuperar(aventureroId))
           habilidadId.realizarAccion()
           pelea.guardarHabilidadRecibida(habilidadId)
           aventureroDAO.actualizar(habilidadId.objetivo)

         }
        return habilidadId.objetivo
    }

    override fun terminarPelea(idDeLaPelea: Long): Pelea {

        return runTrx {
            val pelea = peleaDAO.recuperar(idDeLaPelea)
            val party = partyDAO.recuperar(pelea.party.id!!)

            pelea.setearSiGanoPelea()
            party.terminarPelea(pelea)
            party.resetearAventureros()
            partyDAO.actualizar(party)
            return@runTrx pelea
        }

    }

    override fun recuperarOrdenadas(partyId: Long, pagina: Int?): PeleasPaginadas {
        return  runTrx { peleaDAO.recuperarOrdenadas(partyId,pagina) }
    }
}

