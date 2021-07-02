package ar.edu.unq.epers.tactics.modelo.exception

class IdNotAvailable(val id:Long? ) : RuntimeException() {
    override val message: String?
        get() = "El id $id no esta disponible"
}

class NameNotAvailable(val name:String?) : RuntimeException() {
    override val message: String?
        get() = "El nombre $name no esta disponible"
}

class IsFighting(val name:Long?) : RuntimeException() {
    override val message: String?
        get() = "La party $name ya esta en una pelea"
}

class CantImprove(val name:String?) : RuntimeException(){
    override val message: String?
        get() = "El aventurero $name no cumple requisitos"
}