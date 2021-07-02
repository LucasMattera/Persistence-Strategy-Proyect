package dao

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.modelo.exception.*
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.persistencia.dao.DataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.Neo4j.Neo4jClaseDAO
import ar.edu.unq.epers.tactics.persistencia.dao.Neo4j.Neo4jDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongo.MongoFormacionDAO
import ar.edu.unq.epers.tactics.service.AventureroService
import ar.edu.unq.epers.tactics.service.ClaseService
import ar.edu.unq.epers.tactics.service.DataService
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.impl.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.ClaseServiceImpl
import ar.edu.unq.epers.tactics.service.impl.DataServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class ClaseServiceImplTest{


    lateinit var claseDAO:ClaseDAO
    lateinit var aventureroDAO: AventureroDAO
    lateinit var formacionDAO: MongoFormacionDAO
    lateinit var claseService:ClaseService
    lateinit var neo4jDataDAO: DataDAO
    lateinit var partyDAO:PartyDAO
    lateinit var dataDAO:DataDAO
    lateinit var hibernateserviceData: DataService
    lateinit var neo4JserviceData: DataService
    lateinit var servicePartyDAO: PartyService
    lateinit var atributosAMejorar : MutableList<Atributo>
    lateinit var serviceAventureroDAO:AventureroService

    @Before
    fun setUp(){
        claseDAO = Neo4jClaseDAO()
        aventureroDAO = HibernateAventureroDAO()
        formacionDAO = MongoFormacionDAO()
        claseService = ClaseServiceImpl(claseDAO,aventureroDAO,formacionDAO)

        neo4jDataDAO = Neo4jDataDAO()

        partyDAO = HibernatePartyDAO()

        dataDAO = HibernateDataDAO()

        hibernateserviceData = DataServiceImpl(dataDAO)
        neo4JserviceData = DataServiceImpl(neo4jDataDAO)
        atributosAMejorar = mutableListOf()
        servicePartyDAO = PartyServiceImpl(partyDAO,formacionDAO)
        serviceAventureroDAO = AventureroServiceImpl(aventureroDAO,partyDAO,formacionDAO)

    }


    @Test
    fun crearClaseGuerreroyLuchador(){
        claseService.crearClase("Guerrero")
        claseService.crearClase("Luchador")
        val clasesCreadas = claseService.getClases()

        val nombresDeClasesCreadas = clasesCreadas.map { clase -> clase.nombreClase  }

        Assert.assertTrue(nombresDeClasesCreadas.contains("Guerrero"))
        Assert.assertTrue(nombresDeClasesCreadas.contains("Luchador"))
        Assert.assertEquals(clasesCreadas.size,2)
    }

    @Test
    fun crearClaseGuerreroyLuchadorYClaseMagoNoExiste(){
        claseService.crearClase("Guerrero")
        claseService.crearClase("Luchador")
        val clasesCreadas = claseService.getClases()

        val nombresDeClasesCreadas = clasesCreadas.map { clase -> clase.nombreClase  }



        Assert.assertTrue(nombresDeClasesCreadas.contains("Guerrero"))
        Assert.assertTrue(nombresDeClasesCreadas.contains("Luchador"))
        Assert.assertFalse(nombresDeClasesCreadas.contains("Mago"))
        Assert.assertEquals(clasesCreadas.size,2)
    }

    @Test
    fun noSeCrearonClases(){
        val clasesCreadas = claseService.getClases()
        val nombresDeClasesCreadas = clasesCreadas.map { clase -> clase.nombreClase  }

        Assert.assertEquals(clasesCreadas.size,0)
        Assert.assertFalse(nombresDeClasesCreadas.contains("Guerrero"))
        Assert.assertFalse(nombresDeClasesCreadas.contains("Luchador"))
        Assert.assertFalse(nombresDeClasesCreadas.contains("Mago"))
    }

    @Test
    fun LuchadorRequiereGuerrero(){
        claseService.crearClase("Guerrero")
        claseService.crearClase("Luchador")

        claseService.requerir("Luchador","Guerrero")

        val clasesRequeridasPorLuchador = claseService.getRequerirDe("Luchador")

        val nombreDeClasesRequeridasPorLuchador = clasesRequeridasPorLuchador.map { clase -> clase.nombreClase}

        Assert.assertEquals(clasesRequeridasPorLuchador.size,1)
        Assert.assertTrue(nombreDeClasesRequeridasPorLuchador.contains("Guerrero"))
    }

    @Test
    fun paladinRequiereClerigoYGuerrero(){
        claseService.crearClase("Luchador")
        claseService.crearClase("Guerrero")
        claseService.crearClase("Paladin")
        claseService.crearClase("Clerigo")

        claseService.requerir("Paladin","Clerigo")
        claseService.requerir("Paladin","Guerrero")

        val clasesRequeridasPorLuchador = claseService.getRequerirDe("Paladin")

        val nombreDeClasesRequeridasPorLuchador = clasesRequeridasPorLuchador.map { clase -> clase.nombreClase}

        Assert.assertEquals(clasesRequeridasPorLuchador.size,2)
        Assert.assertTrue(nombreDeClasesRequeridasPorLuchador.contains("Guerrero"))
        Assert.assertTrue(nombreDeClasesRequeridasPorLuchador.contains("Clerigo"))
        Assert.assertFalse(nombreDeClasesRequeridasPorLuchador.contains("Luchador"))
    }

    @Test
    fun LuchadorNoRequiereNada(){
        claseService.crearClase("Luchador")
        claseService.crearClase("Guerrero")
        claseService.crearClase("Paladin")
        claseService.crearClase("Clerigo")

        claseService.requerir("Paladin","Clerigo")
        claseService.requerir("Paladin","Guerrero")

        val clasesRequeridasPorLuchador = claseService.getRequerirDe("Luchador")

        val nombreDeClasesRequeridasPorLuchador = clasesRequeridasPorLuchador.map { clase -> clase.nombreClase}

        Assert.assertEquals(clasesRequeridasPorLuchador.size,0)
        Assert.assertFalse(nombreDeClasesRequeridasPorLuchador.contains("Guerrero"))
        Assert.assertFalse(nombreDeClasesRequeridasPorLuchador.contains("Clerigo"))
        Assert.assertFalse(nombreDeClasesRequeridasPorLuchador.contains("Paladin"))
    }

    @Test
    fun puedeMejorarAClerigoTieneHabilitaSinRequerido(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.sumarPuntoYLvl()

        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Clerigo")

        claseService.crearMejora("Aventurero","Clerigo",listOf<Atributo>(),3)

        var mejora = Mejora("Aventurero","Clerigo",mutableListOf<Atributo>(),3)

        Assert.assertTrue(claseService.puedeMejorar(1,mejora))
    }

    @Test
    fun aplicarMejoraClerigo(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)
        atributosAMejorar.add(Atributo.INTELIGENCIA)
        atributosAMejorar.add(Atributo.DESTREZA)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.sumarPuntoYLvl()
        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Clerigo")
        claseService.crearMejora("Aventurero","Clerigo",atributosAMejorar,3)

        claseService.ganarProficiencia(1,"Aventurero","Clerigo")

        val aventureroActualizado = serviceAventureroDAO.recuperar(1)

        Assert.assertEquals(aventureroActualizado.atributos.inteligencia,3)
        Assert.assertEquals(aventureroActualizado.atributos.fuerza,0)
        Assert.assertEquals(aventureroActualizado.atributos.constitucion,0)
        Assert.assertEquals(aventureroActualizado.atributos.destreza,3)
    }

    @Test
    fun noSePuedeAplicarMejoraClerigoSiNoCumpleLosRequerimientos(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)
        atributosAMejorar.add(Atributo.INTELIGENCIA)
        atributosAMejorar.add(Atributo.DESTREZA)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.sumarPuntoYLvl()
        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Mago")
        claseService.crearClase("Clerigo")
        claseService.crearMejora("Aventurero","Clerigo",atributosAMejorar,3)
        claseService.requerir("Clerigo","Mago")

        assertThrows<CantImprove> { claseService.ganarProficiencia(1,"Aventurero","Clerigo") }

        val aventureroActualizado = serviceAventureroDAO.recuperar(1)

        Assert.assertEquals(aventureroActualizado.atributos.inteligencia,0)
        Assert.assertEquals(aventureroActualizado.atributos.fuerza,0)
        Assert.assertEquals(aventureroActualizado.atributos.constitucion,0)
        Assert.assertEquals(aventureroActualizado.atributos.destreza,0)
    }

    @Test
    fun noSePuedeAplicarMejoraClerigoPorFaltaDeExperiencia(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)
        atributosAMejorar.add(Atributo.INTELIGENCIA)
        atributosAMejorar.add(Atributo.DESTREZA)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)

        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Clerigo")
        claseService.crearClase("Druida")

        claseService.requerir("Clerigo","Druida")
        claseService.crearMejora("Aventurero","Clerigo",atributosAMejorar,3)

        assertThrows<CantImprove> { claseService.ganarProficiencia(1,"Aventurero","Clerigo") }

        val aventureroActualizado = serviceAventureroDAO.recuperar(1)

        Assert.assertEquals(aventureroActualizado.atributos.inteligencia,0)
        Assert.assertEquals(aventureroActualizado.atributos.fuerza,0)
        Assert.assertEquals(aventureroActualizado.atributos.constitucion,0)
        Assert.assertEquals(aventureroActualizado.atributos.destreza,0)
    }

    @Test
    fun aplicarMejoraClerigoYLeHabilitaPaladin(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)
        atributosAMejorar.add(Atributo.INTELIGENCIA)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.sumarPuntoYLvl()
        spiderman.sumarPuntoYLvl()
        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Clerigo")
        claseService.crearClase("Paladin")
        claseService.crearMejora("Aventurero","Clerigo",atributosAMejorar,3)

        claseService.ganarProficiencia(1,"Aventurero","Clerigo")
        claseService.crearMejora("Clerigo","Paladin",atributosAMejorar,3)

        var mejora = Mejora("Clerigo","Paladin",mutableListOf<Atributo>(),3)

        val aventureroActualizado = serviceAventureroDAO.recuperar(1)
        Assert.assertTrue(claseService.puedeMejorar(1,mejora))
        Assert.assertEquals(aventureroActualizado.atributos.inteligencia,3)
        Assert.assertEquals(aventureroActualizado.atributos.fuerza,0)
        Assert.assertEquals(aventureroActualizado.atributos.constitucion,0)
        Assert.assertEquals(aventureroActualizado.atributos.destreza,0)
    }

    @Test
    fun PuedeMejorarAClerigoHabilitaYMagoRequerido(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.proficiencias.add(Clase("Mago"))
        spiderman.sumarPuntoYLvl()

        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Clerigo")
        claseService.crearClase("Mago")

        claseService.crearMejora("Aventurero","Clerigo",listOf<Atributo>(),3)
        claseService.requerir("Clerigo","Mago")


        var mejora = Mejora("Aventurero","Clerigo",mutableListOf<Atributo>(),3)

        Assert.assertTrue(claseService.puedeMejorar(1,mejora))
    }
    @Test
    fun noPuedeMejorarAClerigoLeFaltaRequerido(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.proficiencias.add(Clase("Constructor"))
        spiderman.proficiencias.add(Clase("Tanque"))
        spiderman.sumarPuntoYLvl()

        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Clerigo")
        claseService.crearClase("Mago")

        claseService.crearMejora("Aventurero","Clerigo",listOf<Atributo>(),3)
        claseService.requerir("Clerigo","Mago")


        var mejora = Mejora("Aventurero","Clerigo",mutableListOf<Atributo>(),3)

        Assert.assertFalse(claseService.puedeMejorar(1,mejora))
    }

    @Test
    fun noPuedeMejorarAClerigoLeFaltaElQueHabilita(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.proficiencias.add(Clase("Constructor"))
        spiderman.proficiencias.add(Clase("Mago"))
        spiderman.sumarPuntoYLvl()

        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Quimico")
        claseService.crearClase("Clerigo")
        claseService.crearClase("Mago")
        claseService.crearClase("Constructor")

        claseService.crearMejora("Quimico","Clerigo",listOf<Atributo>(),3)
        claseService.requerir("Clerigo","Mago")


        var mejora = Mejora("Quimico","Clerigo",mutableListOf<Atributo>(),3)

        Assert.assertFalse(claseService.puedeMejorar(1,mejora))
    }

    @Test
    fun noPuedeMejorarAClerigoLeFaltaPuntoDeExp(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.proficiencias.add(Clase("Constructor"))
        spiderman.proficiencias.add(Clase("Mago"))
        spiderman.proficiencias.add(Clase("Quimico"))


        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Quimico")
        claseService.crearClase("Clerigo")
        claseService.crearClase("Mago")
        claseService.crearClase("Constructor")

        claseService.crearMejora("Quimico","Clerigo",listOf<Atributo>(),3)
        claseService.requerir("Clerigo","Mago")


        var mejora = Mejora("Quimico","Clerigo",mutableListOf<Atributo>(),3)

        Assert.assertFalse(claseService.puedeMejorar(1,mejora))
    }

    @Test
    fun noTienePosiblesMejoras(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.puntosDeExperiencia++
        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")

        Assert.assertEquals(claseService.posiblesMejoras(1).size, 0)
    }

    @Test
    fun tieneDosPosiblesMejorasConRequiere(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.sumarPuntoYLvl()
        spiderman.proficiencias.add(Clase("Mago"))
        spiderman.proficiencias.add(Clase("Quimico"))


        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Quimico")
        claseService.crearClase("Clerigo")
        claseService.crearClase("Mago")
        claseService.crearClase("Constructor")

        claseService.crearMejora("Quimico","Clerigo",listOf<Atributo>(),3)
        claseService.requerir("Clerigo","Mago")
        claseService.crearMejora("Aventurero","Constructor",listOf<Atributo>(),3)


        var mejora = Mejora("Quimico","Clerigo",mutableListOf<Atributo>(),3)
        var mejora2 = Mejora("Aventurero","Constructor",mutableListOf<Atributo>(),3)

        var posiblesMejorasRecuperadas = claseService.posiblesMejoras(1)

        Assert.assertEquals(posiblesMejorasRecuperadas.size, 2)
        Assert.assertTrue(contieneMejora(posiblesMejorasRecuperadas,mejora))
        Assert.assertTrue(contieneMejora(posiblesMejorasRecuperadas,mejora2))
    }

    @Test
    fun tieneDosPosiblesMejorasSinRequiere(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.sumarPuntoYLvl()

        spiderman.proficiencias.add(Clase("Quimico"))


        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Quimico")
        claseService.crearClase("Clerigo")
        claseService.crearClase("Mago")
        claseService.crearClase("Constructor")
        claseService.crearMejora("Quimico","Clerigo",listOf<Atributo>(),3)
        claseService.crearMejora("Aventurero","Constructor",listOf<Atributo>(),3)
        var mejora = Mejora("Quimico","Clerigo",mutableListOf<Atributo>(),3)
        var mejora2 = Mejora("Aventurero","Constructor",mutableListOf<Atributo>(),3)

        var posiblesMejorasRecuperadas = claseService.posiblesMejoras(1)

        Assert.assertEquals(posiblesMejorasRecuperadas.size, 2)
        Assert.assertTrue(contieneMejora(posiblesMejorasRecuperadas,mejora))
        Assert.assertTrue(contieneMejora(posiblesMejorasRecuperadas,mejora2))
    }

    @Test
    fun tieneUnaPosibleMejora(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.sumarPuntoYLvl()
        spiderman.proficiencias.add(Clase("Constructor"))
        spiderman.proficiencias.add(Clase("Quimico"))
        spiderman.proficiencias.add(Clase("Mago"))



        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Quimico")
        claseService.crearClase("Clerigo")
        claseService.crearClase("Mago")
        claseService.crearClase("Constructor")

        claseService.crearMejora("Quimico","Clerigo",listOf<Atributo>(),3)
        claseService.requerir("Clerigo","Mago")
        claseService.crearMejora("Aventurero","Constructor",listOf<Atributo>(),3)

        var mejora = Mejora("Quimico","Clerigo",mutableListOf<Atributo>(),3)

        var posiblesMejoras = claseService.posiblesMejoras(1)


        Assert.assertEquals(posiblesMejoras.size, 1)
        Assert.assertTrue(compararMejoras(mejora,posiblesMejoras.first()))
    }

    @Test
    fun caminoMasRentable(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.sumarPuntoYLvl()
        spiderman.sumarPuntoYLvl()


        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Magico")
        claseService.crearClase("Fisico")
        claseService.crearClase("Clerigo")
        claseService.crearClase("Mago")

        claseService.crearMejora("Magico","Fisico",listOf<Atributo>(Atributo.FUERZA),4)
        claseService.crearMejora("Fisico","Clerigo",listOf<Atributo>(Atributo.FUERZA),2)
        claseService.crearMejora("Aventurero","Magico",listOf<Atributo>(Atributo.DESTREZA),4)
        claseService.crearMejora("Aventurero","Fisico",listOf<Atributo>(Atributo.DESTREZA),2)
        claseService.crearMejora("Magico","Clerigo",listOf<Atributo>(Atributo.DESTREZA),1)
        claseService.crearMejora("Magico","Mago",listOf<Atributo>(Atributo.DESTREZA),10)

        val mejora1 = Mejora("Aventurero","Magico", mutableListOf(Atributo.DESTREZA),4)
        val mejora2 = Mejora("Magico","Mago", mutableListOf(Atributo.DESTREZA),10)

        val masRentable = claseService.caminoMasRentable(5,1,Atributo.DESTREZA)

        Assert.assertTrue(compararMejoras(mejora1,masRentable.elementAt(0)))
        Assert.assertTrue(compararMejoras(mejora2,masRentable.elementAt(1)))

    }
    @Test
    fun caminoMasRentableDeFuerza(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.sumarPuntoYLvl()
        spiderman.sumarPuntoYLvl()
        spiderman.proficiencias.add(Clase("Magico"))


        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Magico")
        claseService.crearClase("Fisico")
        claseService.crearClase("Clerigo")
        claseService.crearClase("Mago")

        claseService.crearMejora("Magico","Fisico",listOf<Atributo>(Atributo.FUERZA),4)
        claseService.crearMejora("Fisico","Clerigo",listOf<Atributo>(Atributo.FUERZA),2)
        claseService.crearMejora("Aventurero","Magico",listOf<Atributo>(Atributo.DESTREZA),4)
        claseService.crearMejora("Aventurero","Fisico",listOf<Atributo>(Atributo.DESTREZA),2)
        claseService.crearMejora("Magico","Clerigo",listOf<Atributo>(Atributo.DESTREZA),1)
        claseService.crearMejora("Magico","Mago",listOf<Atributo>(Atributo.DESTREZA),10)

        val mejora1 = Mejora("Magico","Fisico", mutableListOf(Atributo.FUERZA),4)

        val masRentable = claseService.caminoMasRentable(5,1,Atributo.FUERZA)

        Assert.assertTrue(compararMejoras(mejora1,masRentable.elementAt(0)))

    }
    @Test
    fun caminoMasRentableConRequerir(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.sumarPuntoYLvl()
        spiderman.sumarPuntoYLvl()


        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Magico")
        claseService.crearClase("Fisico")
        claseService.crearClase("Clerigo")
        claseService.crearClase("Mago")

        claseService.requerir("Mago","Clerigo")

        claseService.crearMejora("Magico","Fisico",listOf<Atributo>(Atributo.FUERZA),4)
        claseService.crearMejora("Fisico","Clerigo",listOf<Atributo>(Atributo.FUERZA),2)
        claseService.crearMejora("Aventurero","Magico",listOf<Atributo>(Atributo.DESTREZA),4)
        claseService.crearMejora("Aventurero","Fisico",listOf<Atributo>(Atributo.DESTREZA),2)
        claseService.crearMejora("Magico","Clerigo",listOf<Atributo>(Atributo.DESTREZA),10)
        claseService.crearMejora("Magico","Mago",listOf<Atributo>(Atributo.DESTREZA),20)

        val mejora1 = Mejora("Aventurero","Magico", mutableListOf(Atributo.DESTREZA),4)
        val mejora2 = Mejora("Magico","Clerigo", mutableListOf(Atributo.DESTREZA),10)

        val masRentable = claseService.caminoMasRentable(5,1,Atributo.DESTREZA)

        Assert.assertTrue(contieneMejora(masRentable.toSet(),mejora1))
        Assert.assertTrue(contieneMejora(masRentable.toSet(),mejora2))
    }
    @Test
    fun caminoMasRentableMuchaExperiencia(){
        var party = Party("Heroes","")
        servicePartyDAO.crear(party)

        var tacticas = listOf<Tactica>()
        var spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        spiderman.sumarPuntoYLvl()
        spiderman.sumarPuntoYLvl()


        servicePartyDAO.agregarAventureroAParty(1,spiderman)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Magico")
        claseService.crearClase("Fisico")
        claseService.crearClase("Clerigo")
        claseService.crearClase("Mago")
        claseService.crearClase("Paladin")
        claseService.crearClase("MagoOscuro")


        claseService.crearMejora("Magico","Fisico",listOf<Atributo>(Atributo.FUERZA),4)
        claseService.crearMejora("Fisico","Clerigo",listOf<Atributo>(Atributo.FUERZA),2)
        claseService.crearMejora("Aventurero","Magico",listOf<Atributo>(Atributo.DESTREZA),4)
        claseService.crearMejora("Aventurero","Fisico",listOf<Atributo>(Atributo.DESTREZA),2)
        claseService.crearMejora("Magico","Clerigo",listOf<Atributo>(Atributo.DESTREZA),10)
        claseService.crearMejora("Magico","Mago",listOf<Atributo>(Atributo.DESTREZA),20)
        claseService.crearMejora("Mago","Paladin",listOf<Atributo>(Atributo.FUERZA),1)
        claseService.crearMejora("Paladin","MagoOscuro",listOf<Atributo>(Atributo.DESTREZA),2)

        val mejora1 = Mejora("Aventurero","Magico", mutableListOf(Atributo.DESTREZA),4)
        val mejora2 = Mejora("Magico","Mago", mutableListOf(Atributo.DESTREZA),20)
        val mejora3 = Mejora("Mago","Paladin", mutableListOf(Atributo.FUERZA),1)
        val mejora4 = Mejora("Paladin","MagoOscuro", mutableListOf(Atributo.DESTREZA),2)

        val masRentable = claseService.caminoMasRentable(5,1,Atributo.DESTREZA)

        Assert.assertTrue(compararMejoras(mejora1,masRentable.elementAt(0)))
        Assert.assertTrue(compararMejoras(mejora2,masRentable.elementAt(1)))
        Assert.assertTrue(compararMejoras(mejora3,masRentable.elementAt(2)))
        Assert.assertTrue(compararMejoras(mejora4,masRentable.elementAt(3)))
    }



    @After
    fun clear(){
       neo4JserviceData.clear()
       hibernateserviceData.clear()
       formacionDAO.deleteAll()
    }

    private fun compararMejoras(mejora1:Mejora,mejora2:Mejora) : Boolean{
        return mejora1.nombreClaseAMejorar == mejora2.nombreClaseAMejorar &&
                mejora1.nombreClaseMejora == mejora2.nombreClaseMejora &&
                mejora1.cantidad == mejora2.cantidad
    }

    private fun contieneMejora(mejoras:Set<Mejora>,mejoraABuscar:Mejora):Boolean{
        var existe = false
        for (mejora in mejoras){
            existe = existe || mejora.equals(mejoraABuscar)
        }
        return existe
    }
}