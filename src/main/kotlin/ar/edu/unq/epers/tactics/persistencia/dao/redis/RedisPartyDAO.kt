package ar.edu.unq.epers.tactics.persistencia.dao.redis

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas
import org.redisson.Redisson
import org.redisson.api.RMap
import org.redisson.api.RedissonClient
import org.redisson.config.Config

class RedisPartyDAO {

    private var client:RedissonClient? = null
    private var mapParty:RMap<String,Party>? = null
    private var mapRecuperarTodas:RMap<String,List<Party>>? = null
    private var mapRecuperarOrdenadas:RMap<String,PartyPaginadas>? = null

    init {
        val config:Config = Config()

        config.useSingleServer().address = "redis://127.0.0.1:6379"
        this.client = Redisson.create(config)
        mapParty = client!!.getMap("party")
        mapRecuperarTodas = client!!.getMap("recuperarTodas")
        mapRecuperarOrdenadas = client!!.getMap("recuperarOrdenadas")
    }

    fun insertarParty(party: Party){
        mapParty!!.set("${party.id!!}", party)!!

    }

    fun getParty(id:Long):Party?{
        return mapParty!!["$id"]
    }

    fun existsParty(id:Long):Boolean{
        return getParty(id) != null
    }

    fun estanCacheadasTodasLasPartys():Boolean{
        return mapRecuperarTodas!!["1"] != null
    }

    fun recuperarTodas():List<Party>{
        return mapRecuperarTodas!!["1"]!!
    }

    fun insertPartys(partys:List<Party>){
        mapRecuperarTodas!!["1"] = partys
    }

    fun estaCacheadoPartysOrdenadas(orden: Orden, direccion: Direccion, pagina: Int?):Boolean{
        return recuperarOrdenadas(orden,direccion,pagina) != null
    }
    fun insertOrdenadas(orden: Orden, direccion: Direccion, pagina: Int?,ordenadas:PartyPaginadas){
        mapRecuperarOrdenadas!!["$orden-$direccion-${pagina}"] = ordenadas
    }

    fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina: Int?):PartyPaginadas?{
        return mapRecuperarOrdenadas!!["$orden-$direccion-${pagina}"]
    }
}