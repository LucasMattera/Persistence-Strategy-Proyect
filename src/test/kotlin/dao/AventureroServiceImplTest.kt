package dao

import ar.edu.unq.epers.tactics.modelo.Atributos
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongo.MongoFormacionDAO
import ar.edu.unq.epers.tactics.service.impl.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.DataServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import org.hibernate.HibernateException
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class AventureroServiceImplTest {
    lateinit var tacticas : List<Tactica>
    lateinit var  party: Party
    lateinit var  party2: Party
    lateinit var formacionDAO: FormacionDAO
    lateinit var serviceParty: PartyServiceImpl
    lateinit var serviceAventurero : AventureroServiceImpl
    lateinit var dataService : DataServiceImpl
    lateinit var spiderman: Aventurero
    lateinit var capAmerica: Aventurero
    lateinit var ironMan: Aventurero
    lateinit var thor: Aventurero
    lateinit var hulk: Aventurero
    lateinit var batman: Aventurero

    @Before
    fun setUp(){
        var partyDAO : PartyDAO = HibernatePartyDAO()
        tacticas = listOf()
        party = Party("Heroes","")
        party2= Party("Villanos","")
        //Service
        formacionDAO = MongoFormacionDAO()
        serviceParty= PartyServiceImpl(partyDAO,formacionDAO)
        serviceAventurero = AventureroServiceImpl(HibernateAventureroDAO(),partyDAO,formacionDAO)
        //Data dao
        dataService = DataServiceImpl(HibernateDataDAO())
        //Aventureros
        spiderman = Aventurero("spiderman",tacticas,"",Atributos(0,0,0,0),1)
        capAmerica = Aventurero("capAmerica",tacticas,"",Atributos(0,0,0,0),1)
        ironMan = Aventurero("ironMan",tacticas,"",Atributos(0,0,0,0),1)
        thor = Aventurero("thor",tacticas,"",Atributos(0,0,0,0),1)
        hulk = Aventurero("hulk",tacticas,"", Atributos(0,0,0,0),1)
        batman = Aventurero("batman",tacticas,"", Atributos(0,0,0,0),1)
    }

    @Test
    fun crearDatos(){
        var cantidad = 200
        while(cantidad>=0){
            var partyCreada = serviceParty.crear(Party("party$cantidad",""))
            serviceParty.agregarAventureroAParty(partyCreada.id!!,
                Aventurero("Aventurero1-$cantidad",tacticas,"",Atributos(0,0,0,0),1))

            serviceParty.agregarAventureroAParty(partyCreada.id!!,
                Aventurero("Aventurero2-$cantidad",tacticas,"",Atributos(0,0,0,0),1))

            serviceParty.agregarAventureroAParty(partyCreada.id!!,
                Aventurero("Aventurero3-$cantidad",tacticas,"",Atributos(0,0,0,0),1))

            serviceParty.agregarAventureroAParty(partyCreada.id!!,
                Aventurero("Aventurero4-$cantidad",tacticas,"",Atributos(0,0,0,0),1))

            serviceParty.agregarAventureroAParty(partyCreada.id!!,
                Aventurero("Aventurero5-$cantidad",tacticas,"",Atributos(0,0,0,0),1))

            cantidad--
        }
    }

    @Test
    fun testUnNuevoAventureroSeAgregaAUnaParty(){

        serviceParty.crear(party)
        serviceParty.agregarAventureroAParty(1,hulk)

        Assert.assertEquals(party.nombre,hulk.party!!.nombre)
    }

    @Test
    fun noSePuedeAgregarAUnAventureroDosVecesAUnaParty(){
        serviceParty.crear(party)
        serviceParty.agregarAventureroAParty(1,spiderman)
        assertThrows<Exception> { serviceParty.agregarAventureroAParty(1,spiderman) }
        Assert.assertEquals(1,serviceParty.recuperar(1).numeroDeAventureros)
    }

    @Test
    fun unAventureroNoPuedeEstarEnDosPartysDistintas(){
        serviceParty.crear(party)
        serviceParty.crear(party2)
        serviceParty.agregarAventureroAParty(1,spiderman)
        var spidermanRecuperado = serviceAventurero.recuperar(1)
        assertThrows<Exception> { serviceParty.agregarAventureroAParty(2,spidermanRecuperado) }
        Assert.assertEquals(1,serviceParty.recuperar(1).numeroDeAventureros)
        Assert.assertEquals(0,serviceParty.recuperar(2).numeroDeAventureros)
    }

    @Test
    fun unaPartyNoPuedeTenerMasDeCincoAventureros(){
        serviceParty.crear(party)
        serviceParty.agregarAventureroAParty(1,spiderman)
        serviceParty.agregarAventureroAParty(1,hulk)
        serviceParty.agregarAventureroAParty(1,thor)
        serviceParty.agregarAventureroAParty(1,capAmerica)
        serviceParty.agregarAventureroAParty(1,ironMan)
        assertThrows<Exception> {  serviceParty.agregarAventureroAParty(1,batman)}
    }

    @Test
    fun testRecuperarAventurero(){
        serviceParty.crear(party)
        serviceParty.agregarAventureroAParty(1,spiderman)
        serviceParty.agregarAventureroAParty(1,thor)
        var aventureroRecuperado = serviceAventurero.recuperar(spiderman.id!!)

        Assert.assertEquals(aventureroRecuperado.nombre,spiderman.nombre)
        Assert.assertEquals(spiderman.party!!.nombre,aventureroRecuperado.party!!.nombre)
    }

    @Test
    fun seRecuperaUnAventureroConIdInexistenteYEsNull(){
        Assert.assertEquals(serviceAventurero.recuperar(1), null)
    }

    @Test
    fun testEliminarAventurero(){
        serviceParty.crear(party)
        serviceParty.agregarAventureroAParty(1,spiderman)
        serviceParty.agregarAventureroAParty(1,thor)

        var aventureroRecuperado = serviceAventurero.recuperar(spiderman.id!!)
        serviceAventurero.eliminar(aventureroRecuperado)

        var partyRecuperada = serviceParty.recuperar(1)

        Assert.assertEquals(serviceAventurero.recuperar(1),null)
        Assert.assertEquals(partyRecuperada.numeroDeAventureros,1)
        Assert.assertEquals(partyRecuperada.aventureros.size,1)
        Assert.assertEquals(null,aventureroRecuperado.party)
    }

    @Test
    fun testRedisAsCache(){
        var cantidad : Int = 1000
        var list = mutableListOf<Aventurero>()
        serviceParty.crear(party)
        serviceParty.agregarAventureroAParty(1,spiderman)
        serviceParty.agregarAventureroAParty(1,thor)

        while(cantidad > 0){
            var nuevoAventurero1 = serviceAventurero.recuperar(spiderman.id!!)
            var nuevoAventurero2 = serviceAventurero.recuperar(thor.id!!)
            list.add(nuevoAventurero1)
            list.add(nuevoAventurero2)
            cantidad--
        }

        Assert.assertEquals(list.size,2000)
    }

    @Test
    fun noSePuedeEliminarUnAventureroInexistente(){
        assertThrows<KotlinNullPointerException> { serviceAventurero.eliminar(spiderman) }
    }


    @After
    fun clear(){

       // dataService.clear()
        // formacionDAO.deleteAll()
    }
}