package dao

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.persistencia.dao.DataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongo.MongoFormacionDAO
import ar.edu.unq.epers.tactics.service.DataService
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.impl.DataServiceImpl
import ar.edu.unq.epers.tactics.service.impl.FormacionServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import org.junit.*

class FormacionServiceImplTest {

    lateinit var formacionDAO: MongoFormacionDAO
    lateinit var partyDAO: PartyDAO
    lateinit var formacionService: FormacionServiceImpl
    lateinit var dataDAO: DataDAO
    lateinit var serviceData:DataService
    lateinit var partyService : PartyService
    lateinit var aventurero1: Aventurero
    lateinit var aventurero2: Aventurero
    lateinit var aventurero3: Aventurero
    lateinit var aventurero4: Aventurero


    @Before
    fun setUp(){
        formacionDAO = MongoFormacionDAO()
        partyDAO = HibernatePartyDAO()
        formacionService = FormacionServiceImpl(formacionDAO,partyDAO)
        dataDAO = HibernateDataDAO()
        serviceData=DataServiceImpl(dataDAO)
        partyService = PartyServiceImpl(partyDAO,formacionDAO)

        var tacticas = listOf<Tactica>()
        var atributos = Atributos(0,0,0,0)
        var atributos2 = Atributos(0,0,0,0)
        var atributos3 = Atributos(0,0,0,0)
        var atributos4 = Atributos(0,0,0,0)

        aventurero1 = Aventurero("pepe",tacticas,"",atributos)
        aventurero2 = Aventurero("pepe2",tacticas,"",atributos2)
        aventurero3 = Aventurero("pepe3",tacticas,"",atributos3)
        aventurero4 = Aventurero("pepe4",tacticas,"",atributos4)
    }


    @After
    fun clearAll(){
        formacionDAO.deleteAll()
        serviceData.clear()
    }

    @Test
    fun crearYRecuperarFormacion(){
        val requisitos = listOf<Requisito>(Requisito("Mago",2))
        val stats = listOf<AtributoDeFormacion>(AtributoDeFormacion("Destreza",2))
        val formacion = formacionService.crearFormacion("Boy scout",requisitos,stats)

        val formacionRecuperada = formacionService.getBy("nombre",formacion.nombre)

        Assert.assertEquals(formacionRecuperada.nombre,formacion.nombre)
        Assert.assertEquals(formacionRecuperada.atributos.size,1)
        Assert.assertEquals(formacionRecuperada.requisitos.size,1)

    }

    @Test
    fun recuperarTodasLasFormaciones(){
        val requisitos = listOf(Requisito("Mago",4),Requisito("Aventurero",2))
        val requisitos2 = listOf(Requisito("Mago",2),Requisito("Aventurero",1))
        val requisitos3 = listOf(Requisito("Mago",3),Requisito("Aventurero",1))
        val requisitos4 = listOf(Requisito("Mago",5),Requisito("Aventurero",3))
        val stats = listOf<AtributoDeFormacion>()

        val formacion1 = formacionService.crearFormacion("Formacion1",requisitos,stats)
        val formacion2 = formacionService.crearFormacion("Formacion2",requisitos2,stats)
        val formacion3 = formacionService.crearFormacion("Formacion3",requisitos3,stats)
        val formacion4 = formacionService.crearFormacion("Formacion4",requisitos4,stats)

        val formaciones = mutableListOf(formacion1,formacion2,formacion3,formacion4)
        val formacionesRecuperadas = formacionService.todasLasFormaciones()

        Assert.assertEquals(formacionesRecuperadas.size,formaciones.size)
        Assert.assertTrue(formacionesRecuperadas.containsAll(formaciones))
    }

    @Test
    fun unaPartyNoPoseeFormaciones(){
        var party = Party("Heroes","")
        partyService.crear(party)

        partyService.agregarAventureroAParty(1,aventurero1)
        partyService.agregarAventureroAParty(1,aventurero2)
        partyService.agregarAventureroAParty(1,aventurero3)
        partyService.agregarAventureroAParty(1,aventurero4)

        val requisitos1 = listOf(Requisito("Mago",5))
        val requisitos2 = listOf(Requisito("Clerigo",3),Requisito("Aventurero",2))
        val requisitos3 = listOf(Requisito("Hechicero",4),Requisito("MaestroDeArmas",1))
        val requisitos4 = listOf(Requisito("Aventurero",4),Requisito("Mago",2),Requisito("Clerigo",2))


        val formacionesRecuperadas = formacionService.formacionesQuePosee(1)

        Assert.assertEquals(formacionesRecuperadas.size,0)
    }

