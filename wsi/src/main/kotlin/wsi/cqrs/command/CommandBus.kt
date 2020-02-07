package wsi.cqrs.command

import org.koin.core.KoinApplication
import kotlin.reflect.full.findAnnotation

class CommandBus(
        private val koinApp: KoinApplication
) {
    private val definitions = koinApp.koin.rootScope.beanRegistry.getAllDefinitions()

    suspend fun <R: Any> execute(command: Any): R {
        val commandClass = command::class
        val commandHandler = definitions.find { it ->
            val type = it.primaryType.findAnnotation<CommandHandler>()

            type != null && type.clazz == commandClass
        } ?: throw RuntimeException("Command $commandClass not found")

        val handler = koinApp.koin.get<Command<Any>>(
                clazz = commandHandler::primaryType.get(),
                qualifier = null,
                parameters = null
        )
        return handler.execute(command) as R
    }
}
