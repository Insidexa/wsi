package application.pusher

import com.natpryce.konfig.Configuration
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.experimental.builder.factory
import wsi.router.Handler
import wsi.websocket.WebSocketModule
import kotlin.reflect.KClass

class PusherModule: WebSocketModule {
    override fun connectionPath(): String = "pusher"

    override fun dependencies(config: Configuration): Module {
        return module {
            factory<NotificationConnect>()
            single {
                PusherClients(mutableListOf())
            }
        }
    }

    override fun getHandlers(): List<KClass<out Handler<out Any>>> {
        return listOf()
    }

    override fun onConnect(): KClass<NotificationConnect> = NotificationConnect::class
}