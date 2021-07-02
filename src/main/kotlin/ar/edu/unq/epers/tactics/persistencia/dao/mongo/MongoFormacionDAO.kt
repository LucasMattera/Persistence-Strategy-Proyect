package ar.edu.unq.epers.tactics.persistencia.dao.mongo

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Accumulators
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.Projections
import com.mongodb.internal.operation.MapReduceToCollectionOperation
import jdk.nashorn.internal.parser.JSONParser
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.types.ObjectId


class MongoFormacionDAO: FormacionDAO,GenericMongoDAO<Formacion>(Formacion::class.java) {
    override fun crearFormacion(nombreFormacion: String, requerimientos: List<Requisito>,stats: List<AtributoDeFormacion>): Formacion {
        val formacion=Formacion(nombreFormacion,requerimientos,stats)
        save(formacion)
        return formacion
    }

    override fun todasLasFormaciones(): List<Formacion> {
        return collection.find().into(mutableListOf())
    }


    override fun formacionesQuePosee(party : Party): List<Formacion> {
        var mapaDeReq = party.armarClasesDeAventureros()
        val mapToJson = toJson(mapaDeReq.toString())

        var map =   """
                    function(){
                        var reqs =  $mapToJson;
                        if(this.requisitos.every(function(value){
                        return reqs[value.nombreClase] >= value.cantidad;
                        }))emit(this._id,this)};
                    """

        var reduce = """function(n,formaciones){return formaciones};"""

        var resultMap = collection.mapReduce(map,reduce,Map::class.java)

        var resultToList = resultMap.into(mutableListOf())

        var getValuesToResult = resultToList.map { elem -> elem["value"] } as List<List<Document>>

        var valuesAplanado = getValuesToResult.map { elem -> elem[0] }

        return transformarAFormaciones(valuesAplanado)

    }

    override fun atributosQueCorresponden(party : Party): List<AtributoDeFormacion> {
        val list = formacionesQuePosee(party).map { elem -> elem.nombre!! }

        val match = Aggregates.match(`in`("nombre",list))

        val unwind = Aggregates.unwind("\$atributos")

        val project = Aggregates.project(Projections.fields(Projections.include("atributos")))
        val group = Aggregates.group("\$atributos._id", Accumulators.sum("cantidad", "\$atributos.cantidad"))



        return aggregate(listOf(match,unwind,project,group),AtributoDeFormacion::class.java)
    }


    private fun transformarAFormaciones(list:List<Document>):List<Formacion>{
        var resultado = mutableListOf<Formacion>()
        for (elem in list){
            var nombreFormacion = elem["nombre"] as String
            var atributosDeFormacion = documentsToAtributosDeFormacion(elem["atributos"] as List<Document>)
            var requisitos =  documentsToRequisito(elem["requisitos"] as List<Document>)
            var formacionNueva = Formacion(nombreFormacion,requisitos,atributosDeFormacion)
            formacionNueva.setearIdFormacion(elem["_id"] as ObjectId)
            resultado.add(formacionNueva)
        }

        return resultado
    }

    private fun documentsToAtributosDeFormacion(list: List<Document>):MutableList<AtributoDeFormacion>{
        var resultado = mutableListOf<AtributoDeFormacion>()
        for (document in list){
            resultado.add(AtributoDeFormacion(document["_id"] as String,document["cantidad"] as Int))
        }
        return resultado
    }

    private fun documentsToRequisito(list: List<Document>):MutableList<Requisito>{
        var resultado = mutableListOf<Requisito>()
        for (document in list){
            resultado.add(Requisito(document["nombreClase"] as String,document["cantidad"] as Int))
        }
        return resultado
    }


    override fun getBy(property:String, value:String?) : Formacion {
         return super.getBy(property,value)!!
    }

    override fun agregarANuevoAventureroAtributosviejosYActualizarParty(party: Party,aventureroNuevo:Aventurero):Party{
        var atributosViejos = party.atributosDeFormaciones
        aventureroNuevo.aplicarAtributos(atributosViejos)
        return actualizarParty(party)

    }
    override fun actualizarParty(party : Party) : Party {
        var atributosQueCorresponden = atributosQueCorresponden(party)
        var atributosViejos = party.atributosDeFormaciones
        var atributosParaAplicar = atributosParaAplicar(atributosQueCorresponden,atributosViejos)
        party.setearAtributosDeFormacion(atributosQueCorresponden)
        party.aplicarAtributosDeFormacion(atributosParaAplicar)
        return party
    }

    private fun atributosParaAplicar(atributosQueCorresponden:List<AtributoDeFormacion>,
                                     atributosViejos:List<AtributoDeFormacion>) : List<AtributoDeFormacion> {
       var atributosNuevos : List<AtributoDeFormacion> = mutableListOf()
       atributosViejos.forEach { atributoV ->
           atributosQueCorresponden.forEach { atributoN ->
               if(atributoN.id == atributoV.id){
                   var atributoNuevo = AtributoDeFormacion(atributoN.id!!,atributoN.restarCantidad(atributoV.cantidad!!))
                   atributosNuevos += atributoNuevo
               }
           }
       }
       return atributosNuevos
    }

    private fun toJson(valor:String):String{
        return valor.replace("=",":")
    }
}