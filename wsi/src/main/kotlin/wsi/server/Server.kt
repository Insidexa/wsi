package wsi.server

import com.natpryce.konfig.Configuration
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.stringType
import io.ktor.application.*
import io.ktor.html.respondHtml
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.websocket.WebSockets
import org.koin.core.KoinApplication
import org.slf4j.LoggerFactory
import wsi.extensions.registerWSModule
import wsi.router.CommandRouter
import wsi.websocket.WebSocketModule
import java.util.concurrent.TimeUnit

class Server(
        private val modules: List<WebSocketModule>,
        private val config: Configuration,
        private val router: CommandRouter,
        private val koinApp: KoinApplication
) {
    private val logger = LoggerFactory.getLogger("wsi.${Server::class.simpleName}")

    private val SERVER_PORT = Key("SERVER_PORT", intType)
    private val SERVER_HOST = Key("SERVER_HOST", stringType)
    private val IDLE_TIMEOUT = Key("IDLE_TIMEOUT", intType)

    suspend fun start() {
        val onServerStarted = koinApp.koin.getOrNull<ServerStarted>()
        val onServerStarting = koinApp.koin.getOrNull<ServerStarting>()
        val onServerStopping = koinApp.koin.getOrNull<ServerStopping>()
        val onServerStopped = koinApp.koin.getOrNull<ServerStopped>()
        val server = embeddedServer(
                host = config[SERVER_HOST],
                factory = CIO,
                port = config[SERVER_PORT],
                configure = {
                    connectionIdleTimeoutSeconds = config[IDLE_TIMEOUT]
                }) {

            environment.monitor.subscribe(ApplicationStarted) {
                onServerStarted?.let { onServerStarted.invoke() }
            }
            environment.monitor.subscribe(ApplicationStarting) {
                onServerStarting?.let { onServerStarting.invoke() }
            }
            environment.monitor.subscribe(ApplicationStopping) {
                onServerStopping?.let { onServerStopping.invoke() }
            }
            environment.monitor.subscribe(ApplicationStopped) {
                println("stooped")
                onServerStopped?.let { onServerStopped.invoke() }
            }

            install(WebSockets)

            routing {
                get("/") { call.respondHtml {  } }

                modules.forEach { module ->
                    registerWSModule(module, koinApp.koin)
                    router.registerModuleHandlers(module)
                    logger.info("Module {} registered on path \"/{}\"", module::class.simpleName, module.connectionPath())
                }
            }
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            koinApp.close()
            server.stop(1, 5)
        })
        server.start(wait = true)
    }
}