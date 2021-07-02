package ar.edu.unq.epers.tactics.persistencia.dao.Neo4j

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import org.neo4j.driver.*

class Neo4jClaseDAO : ClaseDAO {

    private val driver:Driver

    init {
        val env = System.getenv()
        val url = env.getOrDefault("NEO_URL", "bolt://localhost:7687")
        val username = env.getOrDefault("NEO_USER", "neo4j")
        val password = env.getOrDefault("NEO_PASSWORD", "root")

        driver = GraphDatabase.driver(url, AuthTokens.basic(username, password),
                Config.builder().withLogging(Logging.slf4j()).build()
        )
    }

    override fun getMejoraDe(nombreDeClase:String,nombreDeClase2: String) : Mejora{
        var mejoraObtenida : Mejora? = null
        driver.session().use{ session->
                val query = """MATCH(a:CLASE {nombreClase : ${'$'}nombreClase})-[relacion:Habilita]-(b:CLASE {nombreClase : ${'$'}nombreClase2}) 
                               RETURN relacion 
                            """
                val result = session.run(query,Values.parameters(
                        "nombreClase",nombreDeClase,
                        "nombreClase2",nombreDeClase2
                ))
                result.list{ record:Record ->
                    val relacion = record[0]
                    val atributosString = relacion["atributos"].asList() as MutableList<String>
                    val atributos = atributosString.map { atributo-> stringToAtributo(atributo)} as MutableList<Atributo>
                    val cantidad = relacion["cantidad"].asInt()
                    mejoraObtenida = Mejora(nombreDeClase,nombreDeClase2, atributos,cantidad)

            }
        }
        return mejoraObtenida!!
    }

    private fun stringToAtributo(atributoString:String):Atributo{
        lateinit var atributoACrear:Atributo
        when{
            atributoString == "INTELIGENCIA"-> atributoACrear = Atributo.INTELIGENCIA
            atributoString == "DESTREZA"-> atributoACrear = Atributo.DESTREZA
            atributoString == "CONSTITUCION"-> atributoACrear = Atributo.CONSTITUCION
            atributoString == "FUERZA"-> atributoACrear = Atributo.FUERZA
        }
        return atributoACrear
    }


    override fun crearClase(nombreDeClase: String) {
        driver.session().use { session ->

            session.writeTransaction{
                val query = "CREATE (n:CLASE {nombreClase: ${'$'}elNombre })"
                it.run(query,Values.parameters(
                        "elNombre",nombreDeClase
                ))
            }
        }
    }

    override fun getClases(): List<Clase> {
        return driver.session().use{ session ->
            val query = """
                MATCH (a:CLASE)
                RETURN a
                """
            val result = session.run(query)
            result.list{record:Record->
                val clase = record[0]
                val nombreClase = clase["nombreClase"].asString()
                Clase(nombreClase)
            }
        }
    }

    override fun getMejoras(): List<Mejora>{
        return driver.session().use{ session ->
            val query = """
                MATCH (a:CLASE)-[relacion:Habilita]->(b:CLASE)
                RETURN a,b,relacion
                """
            val result = session.run(query)
            result.list{record:Record->
                val clase1 = record[0]
                val clase2 = record[1]
                val relacion = record[2]
                val nombreClase = clase1["nombreClase"].asString()
                val nombreClaseAMejorar = clase2["nombreClase"].asString()
                val atributosString = relacion["atributos"].asList() as MutableList<String>
                val atributos = atributosString.map { atributo-> stringToAtributo(atributo)} as MutableList<Atributo>
                val cantidadDeAtributos = relacion["cantidad"].asInt()
                Mejora(nombreClase, nombreClaseAMejorar,atributos,cantidadDeAtributos)
            }
        }
    }



    override fun crearMejora(nombreDeClase: String, nombreDeClase2: String, atributos: List<Atributo>, cantidadDeAtributos: Int) {
        var atributosmap = atributos.map { atributo->atributo.toString() }
        driver.session().use { session ->

            session.writeTransaction{
                val query = "MATCH (claseAMejorar:CLASE {nombreClase: ${'$'}nombreClaseAMejorar}) " +
                                   "MATCH(claseMejora:CLASE {nombreClase: ${'$'}nombreClaseMejora}) " +
                                  "MERGE (claseAMejorar)-[relacion:Habilita {atributos:${'$'}atributos,cantidad:${'$'}cantidadAtributos}]->(claseMejora)"
                it.run(query,Values.parameters(
                        "nombreClaseAMejorar",nombreDeClase,
                        "nombreClaseMejora",nombreDeClase2,
                        "atributos",atributosmap,
                        "cantidadAtributos",cantidadDeAtributos
                ))
            }
        }
    }



    override fun requerir(nombreDeClase: String, nombreDeClase2: String) {
        driver.session().use { session ->

            session.writeTransaction{
                val query = "MATCH (claseRequiere:CLASE {nombreClase: ${'$'}nombreClaseRequiere})," +
                        "(claseARequerir:CLASE {nombreClase: ${'$'}nombreClaseARequerir}) " +
                        "CREATE (claseRequiere)-[relacion:Requiere]->(claseARequerir)"
                it.run(query,Values.parameters(
                        "nombreClaseRequiere",nombreDeClase,
                        "nombreClaseARequerir",nombreDeClase2
                ))
            }
        }
    }

