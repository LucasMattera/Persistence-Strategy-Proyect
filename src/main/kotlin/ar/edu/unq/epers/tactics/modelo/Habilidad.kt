package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.service.dto.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import javax.persistence.*
import kotlin.math.min


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
@Cache(region = "habilidadCache",usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
abstract class Habilidad(@Transient var objetivo:Aventurero, @Transient var random: RandomValue = RandomValue()) {

     @Id
     @GeneratedValue(strategy = GenerationType.TABLE)
     var id:Long?=null

    @ManyToOne(fetch = FetchType.LAZY)
    var pelea:Pelea?=null

    abstract fun realizarAccion()
    abstract fun desdeModelo(): HabilidadDTO
    open fun actualizarEmisor(aventurero: Aventurero) {}
    fun actualizarObjetivo(aventurero: Aventurero){
        objetivo=aventurero
    }
}

@Entity
@Cacheable
@Cache(region = "defensaCache",usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class Defensa(val tipo:String,
              @Transient val source: Aventurero,
              objetivo: Aventurero): Habilidad(objetivo) {

    override fun realizarAccion() {
        source.defenderAliado(objetivo)
    }

    override fun desdeModelo(): HabilidadDTO {
        return DefensaDTO(tipo,AventureroDTO.desdeModelo(source), AventureroDTO.desdeModelo(objetivo))
    }
}
@Entity
@Cacheable
@Cache(region = "ataqueCache",usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class Ataque(val tipo:String,
             val daño: Double,
             val precisionFisica: Double,
             objetivo: Aventurero,
             random: RandomValue = RandomValue()): Habilidad(objetivo,random) {
    val ataqueExitoso = random.getNumber() + precisionFisica >= objetivo.estadisticas.armadura() + (objetivo.estadisticas.velocidad() / 2)

    override fun realizarAccion() {
        if (ataqueExitoso){
            objetivo.recibirAtaque(daño)
        }

    }

    override fun actualizarEmisor(aventurero: Aventurero){
        if(ataqueExitoso){
            aventurero.sumarDañoRealizado(daño)
        }
    }

    override fun desdeModelo(): HabilidadDTO {
        return AtaqueDTO(tipo,daño,precisionFisica,AventureroDTO.desdeModelo(objetivo))
    }

}
@Entity
@Cacheable
@Cache(region = "curarCache",usage = CacheConcurrencyStrategy.READ_ONLY)
class Curar(val tipo:String,
            val poderMagico: Int,
            objetivo: Aventurero): Habilidad(objetivo) {

    override fun realizarAccion() {
        objetivo.curarse(poderMagico)
    }

    override fun desdeModelo(): HabilidadDTO {
        return CurarDTO(tipo,poderMagico,AventureroDTO.desdeModelo(objetivo))
    }
    override fun actualizarEmisor(aventurero: Aventurero){
        aventurero.descontarMana(5)
        aventurero.agregarCuracionExitosa(min((objetivo.vidaMax - objetivo.vida) , poderMagico))
    }

}
@Entity
@Cacheable
@Cache(region = "meditarCache",usage = CacheConcurrencyStrategy.READ_ONLY)
class Meditar(val tipo:String, objetivo: Aventurero): Habilidad(objetivo) {

    override fun realizarAccion() {
        objetivo.meditar()
    }

    override fun desdeModelo(): HabilidadDTO {
        return MeditarDTO(tipo,AventureroDTO.desdeModelo(objetivo))
    }

}
@Entity
@Cacheable
@Cache(region = "ataquemagicoCache",usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class AtaqueMagico(val tipo:String,
                   val poderMagico: Double,
                   val sourceLevel: Int,
                   objetivo: Aventurero,
                   random: RandomValue = RandomValue()): Habilidad(objetivo) {

    val ataqueExitoso = random.getNumber() + sourceLevel>=(objetivo.estadisticas.velocidad()/2)

    override fun realizarAccion() {
        if (ataqueExitoso){
            objetivo.recibirAtaque(poderMagico)
        }
    }

    override fun desdeModelo(): HabilidadDTO {
        return AtaqueMagicoDTO(tipo,poderMagico,sourceLevel,AventureroDTO.desdeModelo(objetivo))
    }

    override fun actualizarEmisor(aventurero: Aventurero){
        aventurero.descontarMana(5)
        if(ataqueExitoso){
            aventurero.sumarDañoMagicoRealizado(poderMagico)
        }
    }
}