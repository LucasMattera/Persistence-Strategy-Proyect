package ar.edu.unq.epers.tactics.modelo

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*

@Entity
@Cacheable
@Cache(region = "atrFormacionCache",usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class AtributoDeFormacion : Serializable {

    var id: String? = null
    var cantidad: Int? = null
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var idH : Long? = null
    @ManyToOne(fetch = FetchType.EAGER)
    var party: Party? = null

    protected constructor() {}

    constructor(atributo:String,cantidad:Int) {
        this.id = atributo
        this.cantidad = cantidad

    }

    override fun equals(other:Any?) : Boolean{
        var result = false
        when(other){
            is AtributoDeFormacion -> {
                result = this.id == other.id &&
                        this.cantidad == other.cantidad
            }
        }
        return result
    }

    fun restarCantidad(cantidad : Int) : Int{
        return this.cantidad!! - cantidad
    }

    fun setearParty(party: Party){
        this.party = party
    }
}