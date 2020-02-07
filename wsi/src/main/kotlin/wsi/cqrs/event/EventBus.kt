package wsi.cqrs.event

import org.koin.core.KoinApplication
import kotlin.reflect.full.findAnnotation

class EventBus(
        private val koinApp: KoinApplication
) {
    private val definitions = koinApp.koin.rootScope.beanRegistry.getAllDefinitions()

    suspend fun execute(command: Any) {
        val eventHandlers = definitions.filter { it ->
            val type = it.primaryType.findAnnotation<EventHandler>()

            type != null && type.clazz == command::class
        }

        eventHandlers.forEach { it ->
            val handler = koinApp.koin.get<Event<Any>>(
                    clazz = it::primaryType.get(),
                    qualifier = null,
                    parameters = null
            )
            handler.handle(command)
        }
    }
}
