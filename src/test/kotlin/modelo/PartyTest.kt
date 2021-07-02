package modelo

import ar.edu.unq.epers.tactics.modelo.Atributos
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Tactica
import org.junit.*

class PartyTest {

    lateinit var tacticas : List<Tactica>
    lateinit var aventurero1:Aventurero
    lateinit var aventurero2:Aventurero
    lateinit var aventurero3:Aventurero
    lateinit var aventurero4:Aventurero
    lateinit var aventurero5: Aventurero

    lateinit var party: Party

    @Before
    fun setUp(){

        tacticas = listOf()
        aventurero1 = Aventurero("hulk",tacticas,"", Atributos(0,0,0,0),1)
        aventurero2 = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        aventurero3 = Aventurero("capAmerica",tacticas,"", Atributos(0,0,0,0),1)
        aventurero4 = Aventurero("ironMan",tacticas,"", Atributos(0,0,0,0),1)
        aventurero5 = Aventurero("thor",tacticas,"", Atributos(0,0,0,0),1)

        party = Party("Heroes","asd")


    }

    @Test
    fun TresMagosY5Aventureros(){
        aventurero1.agregarProficiencias("Mago")
        aventurero2.agregarProficiencias("Mago")

        party.agregarAventurero(aventurero1)
        party.agregarAventurero(aventurero2)
        party.agregarAventurero(aventurero3)
        party.agregarAventurero(aventurero4)
        party.agregarAventurero(aventurero5)

        val clases = party.armarClasesDeAventureros()

        Assert.assertEquals(2,clases.size)
        Assert.assertEquals(5,clases.get("Aventurero"))
        Assert.assertEquals(2,clases.get("Mago"))
    }

}