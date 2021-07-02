package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.service.dto.AtributosDTO
import ar.edu.unq.epers.tactics.service.dto.AventureroDTO
import javassist.NotFoundException
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*

@Entity
@Cacheable
@Cache(region = "aventureroCache",usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class Aventurero(
                    @Column(unique = true,nullable = false)
                    var nombre: String,

                    @OneToMany(cascade =[CascadeType.ALL] ,fetch = FetchType.EAGER)
                    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
                    var tacticas: List<Tactica>,
                    var imagenURL:String,
                    @OneToOne(cascade =[CascadeType.ALL] ,fetch = FetchType.EAGER)
                    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
                    var atributos: Atributos,
                    var nivel : Int = 1
    ): Serializable {
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ManyToMany(cascade =[CascadeType.ALL] ,fetch = FetchType.EAGER)
    var proficiencias = mutableSetOf(Clase("Aventurero"))


    @OneToOne(cascade =[CascadeType.ALL] ,fetch = FetchType.EAGER)
    var estadisticas = Estadistica(this)
    var vidaMax: Int
    var vida : Int
    var mana : Int
    var manaMax : Int

    var curacionRealizada: Int
    var dañoFisicoRealizado : Double

    var puntosDeExperiencia = 0

    init{
        vida = this.estadisticas.vida()
        vidaMax = vida
        mana = this.estadisticas.mana()
        manaMax = mana

        curacionRealizada = 0

        dañoFisicoRealizado = 0.0
    }

    fun setearParty(party: Party?) {
        this.party=party
    }

    fun ordenarTacticas(){
        tacticas = tacticas.sortedBy { tactica -> tactica.prioridad }
    }

    fun mejorarFuerza(cantidad:Int) {
        atributos.fuerza += cantidad
    }

    fun mejorarDestreza(cantidad:Int) {
        atributos.destreza += cantidad
    }

    fun mejorarConstitucion(cantidad:Int) {
        atributos.constitucion += cantidad
    }

    fun mejorarInteligencia(cantidad:Int) {
        atributos.inteligencia += cantidad
    }

    fun puedeMejorar(requisitos : List<String>) : Boolean{
        var resultado : Boolean = true
        var nombreDeProficiencias = proficiencias.map { clase -> clase.nombreClase  }
        requisitos.forEach{requisito -> resultado = resultado && nombreDeProficiencias.contains(requisito)}
        return resultado && puntosDeExperiencia > 0
    }

    fun aplicarMejora(claseAMejorar: Clase){
        proficiencias.add(claseAMejorar)
        restarPuntosDeXp()
    }

    fun aplicarAtributos(atributosParaAplicar : List<AtributoDeFormacion>){
        atributosParaAplicar.forEach { atributo -> aplicarAtributo(atributo)}
    }

    fun aplicarAtributo(atributo : AtributoDeFormacion){
        when {
            atributo.id == "Fuerza" -> mejorarFuerza(atributo.cantidad!!)
            atributo.id == "Inteligencia" -> mejorarInteligencia(atributo.cantidad!!)
            atributo.id == "Destreza" -> mejorarDestreza(atributo.cantidad!!)
            atributo.id == "Constitucion" -> mejorarConstitucion(atributo.cantidad!!)
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.EAGER)
    var party: Party? = null

    var estaDefendiendoAliado = false
    @OneToOne(cascade =[CascadeType.ALL])
    var aliadoDefensor:Aventurero? = null
    var rondasSiendoDefendidoRestantantes = 0

    var dañoMagicoTotalRealizado:Double = 0.0

    fun atributosToDTO(): AtributosDTO {
        return AtributosDTO(this.id,this.atributos.fuerza,
                this.atributos.destreza,this.atributos.constitucion,
                this.atributos.inteligencia)
    }

    fun actualizarDesdeDTO(aventureroDTO: AventureroDTO){
        this.nombre = aventureroDTO.nombre
        this.nivel = aventureroDTO.nivel
        this.imagenURL = aventureroDTO.imagenURL
        this.asignarAtributos(aventureroDTO)
        this.tacticas = aventureroDTO.tacticas.map {tacticaDTO -> Tactica.toModel(tacticaDTO) }
    }

    fun recibirAtaque(danio : Double){
        if(estaSiendoDefendidoPorAliado()){
            derivarDanioADefensor(danio)
        }else{
            recibirAtaqueFinal(danio)
        }

    }

    private fun recibirAtaqueFinal(danio: Double) {
        if(estaDefendiendoAliado){
            recibirDanioReducido(danio)
        }else{
            recibirDanioTotal(danio)
        }
    }

    private fun recibirDanioTotal(danio: Double) {
        var danioARecibir = danio.toInt()
        if (danioARecibir>vida){
            this.vida = 0
        }else{
            this.vida -= danio.toInt()
        }
    }

    private fun recibirDanioReducido(danio: Double) {
        var danioARecibir = danio.toInt()/2
        if (danioARecibir>vida){
            this.vida = 0
        }else{
            this.vida -= danio.toInt()/2
        }


    }

    private fun derivarDanioADefensor(danio: Double) {
        this.aliadoDefensor!!.recibirAtaque(danio)
        rondasSiendoDefendidoRestantantes--
    }


    private fun estaSiendoDefendidoPorAliado(): Boolean {
        return rondasSiendoDefendidoRestantantes>0
    }

    private fun asignarAtributos(aventureroDTO : AventureroDTO){
        atributos.fuerza = aventureroDTO.atributos.fuerza
        atributos.inteligencia = aventureroDTO.atributos.inteligencia
        atributos.destreza = aventureroDTO.atributos.destreza
        atributos.constitucion = aventureroDTO.atributos.constitucion
        vida = nivel * 5 + this.atributos.constitucion * 2 + this.atributos.fuerza
        vidaMax = nivel * 5 + this.atributos.constitucion * 2 + this.atributos.fuerza
        mana = nivel + this.atributos.inteligencia
    }

    fun curarse(poderMagico: Int) {
        if (vidaMax < vida + poderMagico){
            vida = vidaMax
        }else{
            vida += poderMagico
        }
    }

    fun defenderAliado(aventurero: Aventurero) {
        if(this.party!!.id == aventurero.party!!.id){
            aventurero.actualizarRondasSiendoDefendido() // al que defiende le setea la variable en 3
            aventurero.setearAliadoDefensor(this)
            this.actualizarEstaDefiendoAliado()
        }else{
            throw NotFoundException("no se puede defender a un enemigo")
        }

    }

    fun actualizarEstaDefiendoAliado() {
        estaDefendiendoAliado.not()
    }

    fun setearAliadoDefensor(aventurero: Aventurero) {
        aliadoDefensor=aventurero
    }

    fun actualizarRondasSiendoDefendido() {
        rondasSiendoDefendidoRestantantes = 3
    }

    var meditacionesTotales: Int = 0
    fun meditar() {
        if (manaMax < mana + nivel){
            mana = manaMax
        }else{
            mana += nivel
        }
         meditacionesTotales += 1
    }

    fun resetear(){
        vida = vidaMax
        mana = manaMax
    }

    fun generarHabilidad(enemigos: List<Aventurero>): Habilidad {
        var habilidadAGenerar:Habilidad? = null
        var contador = 0
        while(habilidadAGenerar==null&&contador<tacticas.size){
            val tactica = tacticas.get(contador)

            habilidadAGenerar = tactica.generarHabilidadSiPuede(this,enemigos)
            contador++
        }
        return habilidadAGenerar!!
    }

    fun descontarMana(manaADescontar: Int) {
        mana -= manaADescontar
    }

    fun estaVivo(): Boolean {
        return vida>0
    }

    fun sumarDañoMagicoRealizado(poderMagico: Double) {
        dañoMagicoTotalRealizado += poderMagico
    }

    fun ataqueFisico(): Int {
        return this.estadisticas.ataqueFisico()
    }

    fun precisionFisica(): Int {
        return this.estadisticas.precisionFisica()
    }

    fun poderMagico(): Int {
        return this.estadisticas.poderMagico()
    }

    fun armadura(): Int {
        return this.estadisticas.armadura()
    }

    fun velocidad(): Int {
        return this.estadisticas.velocidad()
    }


    fun agregarCuracionExitosa(min: Int) {
        curacionRealizada += min
    }
    fun sumarDañoRealizado(daño: Double) {
        this.dañoFisicoRealizado += daño
    }

    fun sumarPuntoYLvl() {
        puntosDeExperiencia++
        nivel++
    }

    fun restarPuntosDeXp() { puntosDeExperiencia-- }

    fun agregarProficiencias(nombreDeClase: String) { proficiencias.add(Clase(nombreDeClase)) }

}