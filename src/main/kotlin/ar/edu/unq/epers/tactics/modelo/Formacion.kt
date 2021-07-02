package ar.edu.unq.epers.tactics.modelo

import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId

class Formacion {


    var id: ObjectId? = null
    var nombre: String? = null
    var atributos: MutableList<AtributoDeFormacion> = mutableListOf()
    var requisitos: MutableList<Requisito> = mutableListOf()


    protected constructor() {}

    constructor(nombre: String, requisitos:List<Requisito> , atributos: List<AtributoDeFormacion>) {
        this.nombre = nombre
        this.atributos = atributos.toMutableList()
        this.requisitos = requisitos.toMutableList()
    }

    override fun equals(other:Any?) : Boolean{
        var result = false
        when(other){
            is Formacion -> {
                result = this.nombre == other.nombre && compararRequisitos(other.requisitos)
                        && compararAtributos(other.atributos)
            }
        }
        return result
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    private fun compararRequisitos(reqs:MutableList<Requisito>) : Boolean{
        return this.requisitos.containsAll(reqs)
    }

    private fun compararAtributos(atrs:MutableList<AtributoDeFormacion>) : Boolean{
        return this.atributos.containsAll(atrs)
    }

    fun setearIdFormacion(id:ObjectId){
        this.id = id
    }
}