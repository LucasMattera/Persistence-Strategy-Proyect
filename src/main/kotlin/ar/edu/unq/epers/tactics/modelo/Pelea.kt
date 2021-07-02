package ar.edu.unq.epers.tactics.modelo

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Cacheable
@Cache(region = "peleaCache",usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class Pelea(
    @OneToOne(cascade =[CascadeType.ALL],fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var party: Party,
    val partyEnemiga: String,
    val date:LocalDateTime = LocalDateTime.now()) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var ganoPelea:Boolean? = null


    @OneToMany(mappedBy = "pelea",cascade =[CascadeType.ALL] ,fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var habilidadesRealizadas:MutableList<Habilidad> = mutableListOf()




    @OneToMany(mappedBy = "pelea",cascade =[CascadeType.ALL] ,fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var habilidadesRecibidas:MutableList<Habilidad> = mutableListOf()

    fun setearSiGanoPelea(){
        ganoPelea = party.quedanAventurerosVivos()
    }

    fun guardarHabilidadRealizada(habilidad: Habilidad) {
        habilidadesRealizadas.add(habilidad)
    }

    fun guardarHabilidadRecibida(habilidad: Habilidad) {
        habilidadesRecibidas.add(habilidad)
    }



}