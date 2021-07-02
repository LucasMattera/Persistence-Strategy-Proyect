package ar.edu.unq.epers.tactics.modelo

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*

@Entity
@Cacheable
@Cache(region = "estadisticaCache",usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class Estadistica(@OneToOne
                  var aventurero:Aventurero):Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null


    fun vida(): Int{

        return aventurero.nivel * 5 + aventurero.atributos.constitucion * 2 + aventurero.atributos.fuerza
    }
    fun armadura(): Int{
        return aventurero.nivel + aventurero.atributos.constitucion
    }
    fun mana(): Int{
        return aventurero.nivel + aventurero.atributos.inteligencia
    }
    fun velocidad(): Int{
        return aventurero.nivel + aventurero.atributos.destreza
    }
    fun ataqueFisico(): Int{
        return aventurero.nivel + aventurero.atributos.fuerza + (aventurero.atributos.destreza / 2)
    }
    fun poderMagico(): Int{
        return aventurero.nivel + aventurero.atributos.inteligencia
    }
    fun precisionFisica(): Int{
        return aventurero.nivel + aventurero.atributos.destreza + aventurero.atributos.fuerza
    }
}
