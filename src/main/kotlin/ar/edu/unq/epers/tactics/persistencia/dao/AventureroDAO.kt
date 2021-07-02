package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.Aventurero

interface AventureroDAO {
    fun actualizar(aventurero: Aventurero)
    fun recuperar(idDelAventurero: Long): Aventurero
    fun eliminar(aventurero: Aventurero) // se vuela
    fun mejorCurandero(): Aventurero
    fun mejorGuerrero(): Aventurero
    fun mejorMago():Aventurero
    fun buda():Aventurero
}