    override fun getRequerirDe(nombreDeClase: String): List<Clase> {
        return driver.session().use{ session ->
            val query = """
                MATCH (a:CLASE {nombreClase: ${'$'}elNombreDeLaClase})-[relacion:Requiere]->(b:CLASE)
                RETURN b
                """
            val result = session.run(query,Values.parameters("elNombreDeLaClase",nombreDeClase))
            result.list{record:Record->
                val clase = record[0]
                val nombreClase = clase["nombreClase"].asString()
                Clase(nombreClase)
            }
        }
    }
    override fun ganarProficiencia(aventureroId: Long, nombreDeClase: String, nombreDeClase2: String): Aventurero {
        TODO("Not yet implemented")
    }

    override fun posiblesMejoras(clases: List<String>): Set<Mejora> {
        return driver.session().use{ session ->
            val query = """
                UNWIND ${'$'}clases as clasePartida
                MATCH (a:CLASE {nombreClase: clasePartida})-[relacion:Habilita]->(b:CLASE)
                WHERE not (b.nombreClase) in ${'$'}clases 
                OPTIONAL MATCH (c:CLASE)-[relacion2:Requiere]->(d:CLASE)
                WHERE (c.nombreClase = b.nombreClase AND d.nombreClase IN ${'$'}clases) or c.nombreClase <> b.nombreClase
                RETURN a,relacion,b
                """
            val result = session.run(query,Values.parameters("clases",clases))
            result.list{ record:Record->
                val clase = record[0]
                val relacion = record[1]
                val clase2 = record[2]
                val nombreClase = clase["nombreClase"].asString()
                val atributosString = relacion["atributos"].asList() as MutableList<String>
                val atributos = atributosString.map { atributo-> stringToAtributo(atributo)} as MutableList<Atributo>
                val cantidadDeAtributos = relacion["cantidad"].asInt()
                val nombreClase2 = clase2["nombreClase"].asString()
                Mejora(nombreClase,nombreClase2,atributos,cantidadDeAtributos)
            }.toSet()
        }
    }

    override fun caminoMasRentable(puntosDeExperiencia: Int,clasesDeAventurero:List<String>, atributo: String): List<Mejora> {
        return driver.session().use{ session ->
            val query = """
                CALL {
                    UNWIND ${'$'}clasesDeAventurero as clasePartida
                    MATCH p=(a:CLASE{nombreClase:clasePartida})-[relacion:Habilita*.. ${puntosDeExperiencia}]->(b:CLASE)
                    WHERE NOT (b.nombreClase) IN ${'$'}clasesDeAventurero
                    WITH nodes(p) as nodos,relationships(p) as relaciones
                    WHERE all(nodo in nodos where (nodo.nombreClase=clasePartida OR not(nodo.nombreClase) IN ${'$'}clasesDeAventurero) AND (size(
                    [(nodo)-[:Requiere]->(f:CLASE) where  not f.nombreClase  in ${'$'}clasesDeAventurero |f ])=0))
                    WITH nodos,relaciones,reduce (numero = 0, relacion in relaciones | numero + (case  when (any(x IN relacion.atributos WHERE (x= ${'$'}atributo) )) then                     relacion.cantidad else 0 END)) as total 
                    RETURN  nodos,relaciones,total
                    ORDER BY total desc
                    limit 1}
                    RETURN nodos,relaciones 
                
                """
            val result = session.run(query,Values.parameters(
                    "clasesDeAventurero",clasesDeAventurero,
                    "puntosDeExperiencia",puntosDeExperiencia,
                    "atributo",atributo
                    ))
            result.list{record:Record->
                val clases = record[0]
                val nombresDeClases:List<String> = clases.asList { clase-> clase["nombreClase"].asString()}

                val relaciones = record[1]

                val atributosDeRelacion:List<List<String>> = relaciones.asList { clase-> clase["atributos"].asList() as List<String>}

                val cantidadDeRelacion:List<Int> = relaciones.asList { clase-> clase["cantidad"].asInt()}



                generarMejoras(nombresDeClases,atributosDeRelacion,cantidadDeRelacion)
            }[0]
        }
    }

    private fun generarMejoras(nombresDeClases:List<String>,atributosDeRelacion:List<List<String>>,cantidadDeRelacion:List<Int>):List<Mejora>{
        var mejoras = mutableListOf<Mejora>()
        var indice = 0
        while (indice<(nombresDeClases.size-1)){
            val nombreClaseAMejorar = nombresDeClases[indice]
            val nombreClaseMejora = nombresDeClases[indice+1]
            val atributosDeMejora = atributosDeRelacion[indice].map { string->stringToAtributo(string) }.toMutableList()
            val cantidadDeMejora = cantidadDeRelacion[indice]

            val mejoraNueva = Mejora(nombreClaseAMejorar,nombreClaseMejora,atributosDeMejora,cantidadDeMejora)
            mejoras.add(mejoraNueva)
            indice++
        }

        return mejoras.toList()
    }


}