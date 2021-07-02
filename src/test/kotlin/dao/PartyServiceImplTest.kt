package dao

import ar.edu.unq.epers.tactics.modelo.Atributos
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongo.MongoFormacionDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.impl.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.DataServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PeleaServiceImpl
import org.hibernate.HibernateException
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class PartyServiceImplTest {

    lateinit var tacticas : List<Tactica>
    lateinit var  party: Party
    lateinit var  party2: Party
    lateinit var formacionDAO: FormacionDAO
    lateinit var serviceParty: PartyServiceImpl
    lateinit var dataService : DataServiceImpl
    lateinit var spiderman: Aventurero
    lateinit var capAmerica: Aventurero
    lateinit var ironMan: Aventurero
    lateinit var thor: Aventurero
    lateinit var peleaService: PeleaServiceImpl

    // dato importante: cuando se inicializa en el before tiene que estar en el
    // mismo orden o rompe

    @Before
    fun setUp(){
        tacticas = listOf()
        //Partys

        party = Party("Heroes","")
        party2= Party("Villanos","")

        //Service
        formacionDAO = MongoFormacionDAO()
        serviceParty= PartyServiceImpl(HibernatePartyDAO(),formacionDAO)



        //Data dao
        dataService = DataServiceImpl(HibernateDataDAO())
        //Aventureros
        spiderman = Aventurero("spiderman",tacticas,"", Atributos(0,0,0,0),1)
        capAmerica = Aventurero("capAmerica",tacticas,"",Atributos(0,0,0,0),1)
        ironMan = Aventurero("ironMan",tacticas,"",Atributos(0,0,0,0),1)
        thor = Aventurero("thor",tacticas,"",Atributos(0,0,0,0),1)

        peleaService= PeleaServiceImpl(HibernatePeleaDAO(),HibernatePartyDAO(),HibernateAventureroDAO())
    }

    @Test
    fun partyQueGanoMasPeleasAsc(){
        serviceParty.crear(party)
        serviceParty.crear(party2)

        serviceParty.agregarAventureroAParty(1,spiderman)
        serviceParty.agregarAventureroAParty(2,thor)

        peleaService.iniciarPelea(1,"primera pelea que gano party 1")
        peleaService.terminarPelea(1)

        peleaService.iniciarPelea(1,"segunda pelea que gano party 1")
        peleaService.terminarPelea(2)

        peleaService.iniciarPelea(1,"tercera pelea que gano party 1")
        peleaService.terminarPelea(3)

        peleaService.iniciarPelea(2,"primera pelea que gano party 2")
        peleaService.terminarPelea(4)

        peleaService.iniciarPelea(2,"segunda pelea que gano party 2")
        peleaService.terminarPelea(5)

        val partysOrdenadas = serviceParty.recuperarOrdenadas(Orden.VICTORIAS,Direccion.ASCENDENTE,1)

        Assert.assertEquals(partysOrdenadas.total , 2)
        Assert.assertEquals(partysOrdenadas.parties[0].nombre, party2.nombre)

    }

    @Test
    fun partyQueGanoMasPeleasDesc(){
        serviceParty.crear(party)
        serviceParty.crear(party2)

        serviceParty.agregarAventureroAParty(1,spiderman)
        serviceParty.agregarAventureroAParty(2,thor)

        peleaService.iniciarPelea(1,"primera pelea que gano party 1")
        peleaService.terminarPelea(1)

        peleaService.iniciarPelea(1,"segunda pelea que gano party 1")
        peleaService.terminarPelea(2)

        peleaService.iniciarPelea(1,"tercera pelea que gano party 1")
        peleaService.terminarPelea(3)

        peleaService.iniciarPelea(2,"primera pelea que gano party 2")
        peleaService.terminarPelea(4)

        peleaService.iniciarPelea(2,"segunda pelea que gano party 2")
        peleaService.terminarPelea(5)

        val partysOrdenadas = serviceParty.recuperarOrdenadas(Orden.VICTORIAS,Direccion.DESCENDENTE,1)

        Assert.assertEquals(partysOrdenadas.total , 2)
        Assert.assertEquals(partysOrdenadas.parties[0].nombre, party.nombre)

    }

    @Test
    fun partyQuePerdioMasPeleasDesc(){
        serviceParty.crear(party)
        serviceParty.crear(party2)

        spiderman.vida = 0
        spiderman.vidaMax = 0
        thor.vida = 0
        thor.vidaMax = 0

        serviceParty.agregarAventureroAParty(1,spiderman)
        serviceParty.agregarAventureroAParty(2,thor)

        peleaService.iniciarPelea(1,"primera pelea que perdio party 1")
        peleaService.terminarPelea(1)

        peleaService.iniciarPelea(1,"segunda pelea que perdio party 1")
        peleaService.terminarPelea(2)

        peleaService.iniciarPelea(1,"tercera pelea que perdio party 1")
        peleaService.terminarPelea(3)

        peleaService.iniciarPelea(2,"primera pelea que perdio party 2")
        peleaService.terminarPelea(4)

        peleaService.iniciarPelea(2,"segunda pelea que perdio party 2")
        peleaService.terminarPelea(5)

        val partysOrdenadas = serviceParty.recuperarOrdenadas(Orden.DERROTAS,Direccion.DESCENDENTE,1)

        Assert.assertEquals(partysOrdenadas.total , 2)
        Assert.assertEquals(partysOrdenadas.parties[0].nombre, party.nombre)

    }

    @Test
    fun partyQuePerdioMasPeleasAsc(){
        serviceParty.crear(party)
        serviceParty.crear(party2)

        spiderman.vida = 0
        spiderman.vidaMax = 0
        thor.vida = 0
        thor.vidaMax = 0

        serviceParty.agregarAventureroAParty(1,spiderman)
        serviceParty.agregarAventureroAParty(2,thor)

        peleaService.iniciarPelea(1,"primera pelea que perdio party 1")
        peleaService.terminarPelea(1)

        peleaService.iniciarPelea(1,"segunda pelea que perdio party 1")
        peleaService.terminarPelea(2)

        peleaService.iniciarPelea(1,"tercera pelea que perdio party 1")
        peleaService.terminarPelea(3)

        peleaService.iniciarPelea(2,"primera pelea que perdio party 2")
        peleaService.terminarPelea(4)

        peleaService.iniciarPelea(2,"segunda pelea que perdio party 2")
        peleaService.terminarPelea(5)

        val partysOrdenadas = serviceParty.recuperarOrdenadas(Orden.DERROTAS,Direccion.ASCENDENTE,1)

        Assert.assertEquals(partysOrdenadas.total , 2)
        Assert.assertEquals(partysOrdenadas.parties[0].nombre, party2.nombre)

    }

    @Test
    fun testPartiesOrdenadasPorPoderAsc(){
        var aventurero1 = Aventurero("spiderman",tacticas,"", Atributos(10,10,10,10),1)
        var aventurero2 = Aventurero("capAmerica",tacticas,"",Atributos(10,10,10,10),1)
        var aventurero3 = Aventurero("ironMan",tacticas,"",Atributos(20,20,20,20),1)
        var aventurero4 = Aventurero("thor",tacticas,"",Atributos(30,30,30,30),1)

        serviceParty.crear(party)
        serviceParty.crear(party2)

        serviceParty.agregarAventureroAParty(1,aventurero1)
        serviceParty.agregarAventureroAParty(1,aventurero2)
        serviceParty.agregarAventureroAParty(2,aventurero3)
        serviceParty.agregarAventureroAParty(2,aventurero4)

        val partysOrdenadas = serviceParty.recuperarOrdenadas(Orden.PODER,Direccion.ASCENDENTE,1)

        Assert.assertEquals(partysOrdenadas.total , 2)
        Assert.assertEquals(partysOrdenadas.parties[0].nombre, party.nombre)

    }

    @Test
    fun testPartiesOrdenadasPorPoderDesc(){
        var aventurero1 = Aventurero("spiderman",tacticas,"", Atributos(10,10,10,10),1)
        var aventurero2 = Aventurero("capAmerica",tacticas,"",Atributos(10,10,10,10),1)
        var aventurero3 = Aventurero("ironMan",tacticas,"",Atributos(20,20,20,20),1)
        var aventurero4 = Aventurero("thor",tacticas,"",Atributos(30,30,30,30),1)

        serviceParty.crear(party)
        serviceParty.crear(party2)

        serviceParty.agregarAventureroAParty(1,aventurero1)
        serviceParty.agregarAventureroAParty(1,aventurero2)
        serviceParty.agregarAventureroAParty(2,aventurero3)
        serviceParty.agregarAventureroAParty(2,aventurero4)

        val partysOrdenadas = serviceParty.recuperarOrdenadas(Orden.PODER,Direccion.DESCENDENTE,1)

        Assert.assertEquals(partysOrdenadas.total , 2)
        Assert.assertEquals(partysOrdenadas.parties[0].nombre, party2.nombre)

    }

    @Test
    fun crearParty(){
        Assert.assertEquals(party,serviceParty.crear(party))
        Assert.assertEquals(party2,serviceParty.crear(party2))

    }

    @Test
    fun recuperarParty(){
        serviceParty.crear(party)
        val partyRecuperada = serviceParty.recuperar(1)
        Assert.assertEquals(partyRecuperada.numeroDeAventureros,party.numeroDeAventureros)
        Assert.assertEquals(party.nombre,partyRecuperada.nombre)

    }

    @Test
    fun seRecuperaUnaPartyNoCreadaYEsNull(){
        Assert.assertEquals(serviceParty.recuperar(1) ,null)

    }

    @Test
    fun actualizarParty(){
        serviceParty.crear(party)
        serviceParty.crear(party2)
        party.numeroDeAventureros ++
        serviceParty.actualizar(party)
        val partyActualizada = serviceParty.recuperar(1)
        Assert.assertEquals(1,partyActualizada.numeroDeAventureros)

    }

    @Test
    fun noSePuedeActualizarPartyNoExistente(){
        assertThrows<HibernateException> { serviceParty.actualizar(party)}
    }

    @Test
    fun recuperoTodasLasPartys(){
        serviceParty.crear(party)
        serviceParty.crear(party2)
        val partysPersistidas = serviceParty.recuperarTodas()
        val nombresDePartysPersistidas = partysPersistidas.map {party -> party.nombre}
        Assert.assertEquals(2,partysPersistidas.size)
        Assert.assertTrue(nombresDePartysPersistidas.contains("Heroes"))
        Assert.assertTrue(nombresDePartysPersistidas.contains("Villanos"))
    }
    @Test
    fun seAgregan4AventurerosYSePersisten(){
        val aventureroHibernate = HibernateAventureroDAO()
        val serviceDeAventurero = AventureroServiceImpl(aventureroHibernate,serviceParty.partyDAO,formacionDAO)


        serviceParty.crear(party)
        serviceParty.crear(party2)
        serviceParty.agregarAventureroAParty(2,spiderman)
        serviceParty.agregarAventureroAParty(2,capAmerica)
        serviceParty.agregarAventureroAParty(1,ironMan)
        serviceParty.agregarAventureroAParty(2,thor)
        // contemplar el caso que se quiera agregar un aventurero que ya existe
        Assert.assertEquals(1,serviceParty.recuperar(1).numeroDeAventureros)
        Assert.assertEquals(3,serviceParty.recuperar(2).numeroDeAventureros)

    }

    @Test
    fun noHayPartysAsiQueSeRecuperaUnaListaVacia(){
        Assert.assertEquals(serviceParty.recuperarOrdenadas(Orden.VICTORIAS,Direccion.DESCENDENTE,1).total,0)
        Assert.assertTrue(serviceParty.recuperarOrdenadas(Orden.VICTORIAS,Direccion.DESCENDENTE,1).parties.isEmpty())
    }

    @Test
    fun sePideUnaPaginaInvalida(){
        assertThrows<Exception> { serviceParty.recuperarOrdenadas(Orden.VICTORIAS,Direccion.DESCENDENTE,-20)}
    }

    @After
    fun clear(){
            //dataService.clear()
            //formacionDAO.deleteAll()

    }

}