package wsi.cqrs.query

import org.koin.core.KoinApplication
import kotlin.reflect.full.findAnnotation

class QueryBus(
        private val koinApp: KoinApplication
) {
    private val definitions = koinApp.koin.rootScope.beanRegistry.getAllDefinitions()
    suspend fun execute(command: Any): Any {
        val commandHandler = definitions.find { it ->
            val type = it.primaryType.findAnnotation<QueryHandler>()

            type != null && type.clazz == command::class
        } ?: throw RuntimeException("Query by ${command::class} not found")

        val handler = koinApp.koin.get<Query<Any>>(
                clazz = commandHandler::primaryType.get(),
                qualifier = null,
                parameters = null
        )
        return handler.execute(command)
    }
}
