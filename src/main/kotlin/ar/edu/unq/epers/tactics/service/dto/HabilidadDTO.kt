package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.*
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
        JsonSubTypes.Type(value = AtaqueDTO::class, name = "Attack"),
        JsonSubTypes.Type(value = DefensaDTO::class, name = "Defend"),
        JsonSubTypes.Type(value = CurarDTO::class, name = "Heal"),
        JsonSubTypes.Type(value = AtaqueMagicoDTO::class, name = "MagicAttack"),
        JsonSubTypes.Type(value = MeditarDTO::class, name = "Meditate")
)
abstract class HabilidadDTO(){
    companion object {

        fun desdeModelo(habilidad: Habilidad):HabilidadDTO{
            return habilidad.desdeModelo()
        }
    }

    abstract fun aModelo(): Habilidad
}

data class AtaqueDTO(val tipo:String,
                     val daño: Double,
                     val precisionFisica: Double,
                     val objetivo: AventureroDTO): HabilidadDTO(){
    override fun aModelo(): Habilidad {
        return Ataque(tipo,daño,precisionFisica,objetivo.aModelo())
    }
}

class DefensaDTO(val tipo:String,
                 val source: AventureroDTO,
                 val objetivo: AventureroDTO): HabilidadDTO(){
    override fun aModelo(): Habilidad {
        return Defensa(tipo,source.aModelo(),objetivo.aModelo())
    }
}

data class CurarDTO(val tipo:String,
                    val poderMagico: Int,
                    val objetivo: AventureroDTO): HabilidadDTO(){

    override fun aModelo(): Habilidad {
        return Curar(tipo,poderMagico,objetivo.aModelo())
    }
}

data class AtaqueMagicoDTO(val tipo:String,
                           val poderMagico: Double,
                           val sourceLevel: Int,
                           val objetivo: AventureroDTO): HabilidadDTO(){
    override fun aModelo(): Habilidad {
        return AtaqueMagico(tipo,poderMagico,sourceLevel,objetivo.aModelo())
    }
}

class MeditarDTO(val tipo:String,val objetivo: AventureroDTO): HabilidadDTO(){
    override fun aModelo(): Habilidad {
        return Meditar(tipo,objetivo.aModelo())
    }
}