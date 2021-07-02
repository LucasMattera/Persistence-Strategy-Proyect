package ar.edu.unq.epers.tactics.modelo

class Requisito {
    var nombreClase: String? = null
    var cantidad: Int? = null

    protected constructor() {}

    constructor(nombreClase:String,cantidad:Int) {
        this.nombreClase = nombreClase
        this.cantidad = cantidad

    }

    override fun equals(other:Any?) : Boolean{
        var result = false
        when(other){
            is Requisito -> {
                result = this.nombreClase == other.nombreClase &&
                        this.cantidad == other.cantidad
            }
        }
        return result
    }
}