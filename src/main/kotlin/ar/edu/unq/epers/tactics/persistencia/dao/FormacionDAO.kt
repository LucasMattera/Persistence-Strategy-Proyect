package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.persistencia.dao.mongo.GenericMongoDAO

interface FormacionDAO {

    fun crearFormacion(nombreFormacion: String, requerimientos:List<Requisito>, stats: List<AtributoDeFormacion>): Formacion
    fun deleteAll()
    fun getBy(property:String,value : String?) : Formacion
    fun todasLasFormaciones() : List<Formacion>
    fun formacionesQuePosee(party: Party): List<Formacion>
    fun atributosQueCorresponden(party : Party): List<AtributoDeFormacion>
    fun actualizarParty(party:Party) : Party
    fun agregarANuevoAventureroAtributosviejosYActualizarParty(party: Party,aventureroNuevo: Aventurero):Party
}