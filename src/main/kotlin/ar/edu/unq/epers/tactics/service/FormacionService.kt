package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.modelo.Requisito

interface FormacionService {

    fun crearFormacion(nombreFormacion:String, requerimientos:List<Requisito>,stats:List<AtributoDeFormacion>):Formacion
    fun todasLasFormaciones():List<Formacion>
    fun atributosQueCorresponden(partyId:Long):List<AtributoDeFormacion>
    fun formacionesQuePosee(partyId:Long): List<Formacion>
    fun getBy(property : String, value : String?) : Formacion
}