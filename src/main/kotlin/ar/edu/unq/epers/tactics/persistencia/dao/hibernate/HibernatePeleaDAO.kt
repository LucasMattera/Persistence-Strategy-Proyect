package ar.edu.unq.epers.tactics.persistencia.dao.hibernate


import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO
import ar.edu.unq.epers.tactics.service.PartyPaginadas
import ar.edu.unq.epers.tactics.service.PeleasPaginadas
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner

class HibernatePeleaDAO: HibernateDAO<Pelea>(Pelea::class.java), PeleaDAO{

    override fun recuperarOrdenadas(partyId: Long, pagina: Int?): PeleasPaginadas {

        val session = HibernateTransactionRunner.currentSession

        val hqlT = "from Pelea where party.id = :id"
        val totales = session.createQuery(hqlT, Pelea::class.java)
        totales.setParameter("id",partyId)

        if(pagina == null || pagina < 1 || (pagina > totales.resultList.size / 10 && pagina > 1)){
            throw Exception("pagina invalida")
        }

        val hql = "from Pelea where party.id = :id order by date desc "


        val query = session.createQuery(hql, Pelea::class.java)
        val paginasASaltar = (pagina?.minus(1))?.times(10)
        query.setParameter("id",partyId)
        query.firstResult=paginasASaltar!!
        query.maxResults=10


        return PeleasPaginadas(query.resultList, totales.resultList.size)
    }
}