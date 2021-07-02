package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner


open class HibernateDAO<T>(private val entityType: Class<T>) {

    open fun crear(entity: T): T{
        val session = HibernateTransactionRunner.currentSession
        session.save(entity)
        return entity
    }

     fun recuperar(id: Long): T {
        val session = HibernateTransactionRunner.currentSession
        return session.get(entityType, id)
    }

    fun actualizar(entity: T){
        val session=HibernateTransactionRunner.currentSession
        session.update(entity)
    }
}