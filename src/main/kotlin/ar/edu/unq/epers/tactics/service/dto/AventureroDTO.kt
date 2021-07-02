package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.*


data class AventureroDTO(var id:Long?,
                         var nivel:Int,
                         var nombre:String,
                         var imagenURL:String,
                         var tacticas: List<TacticaDTO>,
                         var atributos: AtributosDTO,
                         var dañoRecibido:Int){

    companion object {

        fun desdeModelo(aventurero: Aventurero):AventureroDTO{
            val id = aventurero.id
            val nivel = aventurero.nivel
            val nombre = aventurero.nombre
            val imagenURL = aventurero.imagenURL
            val tacticas = aventurero.tacticas.map {  tactica -> tactica.toDTO() }
            val atributos = aventurero.atributosToDTO()
            val dañoRecibido = aventurero.vidaMax-aventurero.vida

            return AventureroDTO(id,nivel,nombre,imagenURL,tacticas,atributos,dañoRecibido)
        }
    }

    fun aModelo():Aventurero{
        val id = this.id
        var nivel = this.nivel
        var fuerza = this.atributos.fuerza
        var destreza = this.atributos.destreza
        var constitucion = this.atributos.constitucion
        var inteligencia = this.atributos.inteligencia
        var nombre = this.nombre
        var imagenURL = this.imagenURL
        var tacticas = this.tacticas.map {  tacticaDTO -> Tactica.toModel(tacticaDTO) }

        var aventurero = Aventurero(nombre,  tacticas, imagenURL, Atributos(fuerza, destreza, constitucion, inteligencia),
        nivel)
        //arreglar
        aventurero.id = id
        return aventurero
    }

    fun actualizarModelo(aventurero: Aventurero){
        aventurero.actualizarDesdeDTO(this)
    }
}

data class AtributosDTO(var id:Long?,
                        var fuerza:Int,
                        var destreza:Int,
                        var constitucion:Int,
                        var inteligencia:Int)
data class TacticaDTO(var id:Long?,
                      var prioridad:Int,
                      var receptor:TipoDeReceptor,
                      var tipoDeEstadistica:TipoDeEstadistica,
                      var criterio:Criterio,
                      var valor:Int,
                      var accion:Accion)

enum class TipoDeReceptor {
    ALIADO,
    ENEMIGO,
    UNO_MISMO
}
enum class TipoDeEstadistica {

    VIDA{
        override fun getValor(aventurero:Aventurero) :Int{
            return aventurero.vida
        }
    },
    ARMADURA {
        override fun getValor(aventurero: Aventurero): Int {
            return aventurero.estadisticas.armadura()
        }
    },
    MANA {
        override fun getValor(aventurero: Aventurero): Int {
            return aventurero.mana
        }
    },
    VELOCIDAD {
        override fun getValor(aventurero: Aventurero): Int {
            return aventurero.estadisticas.velocidad()
        }
    },
    DAÑO_FISICO {
        override fun getValor(aventurero: Aventurero): Int {
            return aventurero.estadisticas.ataqueFisico()
        }
    },
    DAÑO_MAGICO {
        override fun getValor(aventurero: Aventurero): Int {
            return aventurero.estadisticas.poderMagico()
        }
    },
    PRECISION_FISICA {
        override fun getValor(aventurero: Aventurero): Int {
            return aventurero.estadisticas.precisionFisica()
        }
    };

    abstract fun getValor(aventurero:Aventurero): Int
}

enum class Criterio {
    IGUAL {
        override fun cumpleCondicion(estadisticaAventurero: Int, valorCondicion: Int): Boolean {
            return estadisticaAventurero==valorCondicion
        }
    },
    MAYOR_QUE {
        override fun cumpleCondicion(estadisticaAventurero: Int, valorCondicion: Int): Boolean {
            return estadisticaAventurero>valorCondicion
        }
    },
    MENOR_QUE {
        override fun cumpleCondicion(estadisticaAventurero: Int, valorCondicion: Int): Boolean {
            return estadisticaAventurero<valorCondicion
        }
    };

    abstract fun cumpleCondicion(valor1:Int,valor2:Int): Boolean
}

enum class Accion{
    ATAQUE_FISICO {
        override fun generarHabilidad(emisor: Aventurero, objetivo: Aventurero): Habilidad {
            return Ataque(name,emisor.estadisticas.ataqueFisico().toDouble(),
            emisor.estadisticas.precisionFisica().toDouble(),objetivo)
        }
    },
    DEFENDER {
        override fun generarHabilidad(emisor: Aventurero, objetivo: Aventurero): Habilidad {
            return Defensa(name,emisor,objetivo)
        }
    },
    CURAR {
        override fun generarHabilidad(emisor: Aventurero, objetivo: Aventurero): Habilidad {
            return Curar(name,emisor.estadisticas.poderMagico(),objetivo)
        }
    },
    ATAQUE_MAGICO {
        override fun generarHabilidad(emisor: Aventurero, objetivo: Aventurero): Habilidad {
            return AtaqueMagico(name,emisor.estadisticas.poderMagico().toDouble(),
            emisor.nivel,objetivo)
        }
    },
    MEDITAR {
        override fun generarHabilidad(emisor: Aventurero, objetivo: Aventurero): Habilidad {
            return Meditar(name,emisor)
        }
    };

    abstract fun generarHabilidad(emisor:Aventurero, objetivo:Aventurero):Habilidad
}