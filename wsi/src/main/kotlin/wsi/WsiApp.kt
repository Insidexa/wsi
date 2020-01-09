package wsi

import com.natpryce.konfig.Configuration
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.overriding
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.experimental.builder.single
import wsi.cqrs.AggregateDispatcher
import wsi.cqrs.EntityManager
import wsi.cqrs.command.CommandBus
import wsi.cqrs.event.EventBus
import wsi.cqrs.query.QueryBus
import wsi.router.CommandRouter
import wsi.transport.Mapper
import wsi.websocket.DefaultWebSocketPusher
import wsi.websocket.WebSocketHandler
import wsi.websocket.WebSocketModule
import wsi.websocket.WebSocketPusher
import wsi.websocket.connections.SessionManager
import kotlin.reflect.KClass

open class WsiConfiguration {
    val configPath: String = "application.properties"

    val webSocketPusher: KClass<out WebSocketPusher> = DefaultWebSocketPusher::class
}

class WsiApp(
        private val modules: List<KClass<out WebSocketModule>>
) {
    suspend fun run(configure: WsiConfiguration.() -> Unit = {}) {
        val appConfig = WsiConfiguration().apply(configure)
        val config = ConfigurationProperties.systemProperties() overriding
                EnvironmentVariables() overriding
                ConfigurationProperties.fromResource(appConfig.configPath)
        val wsModules = this.modules.map{ it -> it.constructors.first().call() }
        val dependencies = wsModules.map{ it -> it.dependencies(config) }
        startKoin {
            modules(dependencies + serverModule(config))
        }

        Server(wsModules).start()
    }

    private fun serverModule(config: Configuration): Module {
        return module(createdAtStart = true) {
            single { config }
            single { Mapper }
            single<SessionManager>()
            single<WebSocketHandler>()
            single<CommandRouter>()
            single<CommandBus>()
            single<QueryBus>()
            single<EventBus>()
            single<AggregateDispatcher>()
            single<EntityManager>()
            single<DefaultWebSocketPusher>() bind WebSocketPusher::class
        }
    }
}