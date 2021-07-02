package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.service.dto.AtributosDTO
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*

@Entity
@Cacheable
@Cache(region = "atributosCache",usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class Atributos(var fuerza:Int,var destreza:Int,var constitucion:Int,var inteligencia:Int):Serializable {

    init{
        if (fuerza >100 || destreza > 100 || constitucion> 100 || inteligencia > 100){
            throw Exception("Parametros no validos")
        }else{
            this.fuerza = fuerza
            this.constitucion = constitucion
            this.destreza = destreza
            this.inteligencia = inteligencia
        }
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun toDTO():AtributosDTO{
        return AtributosDTO(this.id,this.fuerza,
                            this.destreza,this.constitucion,
                            this.inteligencia)
    }

    companion object{
        fun toModel(atributos: AtributosDTO):Atributos{
            return Atributos(atributos.fuerza,
                    atributos.destreza,atributos.constitucion,
                    atributos.inteligencia)
        }
    }



}