    @Test
    fun unaPartyPoseeUnaFormacion(){
        var party = Party("Heroes","")
        partyService.crear(party)
        aventurero1.agregarProficiencias("Mago")
        aventurero3.agregarProficiencias("Mago")
        aventurero2.agregarProficiencias("Clerigo")

        partyService.agregarAventureroAParty(1,aventurero1)
        partyService.agregarAventureroAParty(1,aventurero2)
        partyService.agregarAventureroAParty(1,aventurero3)
        partyService.agregarAventureroAParty(1,aventurero4)

        val requisitos1 = listOf(Requisito("Mago",5))
        val requisitos2 = listOf(Requisito("Clerigo",3),Requisito("Aventurero",2))
        val requisitos3 = listOf(Requisito("Hechicero",4),Requisito("MaestroDeArmas",1))
        val requisitos4 = listOf(Requisito("Aventurero",4),Requisito("Mago",2),Requisito("Clerigo",1))

        val stats = listOf(AtributoDeFormacion("Destreza",2))

        val formacion1 = formacionService.crearFormacion("Formacion1",requisitos1,stats)
        val formacion2 = formacionService.crearFormacion("Formacion2",requisitos2,stats)
        val formacion3 = formacionService.crearFormacion("Formacion3",requisitos3,stats)
        val formacion4 = formacionService.crearFormacion("Formacion4",requisitos4,stats)

        val formaciones = mutableListOf(formacion4)
        val formacionesRecuperadas = formacionService.formacionesQuePosee(1)

        Assert.assertEquals(formacionesRecuperadas.size,formaciones.size)
        Assert.assertTrue(formacionesRecuperadas.containsAll(formaciones))
    }

    @Test
    fun UnaPartyPoseeDosFormaciones(){
        var party = Party("Heroes","")
        partyService.crear(party)
        aventurero1.agregarProficiencias("Mago")
        aventurero3.agregarProficiencias("Mago")
        aventurero2.agregarProficiencias("Clerigo")

        partyService.agregarAventureroAParty(1,aventurero1)
        partyService.agregarAventureroAParty(1,aventurero2)
        partyService.agregarAventureroAParty(1,aventurero3)
        partyService.agregarAventureroAParty(1,aventurero4)

        val requisitos1 = listOf(Requisito("Mago",2))
        val requisitos2 = listOf(Requisito("Aventurero",2),Requisito("Clerigo",3))
        val requisitos3 = listOf(Requisito("Hechicero",4),Requisito("MaestroDeArmas",1))
        val requisitos4 = listOf(Requisito("Aventurero",4),Requisito("Mago",2),Requisito("Clerigo",1))

        val stats = listOf(AtributoDeFormacion("Destreza",2))

        val formacion1 = formacionService.crearFormacion("Formacion1",requisitos1,stats)
        val formacion2 = formacionService.crearFormacion("Formacion2",requisitos2,stats)
        val formacion3 = formacionService.crearFormacion("Formacion3",requisitos3,stats)
        val formacion4 = formacionService.crearFormacion("Formacion4",requisitos4,stats)

        val formaciones = mutableListOf(formacion4)
        formaciones.add(formacion1)
        val formacionesRecuperadas = formacionService.formacionesQuePosee(1)

        Assert.assertEquals(formacionesRecuperadas.size,formaciones.size)
        Assert.assertTrue(formacionesRecuperadas.containsAll(formaciones))
    }

    @Test
    fun UnaPartyPoseeTresFormaciones(){
        var party = Party("Heroes","")
        partyService.crear(party)
        aventurero1.agregarProficiencias("Mago")
        aventurero3.agregarProficiencias("Mago")
        aventurero2.agregarProficiencias("Clerigo")
        aventurero1.agregarProficiencias("Clerigo")
        aventurero3.agregarProficiencias("Clerigo")

        partyService.agregarAventureroAParty(1,aventurero1)
        partyService.agregarAventureroAParty(1,aventurero2)
        partyService.agregarAventureroAParty(1,aventurero3)
        partyService.agregarAventureroAParty(1,aventurero4)

        val requisitos1 = listOf(Requisito("Mago",2))
        val requisitos2 = listOf(Requisito("Clerigo",3),Requisito("Aventurero",2))
        val requisitos3 = listOf(Requisito("Hechicero",4),Requisito("MaestroDeArmas",1))
        val requisitos4 = listOf(Requisito("Aventurero",4),Requisito("Mago",2),Requisito("Clerigo",2))

        val stats = listOf(AtributoDeFormacion("Destreza",2))

        val formacion1 = formacionService.crearFormacion("Formacion1",requisitos1,stats)
        val formacion2 = formacionService.crearFormacion("Formacion2",requisitos2,stats)
        val formacion3 = formacionService.crearFormacion("Formacion3",requisitos3,stats)
        val formacion4 = formacionService.crearFormacion("Formacion4",requisitos4,stats)

        val formaciones = mutableListOf(formacion4)
        formaciones.add(formacion1)
        formaciones.add(formacion2)
        val formacionesRecuperadas = formacionService.formacionesQuePosee(1)

        Assert.assertEquals(formacionesRecuperadas.size,formaciones.size)
        Assert.assertTrue(formacionesRecuperadas.containsAll(formaciones))
    }

