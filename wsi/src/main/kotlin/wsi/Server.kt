package wsi

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
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.slf4j.LoggerFactory
import wsi.extensions.registerWSModule
import wsi.router.CommandRouter
import wsi.websocket.WebSocketModule
import java.util.concurrent.TimeUnit

class Server(private val modules: List<WebSocketModule>) : KoinComponent {
    private val config: Configuration by inject()
    private val router: CommandRouter by inject()
    private val koinInstance = getKoin()
    private val logger = LoggerFactory.getLogger("wsi.${Server::class.simpleName}")

    private val SERVER_PORT = Key("SERVER_PORT", intType)
    private val SERVER_HOST = Key("SERVER_HOST", stringType)
    private val IDLE_TIMEOUT = Key("IDLE_TIMEOUT", intType)

    suspend fun start() {
        val koin = getKoin()
        val onServerStarted = koin.getOrNull<ServerStarted>()
        val onServerStarting = koin.getOrNull<ServerStarting>()
        val onServerStopping = koin.getOrNull<ServerStopping>()
        val onServerStopped = koin.getOrNull<ServerStopped>()
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
                onServerStopped?.let { onServerStopped.invoke() }
            }

            install(WebSockets)

            routing {
                get("/") { call.respondHtml {  } }

                modules.forEach { module ->
                    registerWSModule(module, koinInstance)
                    router.registerModuleHandlers(module)
                    logger.info("Module {} registered", module::class.simpleName)
                }
            }
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            server.stop(1, 5, TimeUnit.SECONDS)
        })
        server.start(wait = true)
    }
}