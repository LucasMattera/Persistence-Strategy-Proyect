package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.modelo.exception.CantImprove

class Mejora (var nombreClaseAMejorar:String,var nombreClaseMejora:String,var atributosAMejorar : MutableList<Atributo> = mutableListOf(),var cantidad:Int){

    fun agregarAtributoAMejorar(atributo:Atributo){
        atributosAMejorar.add(atributo)
    }

    fun aplicar(aventurero : Aventurero,requisitos : List<Clase>,nombreClase:String){
        var requerimientos = requisitos.map{ clase -> clase.nombreClase}
        requerimientos+=nombreClase
        if (aventurero.puedeMejorar(requerimientos)){
            atributosAMejorar.forEach { atributo -> atributo.aplicarMejora(aventurero,cantidad) }
            aventurero.aplicarMejora(Clase(nombreClaseMejora))
        }
        else{
            throw CantImprove(aventurero.nombre)
        }
    }

    override fun equals(other:Any?) : Boolean{
        var result = false
        when(other){
            is Mejora -> {
                result = this.nombreClaseAMejorar == other.nombreClaseAMejorar &&
                        this.nombreClaseMejora == other.nombreClaseMejora &&
                        this.cantidad == other.cantidad
            }
        }
        return result
    }

}