package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.*

interface ClaseService {

    fun crearClase(nombreDeClase:String)
    fun crearMejora(nombreDeClase: String, nombreDeClase2: String, atributos: List<Atributo>, cantidadDeAtributos: Int)
    fun requerir(nombreDeClase: String,nombreDeClase2: String)
    fun puedeMejorar(aventureroId:Long,mejora: Mejora):Boolean
    fun ganarProficiencia(aventureroId: Long,nombreDeClase: String,nombreDeClase2: String):Aventurero
    fun posiblesMejoras(aventureroId: Long):Set<Mejora>
    fun caminoMasRentable(puntosDeExperiencia: Int, aventureroId: Long, atributo:Atributo): List<Mejora>
    fun getClases():List<Clase>
    fun getRequerirDe(nombreDeClase: String):List<Clase>
    fun getMejoras(): List<Mejora>
    fun getMejoraDe(nombreDeClase: String,nombreDeClase2: String):Mejora

}