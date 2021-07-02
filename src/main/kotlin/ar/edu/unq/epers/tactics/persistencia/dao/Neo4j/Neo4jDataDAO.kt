package ar.edu.unq.epers.tactics.persistencia.dao.Neo4j

import ar.edu.unq.epers.tactics.persistencia.dao.DataDAO
import org.neo4j.driver.*


class Neo4jDataDAO():DataDAO{

    private val driver: Driver
    init {
        val env = System.getenv()
        val url = env.getOrDefault("NEO_URL", "bolt://localhost:7687")
        val username = env.getOrDefault("NEO_USER", "neo4j")
        val password = env.getOrDefault("NEO_PASSWORD", "root")

        driver = GraphDatabase.driver(url, AuthTokens.basic(username, password),
                Config.builder().withLogging(Logging.slf4j()).build()
        )
    }

    override fun clear() {
        return driver.session().use { session ->
            session.run("MATCH (n) DETACH DELETE n")
        }
    }

}