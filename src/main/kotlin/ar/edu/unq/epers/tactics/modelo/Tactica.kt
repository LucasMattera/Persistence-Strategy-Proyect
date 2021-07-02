package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.service.dto.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import java.util.logging.Handler
import javax.persistence.*

@Entity
@Cacheable
@Cache(region = "tacticaCache",usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class Tactica(@Id
              @GeneratedValue(strategy = GenerationType.IDENTITY)
              var id:Long?,
              var prioridad:Int,
              @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
              var receptor: TipoDeReceptor,
              @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
              var tipoDeEstadistica: TipoDeEstadistica,
              @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
              var criterio: Criterio,
              var valor: Int,
              @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
              var accion: Accion
              ):Serializable {

    fun generarHabilidadSiPuede(aventurero:Aventurero,enemigos:List<Aventurero>): Habilidad?{

        if(receptor.equals(TipoDeReceptor.ALIADO)){
            return generarHabilidad(aventurero,aventurero.party!!.aventureros)
        }else if(receptor.equals(TipoDeReceptor.ENEMIGO)){
            return generarHabilidad(aventurero,enemigos)
        }else{
            return accion.generarHabilidad(aventurero,aventurero)
        }
    }


    private fun generarHabilidad(emisor:Aventurero,aventureros: List<Aventurero>): Habilidad?{
        var habilidadAGenerar:Habilidad? = null
        var contador = 0
        while(contador<aventureros.size){
            val aventureroParcial = aventureros.get(contador)
            if(emisor.nombre!=aventureroParcial.nombre&&criterio.cumpleCondicion(tipoDeEstadistica.getValor(aventureroParcial),valor)){
                habilidadAGenerar = accion.generarHabilidad(emisor,aventureroParcial)
            break
            }
            contador++
        }
        return habilidadAGenerar
    }



    fun toDTO():TacticaDTO{
        return TacticaDTO(this.id,
                            this.prioridad,
                            this.receptor,
                            this.tipoDeEstadistica,
                            this.criterio,
                            this.valor,
                            this.accion)
    }

    companion object{
        fun toModel(tacticaDTO: TacticaDTO):Tactica{
            return Tactica(tacticaDTO.id,
                            tacticaDTO.prioridad,
                            tacticaDTO.receptor,
                            tacticaDTO.tipoDeEstadistica,
                            tacticaDTO.criterio,
                            tacticaDTO.valor,
                            tacticaDTO.accion)
        }
    }

}
