package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.modelo.exception.CantImprove
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.service.ClaseService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx

class ClaseServiceImpl(val claseDAO:ClaseDAO, val aventureroDAO:AventureroDAO, val formacionDAO: FormacionDAO): ClaseService {

    override fun crearClase(nombreDeClase: String) {
       runTrx { claseDAO.crearClase(nombreDeClase) }
    }

    override fun getClases(): List<Clase> {
        return runTrx { claseDAO.getClases() }
    }

    override fun crearMejora(nombreDeClase: String, nombreDeClase2: String, atributos: List<Atributo>, cantidadDeAtributos: Int) {
        return runTrx {claseDAO.crearMejora(nombreDeClase,nombreDeClase2,atributos,cantidadDeAtributos)}
    }

    override fun getMejoraDe(nombreDeClase: String,nombreDeClase2: String): Mejora{
        return runTrx { claseDAO.getMejoraDe(nombreDeClase,nombreDeClase2) }
    }

    override fun requerir(nombreDeClase: String, nombreDeClase2: String) {
        runTrx { claseDAO.requerir(nombreDeClase,nombreDeClase2) }
    }

    override fun getRequerirDe(nombreDeClase: String): List<Clase> {
        return runTrx{claseDAO.getRequerirDe(nombreDeClase)}
    }

    override fun puedeMejorar(aventureroId: Long, mejora: Mejora): Boolean {

        return runTrx{
             var aventurero = aventureroDAO.recuperar(aventureroId)
             var clasesRequeridas = getRequerirDe(mejora.nombreClaseMejora)
             var nombresDeClasesRequeridas = clasesRequeridas.map { c -> c.nombreClase }
             nombresDeClasesRequeridas+= mejora.nombreClaseAMejorar
             var puedeMejorar : Boolean = aventurero.puedeMejorar(nombresDeClasesRequeridas)
             return@runTrx puedeMejorar
         }
    }

    override fun ganarProficiencia(aventureroId: Long, nombreDeClase: String, nombreDeClase2: String): Aventurero {
        var mejora = getMejoraDe(nombreDeClase,nombreDeClase2)
        var requisitos = getRequerirDe(nombreDeClase2)
        return runTrx {
            var aventurero = aventureroDAO.recuperar(aventureroId)
            mejora.aplicar(aventurero,requisitos,nombreDeClase)
            aventureroDAO.actualizar(aventurero)
            formacionDAO.actualizarParty(aventurero.party!!)
            return@runTrx aventurero
        }
    }

    override fun posiblesMejoras(aventureroId: Long): Set<Mejora> {
        return runTrx {
            val aventurero = aventureroDAO.recuperar(aventureroId)
            val clasesDeAventurero = aventurero.proficiencias.map { clase -> clase.nombreClase }
            return@runTrx claseDAO.posiblesMejoras(clasesDeAventurero)
        }
    }

    override fun getMejoras(): List<Mejora> {
        return claseDAO.getMejoras()
    }

    override fun caminoMasRentable(puntosDeExperiencia: Int, aventureroId: Long, atributo: Atributo): List<Mejora> {
        return runTrx {
            val aventurero = aventureroDAO.recuperar(aventureroId)
            val clasesAventurero = aventurero.proficiencias.map { clase -> clase.nombreClase }

            return@runTrx claseDAO.caminoMasRentable(puntosDeExperiencia,clasesAventurero,atributo.name)

        }
    }


}