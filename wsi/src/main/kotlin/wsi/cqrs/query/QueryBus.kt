package wsi.cqrs.query

import org.koin.core.KoinComponent
import kotlin.reflect.full.findAnnotation

class QueryBus: KoinComponent {
    private val definitions = getKoin().rootScope.beanRegistry.getAllDefinitions()

    suspend fun execute(command: Any): Any {
        val commandHandler = definitions.find { it ->
            val type = it.primaryType.findAnnotation<QueryHandler>()

            type != null && type.clazz == command::class
        } ?: throw RuntimeException("Query by ${command::class} not found")

        val handler = getKoin().get<Query<Any>>(
                clazz = commandHandler::primaryType.get(),
                qualifier = null,
                parameters = null
        )
        return handler.execute(command)
    }
}
