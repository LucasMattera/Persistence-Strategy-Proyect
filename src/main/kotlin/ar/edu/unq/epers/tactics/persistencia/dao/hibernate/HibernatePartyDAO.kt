package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import javax.management.Query

open class HibernatePartyDAO: HibernateDAO<Party>(Party::class.java), PartyDAO {


    override fun recuperarTodas(): List<Party> {
        val session = HibernateTransactionRunner.currentSession
        val hql = "from Party order by numeroDeAventureros asc"

        val query = session.createQuery(hql,Party::class.java)


        return query.resultList
    }

    override fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina:Int?): PartyPaginadas{
        if(pagina == null || pagina < 1 || (pagina > this.cantidadDePartys() / 10 && pagina > 1)){
            throw Exception("pagina invalida")
        }
        val session = HibernateTransactionRunner.currentSession

        var hql : String = when{
            (orden == Orden.VICTORIAS)-> recuperarOrdenadasVictoria(direccion)
            (orden == Orden.DERROTAS) -> recuperarOrdenadasDerrota(direccion)
            (orden == Orden.PODER)-> recuperarOrdenasPoder(direccion)

            else-> throw Exception("entrada invalida")

        }

        val query = session.createQuery(hql,Party::class.java).setCacheable(true)
        val paginasASaltar = (pagina?.minus(1))?.times(10)
        query.firstResult = paginasASaltar!!
        //query.maxResults = 10


        return PartyPaginadas(query.resultList,this.cantidadDePartys().toInt())
    }


    private fun recuperarOrdenadasVictoria(direccion: Direccion):String{

        return ("select p " +
                "from Party p join Pelea peleas " +
                "on p.id = peleas.party.id " +
                "where peleas.ganoPelea = True " +
                "group by p.id " +
                "order by count(p.id) ${direccion.direccionString()}")

    }

    private fun recuperarOrdenadasDerrota(direccion: Direccion):String{

        return ("select p " +
                "from Party p join Pelea peleas " +
                "on p.id = peleas.party.id " +
                "where peleas.ganoPelea = False " +
                "group by p.id " +
                "order by count(p.id) ${direccion.direccionString()}")

    }

    private fun recuperarOrdenasPoder(direccion: Direccion):String {

        return "select p " +
                "from Party p " +
                "join p.aventureros av " +
                "join av.atributos a " +
                "group by p.id " +
                "order by (sum(av.nivel)+sum(a.fuerza)+ sum(a.destreza)) + (sum(av.nivel)+sum(a.fuerza)+ (sum(a.destreza)/2)) + (sum(av.nivel)+sum(a.inteligencia)) ${direccion.direccionString()}"
    }
    override fun cantidadDePartys(): Long {
        val session = HibernateTransactionRunner.currentSession
        val hql = "select COUNT(nombre) from Party "

        val query = session.createQuery(hql).setCacheable(true)
        return query.singleResult as Long
    }

}