    @Test
    fun AUnaPartyLeCorrespondeUnAtributoPorSuFormacion(){
        var party = Party("Heroes","")
        partyService.crear(party)
        aventurero1.agregarProficiencias("Mago")
        aventurero3.agregarProficiencias("Mago")
        aventurero2.agregarProficiencias("Clerigo")

        partyService.agregarAventureroAParty(1,aventurero1)
        partyService.agregarAventureroAParty(1,aventurero2)
        partyService.agregarAventureroAParty(1,aventurero3)
        partyService.agregarAventureroAParty(1,aventurero4)

        val requisitos1 = listOf(Requisito("Mago",2))

        val stats = listOf(AtributoDeFormacion("Destreza",2))

        formacionService.crearFormacion("Formacion1",requisitos1,stats)

        val atributosQueCorresponden = formacionService.atributosQueCorresponden(1)

        Assert.assertEquals(stats.size,atributosQueCorresponden.size)
        Assert.assertTrue(atributosQueCorresponden.containsAll(stats))
    }

    @Test
    fun UnaPartyLeCorrespondenAtributosDeDosFormaciones(){
        var party = Party("Heroes","")
        partyService.crear(party)
        aventurero1.agregarProficiencias("Mago")
        aventurero3.agregarProficiencias("Mago")
        aventurero2.agregarProficiencias("Clerigo")



        val requisitos1 = listOf(Requisito("Mago",2))
        val requisitos2 = listOf(Requisito("Clerigo",3),Requisito("Aventurero",2))
        val requisitos3 = listOf(Requisito("Hechicero",4),Requisito("MaestroDeArmas",1))
        val requisitos4 = listOf(Requisito("Aventurero",4),Requisito("Mago",2),Requisito("Clerigo",1))

        val stats = mutableListOf(AtributoDeFormacion("Destreza",2), AtributoDeFormacion("Fuerza",5))
        val stats2 = mutableListOf(AtributoDeFormacion("Destreza",4), AtributoDeFormacion("Inteligencia",1))

        formacionService.crearFormacion("Formacion1",requisitos1,stats)
        formacionService.crearFormacion("Formacion2",requisitos2,stats)
        formacionService.crearFormacion("Formacion3",requisitos3,stats)
        formacionService.crearFormacion("Formacion4",requisitos4,stats2)

        partyService.agregarAventureroAParty(1,aventurero1)
        partyService.agregarAventureroAParty(1,aventurero2)
        partyService.agregarAventureroAParty(1,aventurero3)
        partyService.agregarAventureroAParty(1,aventurero4)

        val statsTotales = listOf(AtributoDeFormacion("Destreza",6), AtributoDeFormacion("Fuerza",5),
                                 AtributoDeFormacion("Inteligencia",1))
        val atributosQueCorresponden = formacionService.atributosQueCorresponden(1)

        Assert.assertEquals(atributosQueCorresponden.size,statsTotales.size)
        Assert.assertTrue(atributosQueCorresponden.containsAll(statsTotales))
    }

