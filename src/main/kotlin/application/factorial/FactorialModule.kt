package application.factorial

import com.natpryce.konfig.Configuration
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.experimental.builder.factory
import org.koin.experimental.builder.single
import wsi.server.ServerStarted
import wsi.router.Handler
import wsi.websocket.WebSocketModule
import kotlin.reflect.KClass

class ServerStartedEvent: ServerStarted {
    override fun invoke() {
        println("server stated")
    }

}

class FactorialModule: WebSocketModule {
    override fun connectionPath(): String = "factorial"

    override fun dependencies(config: Configuration): Module {
        return module {
            factory<FactorialHandler>()
            factory<FactorialGuard>()
            single<FactorialCalculatedEvent>()
            factory<FactorialMiddleware>()
            single<ServerStartedEvent>() bind ServerStarted::class
        }
    }

    override fun getHandlers(): List<KClass<out Handler<out Any>>> {
        return listOf(
                FactorialHandler::class
        )
    }

    override fun onConnect(): KClass<FactorialMiddleware> = FactorialMiddleware::class
}