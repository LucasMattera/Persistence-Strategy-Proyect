package ar.edu.unq.epers.tactics.modelo

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*

@Entity
@Cacheable
@Cache(region = "claseCache",usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class Clase(var nombreClase:String):Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}