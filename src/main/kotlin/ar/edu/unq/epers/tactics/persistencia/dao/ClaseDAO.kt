package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.*

interface ClaseDAO {

    fun crearClase(nombreDeClase:String)
    fun crearMejora(nombreDeClase: String, nombreDeClase2: String, atributos: List<Atributo>, cantidadDeAtributos: Int)
    fun requerir(nombreDeClase: String,nombreDeClase2: String)
    fun ganarProficiencia(aventureroId: Long,nombreDeClase: String,nombreDeClase2: String): Aventurero
    fun posiblesMejoras(clases: List<String>):Set<Mejora>
    fun caminoMasRentable(puntosDeExperiencia: Int,clasesDeAventurero:List<String>, atributo: String): List<Mejora>

    fun getClases(): List<Clase>
    fun getRequerirDe(nombreDeClase: String):List<Clase>

    fun getMejoras(): List<Mejora>
    fun getMejoraDe(nombreDeClase: String,nombreDeClase2: String):Mejora

}