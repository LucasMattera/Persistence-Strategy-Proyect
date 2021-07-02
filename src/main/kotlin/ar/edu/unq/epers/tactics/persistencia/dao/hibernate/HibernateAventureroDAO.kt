package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner

class HibernateAventureroDAO:HibernateDAO<Aventurero>(Aventurero::class.java),AventureroDAO {


    override fun eliminar(aventurero: Aventurero) {
        val session = HibernateTransactionRunner.currentSession
        session.delete(aventurero)
    }


    override fun mejorCurandero(): Aventurero {
        val session = HibernateTransactionRunner.currentSession
        val hql = "from Aventurero order by curacionRealizada desc"

        val query = session.createQuery(hql, Aventurero::class.java)
        query.maxResults = 1
        return query.singleResult
    }


    override fun mejorGuerrero() : Aventurero {
        val session = HibernateTransactionRunner.currentSession
        val hql = "from Aventurero order by dañoFisicoRealizado desc"
        val query = session.createQuery(hql,Aventurero::class.java)
        query.maxResults = 1
        return query.singleResult
    }

    override fun mejorMago(): Aventurero {
        val session = HibernateTransactionRunner.currentSession
        val hql = "from Aventurero order by dañoMagicoTotalRealizado desc"

        val query = session.createQuery(hql, Aventurero::class.java)
        query.maxResults = 1
        return query.singleResult
    }

    override fun buda(): Aventurero {
        val session = HibernateTransactionRunner.currentSession
        val hql = "from Aventurero order by meditacionesTotales desc"
        val query = session.createQuery(hql, Aventurero::class.java)

        query.maxResults = 1


        return query.singleResult
    }
}