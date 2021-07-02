package dao

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.modelo.exception.IsFighting
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.*
import ar.edu.unq.epers.tactics.persistencia.dao.mongo.MongoFormacionDAO
import ar.edu.unq.epers.tactics.service.dto.Accion
import ar.edu.unq.epers.tactics.service.dto.Criterio
import ar.edu.unq.epers.tactics.service.dto.TipoDeEstadistica
import ar.edu.unq.epers.tactics.service.dto.TipoDeReceptor
import ar.edu.unq.epers.tactics.service.impl.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.DataServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PeleaServiceImpl
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class PeleaServiceImplTest {

    lateinit var  party: Party
    lateinit var  party2 : Party
    lateinit var aventurero : Aventurero
    lateinit var aventurero2 : Aventurero
    lateinit var formacionDAO: MongoFormacionDAO
    lateinit var serviceAventurero: AventureroServiceImpl
    lateinit var serviceParty: PartyServiceImpl
    lateinit var servicePelea: PeleaServiceImpl
    lateinit var dataService : DataServiceImpl
    lateinit var habilidadGenerada : Ataque
    lateinit var habilidadGenerada2 : AtaqueMagico

    @Before
    fun setup() {
        var tacticas = listOf<Tactica>()
        var tacticas2 = listOf<Tactica>()
        var tactica1 = Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO)
        var tactica3 = Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MENOR_QUE,100,Accion.ATAQUE_MAGICO)

        tacticas+= tactica1
        tacticas2+= tactica3

        party = Party("Heroes", "")
        party2 = Party("Villanos","")
        aventurero = Aventurero("Hulk",tacticas,"", Atributos(10,10,10,10))
        aventurero2 = Aventurero("Thanos",tacticas2,"", Atributos(10,1,10,10))
        //Service
        formacionDAO = MongoFormacionDAO()
        serviceParty = PartyServiceImpl(HibernatePartyDAO(),formacionDAO)
        serviceAventurero = AventureroServiceImpl(HibernateAventureroDAO(),HibernatePartyDAO(),formacionDAO)
        //Data dao
        dataService = DataServiceImpl(HibernateDataDAO())
        servicePelea = PeleaServiceImpl(HibernatePeleaDAO(), HibernatePartyDAO(), HibernateAventureroDAO())
    }

    @Test
    fun alCrearUnaPartyNoEstaEnPelea(){
        serviceParty.crear(party)
        party = serviceParty.recuperar(1)

        Assert.assertFalse(servicePelea.estaEnPelea(party.id!!))
    }

    @Test
    fun alEjecutarIniciarPeleaLaPartyEntraEnUnaPelea() {
        serviceParty.crear(party)
        servicePelea.iniciarPelea(party.id!!,party2.nombre)
        party = serviceParty.recuperar(1)

        Assert.assertTrue(servicePelea.estaEnPelea(party.id!!))
    }

    @Test
    fun alIniciarUnaPeleaEstaSePersisteEnlaBaseDeDatos() {
        serviceParty.crear(party)
        val pelea = servicePelea.iniciarPelea(party.id!!,party2.nombre)

        Assert.assertNotNull(servicePelea.recuperar(pelea.id!!))
    }

    @Test
    fun alPonerAPelearUnaPartyDosVecesSeLanzaUnaExcecion() {
        serviceParty.crear(party)
        servicePelea.iniciarPelea(party.id!!,party2.nombre)

        assertThrows<IsFighting> { servicePelea.iniciarPelea(party.id!!,party2.nombre) }
    }

    @Test
    fun iniciaYSeTerminaUnaPelea(){
        serviceParty.crear(party)
        serviceParty.agregarAventureroAParty(party.id!!,aventurero)
        serviceParty.agregarAventureroAParty(party.id!!,aventurero2)


        val pelea = servicePelea.iniciarPelea(party.id!!,party2.nombre)
        party = serviceParty.recuperar(1)

        aventurero.vida = 0
        aventurero2.vida= 0

        aventurero.mana = 0
        aventurero2.mana=0

        serviceAventurero.actualizar(aventurero)
        serviceAventurero.actualizar(aventurero2)

        val estadoDePartyAntesDeTerminarla = party.estaEnPelea
        servicePelea.terminarPelea(pelea.id!!)

        val aventureroDespuesDeTerminarPelea = serviceAventurero.recuperar(aventurero.id!!)
        val aventurero2DespuesDeTerminarPelea = serviceAventurero.recuperar(aventurero2.id!!)

        Assert.assertFalse(servicePelea.estaEnPelea(party.id!!))
        Assert.assertTrue(estadoDePartyAntesDeTerminarla)

        Assert.assertEquals(aventureroDespuesDeTerminarPelea.vida,aventureroDespuesDeTerminarPelea.vidaMax)
        Assert.assertEquals(aventurero2DespuesDeTerminarPelea.vida,aventurero2DespuesDeTerminarPelea.vidaMax)

        Assert.assertEquals(aventureroDespuesDeTerminarPelea.mana,aventureroDespuesDeTerminarPelea.manaMax)
        Assert.assertEquals(aventurero2DespuesDeTerminarPelea.mana,aventurero2DespuesDeTerminarPelea.manaMax)
    }

    @Test
    fun resolverTurnoAtaqueFisico(){
        serviceParty.crear(party)
        var partyEnemiga = serviceParty.crear(party2)
        var aventur1 = serviceParty.agregarAventureroAParty(1,aventurero)
        var aventur2 = serviceParty.agregarAventureroAParty(2,aventurero2)

        val pelea = servicePelea.iniciarPelea(1,partyEnemiga.nombre)

        var habilidad : Habilidad = servicePelea.resolverTurno(pelea.id!!,aventur1.id!!,aventur2.party!!.aventureros)
        var habilidadParaTestear = habilidad
        habilidadParaTestear = habilidadParaTestear as Ataque
        habilidadGenerada = Ataque("ATAQUE_FISICO",aventurero.estadisticas.ataqueFisico().toDouble(),aventurero.estadisticas.precisionFisica().toDouble(),aventur2)

        servicePelea.recibirHabilidad(1,2,habilidad)

        var aventureroRecuperado = serviceAventurero.recuperar(2)

        Assert.assertEquals(habilidadParaTestear.tipo,habilidadGenerada.tipo)
        Assert.assertEquals(habilidadParaTestear.daño,habilidadGenerada.daño,0.0)
        Assert.assertEquals(habilidadParaTestear.precisionFisica,habilidadGenerada.precisionFisica,0.0)
    }

    @Test
    fun resolverTurnoAtaqueMagico(){
        var partyEnemiga = serviceParty.crear(party)
        serviceParty.crear(party2)
        var aventur1 = serviceParty.agregarAventureroAParty(1,aventurero)
        var aventur2 = serviceParty.agregarAventureroAParty(2,aventurero2)

        val pelea = servicePelea.iniciarPelea(2,partyEnemiga.nombre)

        var habilidad : Habilidad = servicePelea.resolverTurno(pelea.id!!,aventur2.id!!,aventur1.party!!.aventureros)
        var habilidadParaTestear = habilidad

        habilidadParaTestear = habilidadParaTestear as AtaqueMagico
        habilidadGenerada2 = AtaqueMagico("ATAQUE_MAGICO",aventur2.estadisticas.poderMagico().toDouble(),aventur2.nivel,aventur1)

        servicePelea.recibirHabilidad(1,1,habilidad)

        Assert.assertEquals(habilidadParaTestear.tipo,habilidadGenerada2.tipo)
        Assert.assertEquals(habilidadParaTestear.poderMagico,habilidadGenerada2.poderMagico,0.0)
        Assert.assertEquals(habilidadParaTestear.sourceLevel,habilidadGenerada2.sourceLevel)
    }

    @Test
    fun testRecibirHabilidadAtaqueFisico(){
        var randomProvider = RandomValue(true,100)
        serviceParty.crear(party)
        var partyEnemiga = serviceParty.crear(party2)
        var aventur1 = serviceParty.agregarAventureroAParty(1,aventurero)
        var aventur2 = serviceParty.agregarAventureroAParty(2,aventurero2)

        val pelea = servicePelea.iniciarPelea(1,partyEnemiga.nombre)


        var habilidadGenerada : Habilidad = Ataque("ATAQUE_FISICO",aventurero.estadisticas.ataqueFisico().toDouble(),aventurero.estadisticas.precisionFisica().toDouble(),aventur2,randomProvider)

        servicePelea.recibirHabilidad(1,2,habilidadGenerada)

        var aventureroRecuperado = serviceAventurero.recuperar(2)

        Assert.assertEquals(aventureroRecuperado.vidaMax,35)
        Assert.assertEquals(aventureroRecuperado.vida,19)
    }

    @Test
    fun testRecibirHabilidadAtaqueMagico(){
        var randomProvider = RandomValue(true,100)
        serviceParty.crear(party)
        var partyEnemiga = serviceParty.crear(party2)
        var aventur1 = serviceParty.agregarAventureroAParty(1,aventurero)
        var aventur2 = serviceParty.agregarAventureroAParty(2,aventurero2)

        val pelea = servicePelea.iniciarPelea(1,partyEnemiga.nombre)


        var habilidadGenerada : Habilidad = AtaqueMagico("ATAQUE_MAGICO",aventur2.estadisticas.poderMagico().toDouble(),aventur2.nivel,aventur1,randomProvider)

        servicePelea.recibirHabilidad(1,1,habilidadGenerada)

        var aventureroAtacado = serviceAventurero.recuperar(1)
        var aventureroEmisor = serviceAventurero.recuperar(2)

        Assert.assertEquals(aventureroAtacado.vidaMax,35)
        Assert.assertEquals(aventureroAtacado.vida,24)

    }

    @Test
    fun noHayPeleasAsiQueSeRecuperaUnaListaVacia(){
        serviceParty.crear(party)
        Assert.assertEquals(servicePelea.recuperarOrdenadas(1,1).total,0)
        Assert.assertTrue(servicePelea.recuperarOrdenadas(1,1).peleas.isEmpty())
    }

    @Test
    fun sePideUnaPaginaInvalida(){
        serviceParty.crear(party)
        assertThrows<Exception> { servicePelea.recuperarOrdenadas(1,-20)}
    }

    @Test
    fun unaPartyCreaDosPeleasYSeOrdenanPorFecha(){
        serviceParty.crear(party)
        var partyEnemiga = serviceParty.crear(party2)

        servicePelea.iniciarPelea(1,partyEnemiga.nombre)
        servicePelea.terminarPelea(1)
        servicePelea.iniciarPelea(1,partyEnemiga.nombre)
        servicePelea.terminarPelea(1)

        Assert.assertEquals(servicePelea.recuperarOrdenadas(1,1).total,2)
        Assert.assertEquals(servicePelea.recuperarOrdenadas(1,1).peleas[0].date, servicePelea.recuperar(2).date)

    }

    @Test
    fun unaPartyHace20PeleasYSePideLaSegundaPagina(){
        serviceParty.crear(party)
        var partyEnemiga = serviceParty.crear(party2)
        var i = 1
        while (i<= 20){
            servicePelea.iniciarPelea(1,partyEnemiga.nombre)
            servicePelea.terminarPelea(i.toLong())
            i++
        }
        Assert.assertEquals(servicePelea.recuperarOrdenadas(1,2).total,20)
        Assert.assertEquals(servicePelea.recuperarOrdenadas(1,1).peleas[0].date, servicePelea.recuperar(i.toLong().minus(1)).date)
    }

    @Test
    fun unaPartyHace20PeleasYSePideLaTerceraPaginaDaErrorAlNoExistir(){
        serviceParty.crear(party)
        var partyEnemiga = serviceParty.crear(party2)
        var i = 1
        while (i<= 20){
            servicePelea.iniciarPelea(1,partyEnemiga.nombre)
            servicePelea.terminarPelea(i.toLong())
            i++
        }
        assertThrows<Exception> { servicePelea.recuperarOrdenadas(1,3)}
    }

    @Test
    fun cargarDatos(){
        var contador = 5000
        var contador2 = 5000
        var posicion = 0




        var partys1 = mutableListOf<Party>()
        var partys2 = mutableListOf<Party>()



        while(contador > 0){


            var atributos1 = Atributos(50,5,20,5)
            var atributos2 = Atributos(50,5,20,5)
            var atributos3 = Atributos(50,5,20,5)
            var atributos4 = Atributos(50,5,20,5)
            var atributos5 = Atributos(50,5,20,5)

            var tacticas1 = listOf<Tactica>(Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO),Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO))
            var tacticas2 = listOf<Tactica>(Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO),Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO))
            var tacticas3 = listOf<Tactica>(Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO),Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO))
            var tacticas4 = listOf<Tactica>(Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO),Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO))
            var tacticas5 = listOf<Tactica>(Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO),Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO))

            var nuevaParty = serviceParty.crear(Party("PartyAliada${contador}",""))
            var nuevoAventurero = Aventurero("Aventurero1 de party${nuevaParty.nombre}"
                                             ,tacticas1,"",atributos1)
            var nuevoAventurero2 = Aventurero("Aventurero2 de party${nuevaParty.nombre}"
                ,tacticas2,"",atributos2)
            var nuevoAventurero3 = Aventurero("Aventurero3 de party${nuevaParty.nombre}"
                ,tacticas3,"",atributos3)
            var nuevoAventurero4 = Aventurero("Aventurero4 de party${nuevaParty.nombre}"
                ,tacticas4,"",atributos4)
            var nuevoAventurero5 = Aventurero("Aventurero5 de party${nuevaParty.nombre}"
                ,tacticas5,"",atributos5)

            serviceParty.agregarAventureroAParty(nuevaParty.id!!,nuevoAventurero)
            serviceParty.agregarAventureroAParty(nuevaParty.id!!,nuevoAventurero2)
            serviceParty.agregarAventureroAParty(nuevaParty.id!!,nuevoAventurero3)
            serviceParty.agregarAventureroAParty(nuevaParty.id!!,nuevoAventurero4)
            serviceParty.agregarAventureroAParty(nuevaParty.id!!,nuevoAventurero5)

            nuevaParty = serviceParty.recuperar(nuevaParty.id!!)
            partys1.add(nuevaParty)
            contador--
        }

        while(contador2 > 0){

            var atributos1 = Atributos(1,2,96,1)
            var atributos2 = Atributos(1,2,96,1)
            var atributos3 = Atributos(1,2,96,1)
            var atributos4 = Atributos(1,2,96,1)
            var atributos5 = Atributos(1,2,96,1)

            var tacticas1 = listOf<Tactica>(Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO),Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO))
            var tacticas2 = listOf<Tactica>(Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO),Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO))
            var tacticas3 = listOf<Tactica>(Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO),Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO))
            var tacticas4 = listOf<Tactica>(Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO),Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO))
            var tacticas5 = listOf<Tactica>(Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO),Tactica(null,1,TipoDeReceptor.ENEMIGO,TipoDeEstadistica.VIDA,Criterio.MAYOR_QUE,0,Accion.ATAQUE_FISICO))


            var nuevaParty = serviceParty.crear(Party("PartyEnemiga${contador2}",""))
            var nuevoAventurero = Aventurero("Aventurero1 de party${nuevaParty.nombre}"
                ,tacticas1,"",atributos1)
            var nuevoAventurero2 = Aventurero("Aventurero2 de party${nuevaParty.nombre}"
                ,tacticas2,"",atributos2)
            var nuevoAventurero3 = Aventurero("Aventurero3 de party${nuevaParty.nombre}"
                ,tacticas3,"",atributos3)
            var nuevoAventurero4 = Aventurero("Aventurero4 de party${nuevaParty.nombre}"
                ,tacticas4,"",atributos4)
            var nuevoAventurero5 = Aventurero("Aventurero5 de party${nuevaParty.nombre}"
                ,tacticas5,"",atributos5)

            serviceParty.agregarAventureroAParty(nuevaParty.id!!,nuevoAventurero)
            serviceParty.agregarAventureroAParty(nuevaParty.id!!,nuevoAventurero2)
            serviceParty.agregarAventureroAParty(nuevaParty.id!!,nuevoAventurero3)
            serviceParty.agregarAventureroAParty(nuevaParty.id!!,nuevoAventurero4)
            serviceParty.agregarAventureroAParty(nuevaParty.id!!,nuevoAventurero5)

            nuevaParty = serviceParty.recuperar(nuevaParty.id!!)
            partys2.add(nuevaParty)
            contador2--
        }

        while(posicion < 5000){
            var posicionAv = 0
            var partyAliada = partys1[posicion]
            var partyEnemiga = partys2[posicion]

            var pelea = servicePelea.iniciarPelea(partyAliada.id!!,partyEnemiga.nombre)
            var aventureros = partyAliada.aventureros

            while(posicionAv < 5){
                var aventureroAliado = partyAliada.aventureros[posicionAv]
                var aventureroEnemigo = partyEnemiga.aventureros[posicionAv]
                var habilidad = servicePelea.resolverTurno(pelea.id!!,aventureroAliado.id!!,partyEnemiga.aventureros)

                servicePelea.recibirHabilidad(pelea.id!!,aventureroEnemigo.id!!,habilidad)
                posicionAv++
            }
            posicion++
        }
    }


    @Test
    fun pruebaRedisRecuperarTodas(){
        //val partyPersistida = serviceParty.crear(Party("Aventurero",""))
        //val partyPersistida2 = serviceParty.crear(Party("Heroes",""))

        val partys = serviceParty.recuperarTodas()


        Assert.assertEquals(partys.size,2000)

    }

    @After
    fun deletaAll(){
        //dataService.clear()
        //formacionDAO.deleteAll()
    }
}