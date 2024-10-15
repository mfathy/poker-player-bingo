package poker.player.kotlin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import models.GameState
import org.json.JSONObject

fun main() {
    val player = Player()
    embeddedServer(Netty, getPort()) {
        routing {
            get("/") {
                call.respondText("Hello, world!", ContentType.Text.Html)
            }
            post {
                val formParameters = call.receiveParameters()
                val result = when (val action = formParameters["action"].toString()) {
                    "bet_request" -> {
                        val gameStateStr = formParameters["game_state"]

                        if (gameStateStr == null) {
                            "Missing game_state!"
                        } else {
//                            "100"
                            val gameState = Json.decodeFromString<GameState>(gameStateStr)
                            player.betRequest(gameState).toString()
                        }
                    }

                    "showdown" -> {
                        player.showdown()
                        "OK"
                    }

                    "version" -> player.version()
                    else -> "Unknown action '$action'!"
                }

                call.respondText(result)
            }
        }
    }.start(wait = true)
}

private fun getPort(): Int {
    val port = System.getenv("PORT") ?: "8080"

    return Integer.parseInt(port)
}
