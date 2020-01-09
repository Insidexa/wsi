package wsi.cqrs.event

import org.koin.core.KoinComponent
import kotlin.reflect.full.findAnnotation

class EventBus: KoinComponent {
    private val definitions = getKoin().rootScope.beanRegistry.getAllDefinitions()

    suspend fun execute(command: Any) {
        val eventHandlers = definitions.filter { it ->
            val type = it.primaryType.findAnnotation<EventHandler>()

            type != null && type.clazz == command::class
        }

        eventHandlers.forEach { it ->
            val handler = getKoin().get<Event<Any>>(
                    clazz = it::primaryType.get(),
                    qualifier = null,
                    parameters = null
            )
            handler.handle(command)
        }
    }
}
