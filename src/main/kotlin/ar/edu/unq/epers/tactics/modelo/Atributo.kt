package ar.edu.unq.epers.tactics.modelo

enum class Atributo {
    FUERZA{
        override fun aplicarMejora(aventurero: Aventurero, cantidadAMejorar: Int) {
            aventurero.mejorarFuerza(cantidadAMejorar)
        }

    },
    DESTREZA{
        override fun aplicarMejora(aventurero: Aventurero, cantidadAMejorar: Int) {
            aventurero.mejorarDestreza(cantidadAMejorar)
        }

    },
    CONSTITUCION{
        override fun aplicarMejora(aventurero: Aventurero, cantidadAMejorar: Int) {
            aventurero.mejorarConstitucion(cantidadAMejorar)
        }

    },
    INTELIGENCIA{
        override fun aplicarMejora(aventurero: Aventurero, cantidadAMejorar: Int) {
            aventurero.mejorarInteligencia(cantidadAMejorar)
        }

    };

    abstract fun aplicarMejora(aventurero:Aventurero,cantidadAMejorar:Int)
}