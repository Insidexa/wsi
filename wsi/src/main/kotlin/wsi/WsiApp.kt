package wsi

import com.natpryce.konfig.Configuration
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.overriding
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.experimental.builder.single
import wsi.cqrs.AggregateDispatcher
import wsi.cqrs.EntityManager
import wsi.cqrs.command.CommandBus
import wsi.cqrs.event.EventBus
import wsi.cqrs.query.QueryBus
import wsi.extensions.createFrom
import wsi.router.CommandRouter
import wsi.server.Server
import wsi.transport.Mapper
import wsi.websocket.WebSocketHandler
import wsi.websocket.WebSocketModule
import wsi.websocket.WebSocketPusher
import wsi.websocket.connections.SessionManager
import kotlin.reflect.KClass

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
        val koinApp = koinApplication {
            modules(dependencies + serverModule(config, appConfig, wsModules))
        }
        koinApp.koin.declare(koinApp)
        val server = koinApp.koin.get<Server>()

        server.start()
    }

    private fun serverModule(
            config: Configuration,
            wsiConfiguration: WsiConfiguration,
            wsModules: List<WebSocketModule>
    ): Module {
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
            single {
                createFrom(wsiConfiguration.webSocketPusher, this)
            } bind WebSocketPusher::class
            single<Server>()
            single { wsModules }
        }
    }
}
