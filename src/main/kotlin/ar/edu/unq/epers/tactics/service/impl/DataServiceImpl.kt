package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.persistencia.dao.DataDAO
import ar.edu.unq.epers.tactics.service.DataService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx

class DataServiceImpl(val dataDao:DataDAO): DataService {

    override fun clear() {
       runTrx{dataDao.clear()}
    }
}