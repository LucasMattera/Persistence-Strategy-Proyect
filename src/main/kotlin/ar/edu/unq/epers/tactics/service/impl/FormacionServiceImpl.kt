package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.modelo.Requisito
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.FormacionService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx

class FormacionServiceImpl(val formacionDAO: FormacionDAO,val partyDAO: PartyDAO):FormacionService {


    override fun crearFormacion(nombreFormacion: String,requerimientos:
                                List<Requisito>,stats: List<AtributoDeFormacion>): Formacion {

        return formacionDAO.crearFormacion(nombreFormacion,requerimientos,stats)
    }

    override fun todasLasFormaciones(): List<Formacion> {
        return formacionDAO.todasLasFormaciones()
    }

    override fun atributosQueCorresponden(partyId: Long): List<AtributoDeFormacion> {
        return runTrx {
            var party = partyDAO.recuperar(partyId)
            return@runTrx formacionDAO.atributosQueCorresponden(party)
        }

    }

    override fun formacionesQuePosee(partyId: Long): List<Formacion> {
        return runTrx{
            var party = partyDAO.recuperar(partyId)

            return@runTrx formacionDAO.formacionesQuePosee(party)

        }
    }

    override fun getBy(property: String, value: String?) : Formacion{
        return formacionDAO.getBy(property,value)
    }

}