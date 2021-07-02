package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.service.AventureroLeaderboardService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx

class AventureroLeaderboardServiceImpl(val aventureroDAO: AventureroDAO) : AventureroLeaderboardService {

    override fun mejorGuerrero(): Aventurero {
        return runTrx{
            aventureroDAO.mejorGuerrero()
        }
    }

    override fun mejorMago(): Aventurero {
        return runTrx{
         aventureroDAO.mejorMago()
        }
    }

    override fun mejorCurandero(): Aventurero {
        return runTrx {
            aventureroDAO.mejorCurandero()
           
        }
    }

    override fun buda(): Aventurero {
        
        return runTrx{
            aventureroDAO.buda()
        }
    }
}