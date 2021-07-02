package ar.edu.unq.epers.tactics.modelo

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.engine.FetchStyle
import java.io.Serializable
import javax.persistence.*


@Entity
@Cacheable
@Cache(region = "partyCache",usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class Party(@Column(unique = true,nullable = false) var nombre: String,
                                                    var imagenURL:String):Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var numeroDeAventureros = 0
    var estaEnPelea = false
    @OneToMany(mappedBy = "party", cascade = [CascadeType.ALL],fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var atributosDeFormaciones : List<AtributoDeFormacion> = listOf(AtributoDeFormacion("Fuerza",0),
                                                                    AtributoDeFormacion("Inteligencia",0),
                                                                    AtributoDeFormacion("Destreza",0),
                                                                    AtributoDeFormacion("Constitucion",0))

    init {
        atributosDeFormaciones.forEach{atributo->atributo.setearParty(this)}
    }

    @OneToMany(mappedBy = "party",cascade =[CascadeType.ALL] ,fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var aventureros:List<Aventurero> = listOf()

    fun agregarAventurero(aventurero: Aventurero) {
        aventureros+=aventurero
        numeroDeAventureros++
        aventurero.setearParty(this)

    }

    fun eliminarAventurero(aventurero: Aventurero) {
        aventureros-= aventurero
        numeroDeAventureros--
    }

    fun setearAventurerosDesdeDTO(aventureros:List<Aventurero>){
        this.aventureros = aventureros
    }


    fun estaEnPelea(): Boolean {
        return estaEnPelea
    }

    fun perteneceAParty(aventurero: Aventurero): Boolean {
        return aventureros.contains(aventurero)
    }

    fun setearAtributosDeFormacion(atributos : List<AtributoDeFormacion>){
        atributosDeFormaciones.forEach{atributo->atributos.forEach { atributoNuevo-> if (atributo.id!! == atributoNuevo.id!!){atributo.cantidad = atributoNuevo.cantidad} }}

    }

    fun aplicarAtributosDeFormacion(atributosParaAplicar : List<AtributoDeFormacion>){
        this.aventureros.forEach { aventurero -> aventurero.aplicarAtributos(atributosParaAplicar) }
    }

    fun sePoneAPelear() {
        if (!estaEnPelea) {
            estaEnPelea = !estaEnPelea
        }
    }

    fun terminarPelea(pelea: Pelea) {
        if(estaEnPelea){
            estaEnPelea = !estaEnPelea
            sumarPuntosYLvlSiGano()
        }

    }

    private fun sumarPuntosYLvlSiGano(){
        if(algunAventureroEstaVivo()){
            sumarPuntosYLvlAventureros()
        }
    }

    private fun algunAventureroEstaVivo(): Boolean{
        var aventurerosVivos = false
        for(aventurero in aventureros){
            aventurerosVivos = aventurerosVivos || aventurero.estaVivo()
        }
        return aventurerosVivos
    }

    private fun sumarPuntosYLvlAventureros(){
        for (aventurero in aventureros){
            aventurero.sumarPuntoYLvl()
        }
    }

    fun resetearAventureros(){
        aventureros.forEach{aventurero-> aventurero.resetear()}
    }

    fun quedanAventurerosVivos(): Boolean {
        var quedanAventurerosVivos = false
        for(aventurero in aventureros){
            quedanAventurerosVivos = quedanAventurerosVivos || aventurero.estaVivo()
        }
        return quedanAventurerosVivos
    }

    fun armarClasesDeAventureros():HashMap<String,Int>{
        val clasesDeAventureros = hashMapOf<String,Int>()
        val proficienciasDeAventureros = aventureros.flatMap { aventurero -> aventurero.proficiencias}
        val nombresDeProficiencias = proficienciasDeAventureros.map { prof->prof.nombreClase }

        for(clase in nombresDeProficiencias){
            if(clasesDeAventureros.containsKey(clase)){
                val nuevoValor = clasesDeAventureros.get(clase)!! + 1
                clasesDeAventureros.put(clase,nuevoValor)
                // sumar 1
            }else{
                clasesDeAventureros.put(clase,1)
            }
        }
        return clasesDeAventureros
    }

}