    @Test
    fun UnaPartyNoLeCorrespondeNingunAtributoPorSusFormaciones(){
        var party = Party("Heroes","")
        partyService.crear(party)
        aventurero1.agregarProficiencias("Mago")
        aventurero3.agregarProficiencias("Mago")
        aventurero2.agregarProficiencias("Clerigo")

        partyService.agregarAventureroAParty(1,aventurero1)
        partyService.agregarAventureroAParty(1,aventurero2)
        partyService.agregarAventureroAParty(1,aventurero3)
        partyService.agregarAventureroAParty(1,aventurero4)

        val requisitos1 = listOf(Requisito("Mago",5))
        val requisitos2 = listOf(Requisito("Clerigo",3),Requisito("Aventurero",2))
        val requisitos3 = listOf(Requisito("Hechicero",4),Requisito("MaestroDeArmas",1))
        val requisitos4 = listOf(Requisito("Aventurero",5),Requisito("Mago",2),Requisito("Clerigo",2))

        val stats = mutableListOf(AtributoDeFormacion("Destreza",2), AtributoDeFormacion("Fuerza",5))
        val stats2 = mutableListOf(AtributoDeFormacion("Destreza",4), AtributoDeFormacion("Inteligencia",1))

        formacionService.crearFormacion("Formacion1",requisitos1,stats)
        formacionService.crearFormacion("Formacion2",requisitos2,stats)
        formacionService.crearFormacion("Formacion3",requisitos3,stats)
        formacionService.crearFormacion("Formacion4",requisitos4,stats2)

        val atributosQueCorresponden = formacionService.atributosQueCorresponden(1)

        Assert.assertTrue(atributosQueCorresponden.isEmpty())
    }

    @Test
    fun UnaPartyConDosFormacionesLeAplicaAtributosDeFormacionASusAventureros(){
        var party = Party("Heroes","")
        partyService.crear(party)
        aventurero1.agregarProficiencias("Mago")
        aventurero3.agregarProficiencias("Mago")
        aventurero2.agregarProficiencias("Clerigo")



        val requisitos1 = listOf(Requisito("Mago",2))
        val requisitos2 = listOf(Requisito("Clerigo",3),Requisito("Aventurero",2))
        val requisitos3 = listOf(Requisito("Hechicero",4),Requisito("MaestroDeArmas",1))
        val requisitos4 = listOf(Requisito("Aventurero",4),Requisito("Mago",2),Requisito("Clerigo",1))

        val stats = mutableListOf(AtributoDeFormacion("Destreza",2), AtributoDeFormacion("Fuerza",5))
        val stats2 = mutableListOf(AtributoDeFormacion("Destreza",4), AtributoDeFormacion("Inteligencia",1))

        formacionService.crearFormacion("Formacion1",requisitos1,stats)
        formacionService.crearFormacion("Formacion2",requisitos2,stats)
        formacionService.crearFormacion("Formacion3",requisitos3,stats)
        formacionService.crearFormacion("Formacion4",requisitos4,stats2)

        partyService.agregarAventureroAParty(1,aventurero1)
        partyService.agregarAventureroAParty(1,aventurero2)
        partyService.agregarAventureroAParty(1,aventurero3)
        partyService.agregarAventureroAParty(1,aventurero4)

        val destreza6 = AtributoDeFormacion("Destreza",6)
        val fuerza5 = AtributoDeFormacion("Fuerza",5)
        val inteligencia1 = AtributoDeFormacion("Inteligencia",1)

        val aventurerosDePartyActualizado = partyService.recuperar(1).aventureros
        val aventurero1 = aventurerosDePartyActualizado[0]
        val aventurero2 = aventurerosDePartyActualizado[1]
        val aventurero3 = aventurerosDePartyActualizado[2]
        val aventurero4 = aventurerosDePartyActualizado[3]

        Assert.assertEquals(aventurero1.atributos.destreza,destreza6.cantidad)
        Assert.assertEquals(aventurero2.atributos.destreza,destreza6.cantidad)
        Assert.assertEquals(aventurero3.atributos.destreza,destreza6.cantidad)
        Assert.assertEquals(aventurero4.atributos.destreza,destreza6.cantidad)

        Assert.assertEquals(aventurero1.atributos.fuerza,fuerza5.cantidad)
        Assert.assertEquals(aventurero2.atributos.fuerza,fuerza5.cantidad)
        Assert.assertEquals(aventurero3.atributos.fuerza,fuerza5.cantidad)
        Assert.assertEquals(aventurero4.atributos.fuerza,fuerza5.cantidad)

        Assert.assertEquals(aventurero1.atributos.inteligencia,inteligencia1.cantidad)
        Assert.assertEquals(aventurero2.atributos.inteligencia,inteligencia1.cantidad)
        Assert.assertEquals(aventurero3.atributos.inteligencia,inteligencia1.cantidad)
        Assert.assertEquals(aventurero4.atributos.inteligencia,inteligencia1.cantidad)

        Assert.assertEquals(aventurero1.atributos.constitucion,0)
        Assert.assertEquals(aventurero2.atributos.constitucion,0)
        Assert.assertEquals(aventurero3.atributos.constitucion,0)
        Assert.assertEquals(aventurero4.atributos.constitucion,0)
    }

}