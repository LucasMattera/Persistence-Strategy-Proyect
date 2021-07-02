package dao

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongo.MongoFormacionDAO
import ar.edu.unq.epers.tactics.service.dto.Accion
import ar.edu.unq.epers.tactics.service.dto.Criterio
import ar.edu.unq.epers.tactics.service.dto.TipoDeEstadistica
import ar.edu.unq.epers.tactics.service.dto.TipoDeReceptor
import ar.edu.unq.epers.tactics.service.impl.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AventureroLeaderboardServiceImplTest {
    lateinit var tacticas1: MutableList<Tactica>
    lateinit var tacticas2: MutableList<Tactica>
    lateinit var tacticas3: MutableList<Tactica>
    lateinit var party: Party
    lateinit var party2: Party
    lateinit var formacionDAO: FormacionDAO
    lateinit var serviceParty: PartyServiceImpl
    lateinit var serviceAventurero: AventureroServiceImpl
    lateinit var dataService: DataServiceImpl
    lateinit var servicePelea: PeleaServiceImpl
    lateinit var spiderman: Aventurero
    lateinit var capAmerica: Aventurero
    lateinit var ironMan: Aventurero
    lateinit var thor: Aventurero
    lateinit var hulk: Aventurero
    lateinit var batman: Aventurero
    lateinit var serviceLeaderboard: AventureroLeaderboardServiceImpl

    @Before
    fun setUp() {
        var partyDAO: PartyDAO = HibernatePartyDAO()
        var aventureroDAO: AventureroDAO = HibernateAventureroDAO()
        tacticas1 = mutableListOf()
        tacticas2 = mutableListOf()
        tacticas3 = mutableListOf()
        var tactica1 =Tactica(null,1,TipoDeReceptor.ALIADO,TipoDeEstadistica.MANA,Criterio.MAYOR_QUE,5,Accion.CURAR)
        var tactica2 =Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MENOR_QUE,50,Accion.ATAQUE_FISICO)
        var tactica3 =Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO)
        tacticas1.add(tactica1)
        tacticas2.add(tactica2)
        tacticas3.add(tactica3)
        party = Party("Heroes", "")
        party2 = Party("Villanos", "")
        //Service
        formacionDAO = MongoFormacionDAO()
        serviceParty = PartyServiceImpl(partyDAO,formacionDAO)
        serviceAventurero = AventureroServiceImpl(aventureroDAO, partyDAO,formacionDAO)
        serviceLeaderboard = AventureroLeaderboardServiceImpl(aventureroDAO)
        servicePelea = PeleaServiceImpl(HibernatePeleaDAO(), partyDAO, aventureroDAO)
        //Data dao
        dataService = DataServiceImpl(HibernateDataDAO())
        //Aventureros
        spiderman = Aventurero("spiderman", tacticas2, "", Atributos(50, 50, 50, 50), 1)
        capAmerica = Aventurero("capAmerica", tacticas1, "", Atributos(60, 60, 100, 60), 1)
        batman = Aventurero("batman",tacticas3,"", Atributos(24,24,1,62),1)
    }

    @Test
    fun elUnicoAventureroQueHayEsElMejorCurandero(){
        serviceParty.crear(party)
        var aventur1 = serviceParty.agregarAventureroAParty(1,spiderman)
        Assert.assertEquals(serviceLeaderboard.mejorCurandero().nombre,aventur1.nombre)
    }

    @Test
    fun elUnicoAventureroQueHayEsElMejorGuerrero(){
        serviceParty.crear(party)
        var aventur1 = serviceParty.agregarAventureroAParty(1,spiderman)
        Assert.assertEquals(serviceLeaderboard.mejorGuerrero().nombre,aventur1.nombre)
    }

    @Test
    fun elUnicoAventureroQueHayEsBuda() {
        serviceParty.crear(party)
        var aventur1 = serviceParty.agregarAventureroAParty(1,spiderman)
        Assert.assertEquals(serviceLeaderboard.buda().nombre,aventur1.nombre)
    }

    @Test
    fun elMejorCuranderoUnico(){
        serviceParty.crear(party)
        var partyEnemiga = serviceParty.crear(party2)
        var aventur1 = serviceParty.agregarAventureroAParty(1,spiderman)
        var aventur2 = serviceParty.agregarAventureroAParty(2,capAmerica)
        var aventur3 = serviceParty.agregarAventureroAParty(2,batman)

        //pelea 1, party 1, spiderman
        // pelea 2, party 2, capamerica y batman

        val pelea = servicePelea.iniciarPelea(1,partyEnemiga.nombre)
        val pelea2 = servicePelea.iniciarPelea(2,party.nombre)

        var habilidad : Habilidad = servicePelea.resolverTurno(pelea.id!!,aventur1.id!!,aventur3.party!!.aventureros)
        // spiderman crea ataque hacia batman

       servicePelea.recibirHabilidad(2,3,habilidad)
        //spiderman ataca a batman

        var habilidad2 : Habilidad = servicePelea.resolverTurno(pelea2.id!!,aventur2.id!!,aventur1.party!!.aventureros)
        // cap america crea curacion

        servicePelea.recibirHabilidad(2,3,habilidad2)
        // cap america cura a batman

        Assert.assertEquals(serviceLeaderboard.mejorCurandero().nombre,aventur2.nombre)
    }

    @Test
    fun elQueMasDanioMagicoHizo(){
        var tacticas1Avent1 = mutableListOf<Tactica>()
        var tacticas2Avent2 = mutableListOf<Tactica>()
        var tacticaAvent1 =Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_MAGICO)
        var tactica2Avent2 =Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_MAGICO)
        tacticas1Avent1.add(tacticaAvent1)
        tacticas2Avent2.add(tactica2Avent2)

        ironMan = Aventurero("ironman", tacticas1Avent1, "", Atributos(20, 1, 50, 30), 10)
        thor = Aventurero("thor", tacticas2Avent2, "", Atributos(60, 1, 100, 10), 10)


        serviceParty.crear(party)
        var partyEnemiga = serviceParty.crear(party2)
        var aventur1 = serviceParty.agregarAventureroAParty(1,ironMan)
        var aventur2 = serviceParty.agregarAventureroAParty(2,thor)

        val pelea = servicePelea.iniciarPelea(1,partyEnemiga.nombre)
        val pelea2 = servicePelea.iniciarPelea(2,party.nombre)


        var habilidadDeIronman : Habilidad = servicePelea.resolverTurno(pelea.id!!,aventur1.id!!,aventur2.party!!.aventureros)
        servicePelea.recibirHabilidad(2,2,habilidadDeIronman)

        var habilidadDeThor : Habilidad = servicePelea.resolverTurno(pelea2.id!!,aventur2.id!!,aventur1.party!!.aventureros)

        servicePelea.recibirHabilidad(2,1,habilidadDeThor)

        Assert.assertEquals(serviceLeaderboard.mejorMago().nombre,aventur1.nombre)
    }

    @Test
    fun elMejorGuerreroUnico() {
        serviceParty.crear(party)
        var partyEnemiga = serviceParty.crear(party2)
        var aventur1 = serviceParty.agregarAventureroAParty(1, spiderman)
        var aventur2 = serviceParty.agregarAventureroAParty(2, capAmerica)
        var aventur3 = serviceParty.agregarAventureroAParty(2, batman)

        val pelea = servicePelea.iniciarPelea(1,partyEnemiga.nombre)
        val pelea2 = servicePelea.iniciarPelea(2,party.nombre)

        var habilidad : Habilidad = servicePelea.resolverTurno(pelea.id!!,aventur1.id!!,aventur3.party!!.aventureros)

        servicePelea.recibirHabilidad(2,3,habilidad)

        Assert.assertEquals(serviceLeaderboard.mejorGuerrero().nombre,aventur1.nombre)
    }

    @Test
    fun elMejorBudaUnico() {
        serviceParty.crear(party)
        var partyEnemiga = serviceParty.crear(party2)
        var aventur1 = serviceParty.agregarAventureroAParty(1, spiderman)
        var aventur2 = serviceParty.agregarAventureroAParty(2, capAmerica)
        var aventur3 = serviceParty.agregarAventureroAParty(2, batman)

        val pelea = servicePelea.iniciarPelea(1,partyEnemiga.nombre)
        val pelea2 = servicePelea.iniciarPelea(2,party.nombre)

        var habilidad : Habilidad = servicePelea.resolverTurno(pelea.id!!,aventur1.id!!,aventur3.party!!.aventureros)

        servicePelea.recibirHabilidad(2,3,habilidad)

        Assert.assertEquals(serviceLeaderboard.buda().nombre,aventur1.nombre)
    }

    @After
    fun clear(){
        dataService.clear()
        formacionDAO.deleteAll()
    }
}

