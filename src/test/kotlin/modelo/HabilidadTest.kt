package modelo

import ar.edu.unq.epers.tactics.modelo.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HabilidadTest {
    lateinit var party                          : Party
    lateinit var aventurero                     : Aventurero
    lateinit var aventurero2                    : Aventurero
    lateinit var aventurero3                    : Aventurero
    lateinit var habilidadAtaqueExitoso         : Habilidad
    lateinit var habilidadDefensa               : Habilidad
    lateinit var habilidadCurar                 : Habilidad
    lateinit var habilidadAtaqueMagicoExitoso   : Habilidad
    lateinit var habilidadMeditar               : Habilidad

    @Before
    fun setUp(){
        var tacticas = listOf<Tactica>()
        var atributos = Atributos(10,10,40,10)
        var randomProvider = RandomValue(true,100)
        party = Party("party","")
        party.id = 1
        aventurero = Aventurero("hulk",tacticas,"",atributos)
        aventurero2 = Aventurero("spiderman",tacticas,"",atributos)
        aventurero3 = Aventurero("ironman",tacticas,"",atributos)
        var ataqueFisico = aventurero3.ataqueFisico()
        var precisionFisica = aventurero3.precisionFisica()
        var ataqueMagico = aventurero3.poderMagico()
        party.agregarAventurero(aventurero)
        party.agregarAventurero(aventurero3)
        habilidadAtaqueExitoso = Ataque("ATAQUE",ataqueFisico.toDouble(),precisionFisica.toDouble(),aventurero2,randomProvider)
        habilidadDefensa = Defensa("DEFENSA",aventurero,aventurero3)
        habilidadCurar = Curar("CURAR",ataqueMagico,aventurero2)
        habilidadAtaqueMagicoExitoso = AtaqueMagico("ATAQUE_MAGICO",ataqueMagico.toDouble(),aventurero.nivel,aventurero2,randomProvider)
        habilidadMeditar = Meditar("MEDITAR",aventurero2)
    }

    @Test
    fun testHabilidadAtaqueFisicoExitoso(){
        //aventurero3 tiene 16 de ataque fisico
        habilidadAtaqueExitoso.realizarAccion()
        // aventurero2 tiene (fuerza = 10, consti = 40, nivel=1)
        // entonces aventurero2.vida = 95 = 5(por nivel 1) + 10(fuerza) + 40x2(consti)
        Assert.assertEquals(aventurero2.vida,79) // vida = 95 - 16 = 79
    }

    @Test
    fun testHabildiadAtaqueFisicoNoExitoso(){
        var randomProvider = RandomValue(true,1)
        var habilidadAtaqueNoExitoso = Ataque("ATAQUE",aventurero.ataqueFisico().toDouble(),aventurero.precisionFisica().toDouble(),aventurero2)
        // aventurero2 tiene (fuerza = 10, consti = 40, nivel=1)
        // entonces aventurero2.vida = 95 = 5(por nivel 1) + 10(fuerza) + 40x2(consti)
        habilidadAtaqueNoExitoso.realizarAccion()

        Assert.assertEquals(aventurero2.vida,95) // no se golpea el ataque
    }

    @Test
    fun testHabilidadDefensa(){
        habilidadDefensa.realizarAccion()
        var randomProvider = RandomValue(true,100)
        var habilidadAtaqueAAventurero3 = Ataque("ATAQUE",aventurero2.ataqueFisico().toDouble(),aventurero2.precisionFisica().toDouble(),aventurero3,randomProvider)
        habilidadAtaqueAAventurero3.realizarAccion()

        Assert.assertEquals(aventurero2.vida,95)
        Assert.assertEquals(aventurero3.vida,95)
        Assert.assertEquals(aventurero.vida,79)
    }

    @Test
    fun habilidadCurarConVidaYPoderMagicoMenorAVidaMaxima(){
        habilidadAtaqueExitoso.realizarAccion() // Le saco 16 de vida al aventurero2
        habilidadCurar.realizarAccion() // Le curo 11 de vida al aventurero2
        Assert.assertEquals(aventurero2.vida,90)
    }

    @Test
    fun habilidadCurarConVidaYPoderMagicoNoMenorAVidaMaxima(){
        habilidadCurar.realizarAccion() // No lo cura ya que superaria al maximo.
        Assert.assertEquals(aventurero2.vida,95)
    }

    @Test
    fun habilidadAtaqueMagico(){
        habilidadAtaqueMagicoExitoso.realizarAccion()

        Assert.assertEquals(aventurero2.vida,84)
    }

    @Test
    fun habilidadMeditar(){
        aventurero2.manaMax = 100
        habilidadMeditar.realizarAccion()
        Assert.assertEquals(aventurero2.mana,12)
    }
}