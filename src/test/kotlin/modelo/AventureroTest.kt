package modelo

import ar.edu.unq.epers.tactics.modelo.Atributos
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.service.dto.AventureroDTO
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class AventureroTest {
    lateinit var tacticas : List<Tactica>
    lateinit var spiderman: Aventurero
    lateinit var capAmerica: Aventurero
    lateinit var ironMan: Aventurero
    lateinit var thor: Aventurero

    @Before
    fun setUp(){
        tacticas = listOf()
        spiderman = Aventurero("spiderman",tacticas,"",Atributos(0,0,0,0),1)
        capAmerica = Aventurero("capAmerica",tacticas,"",Atributos(0,0,0,0),1)
        ironMan = Aventurero("ironMan",tacticas,"",Atributos(0,0,0,0),1)
        thor = Aventurero("thor",tacticas,"",Atributos(0,0,0,0),1)
    }

    @Test
    fun testUnNuevoAventureroSinEspecificarTienePorDefectoValoresEnSusStats(){
        Assert.assertEquals(spiderman.nivel,1)
        Assert.assertEquals(spiderman.atributos.fuerza,0)
        Assert.assertEquals(spiderman.atributos.constitucion,0)
        Assert.assertEquals(spiderman.atributos.destreza,0)
        Assert.assertEquals(spiderman.atributos.inteligencia,0)
        Assert.assertEquals(spiderman.vida,5)
        Assert.assertEquals(spiderman.mana,1)
        Assert.assertEquals(spiderman.armadura(),1)
        Assert.assertEquals(spiderman.velocidad(),1)
        Assert.assertEquals(spiderman.poderMagico(),1)
        Assert.assertEquals(spiderman.ataqueFisico(),1)
        Assert.assertEquals(spiderman.precisionFisica(),1)
    }

    @Test
    fun testUnAventureroPuedeCrearseConEstadisticasEspecificas(){

        var nuevoAventurero = Aventurero("hulk",tacticas,"", Atributos(10,15,20,10),1)


        nuevoAventurero.actualizarDesdeDTO(AventureroDTO.desdeModelo(nuevoAventurero))

        Assert.assertEquals(nuevoAventurero.nivel,1)
        Assert.assertEquals(nuevoAventurero.atributos.fuerza,10)
        Assert.assertEquals(nuevoAventurero.atributos.destreza,15)
        Assert.assertEquals(nuevoAventurero.atributos.constitucion,20)
        Assert.assertEquals(nuevoAventurero.atributos.inteligencia,10)
        Assert.assertEquals(nuevoAventurero.vida,55)
        Assert.assertEquals(nuevoAventurero.mana,11)
        Assert.assertEquals(nuevoAventurero.armadura(),21)
        Assert.assertEquals(nuevoAventurero.velocidad(),16)
        Assert.assertEquals(nuevoAventurero.ataqueFisico(),18)
        Assert.assertEquals(nuevoAventurero.poderMagico(),11)
        Assert.assertEquals(nuevoAventurero.precisionFisica(),26)

    }

    @Test
    fun noSePuedeCrearUnAventureroConParametrosInvalidos(){
        assertThrows<Exception> { var nuevoAventurero = Aventurero("hulk",tacticas,"", Atributos(1000,15,20,10),1) }
    }
}