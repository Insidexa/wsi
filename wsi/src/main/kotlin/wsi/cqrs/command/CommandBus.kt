package wsi.cqrs.command

import org.koin.core.KoinComponent
import kotlin.reflect.full.findAnnotation

class CommandBus: KoinComponent {
    private val definitions = getKoin().rootScope.beanRegistry.getAllDefinitions()

    suspend fun <R: Any> execute(command: Any): R {
        val commandClass = command::class
        val commandHandler = definitions.find { it ->
            val type = it.primaryType.findAnnotation<CommandHandler>()

            type != null && type.clazz == commandClass
        } ?: throw RuntimeException("Command $commandClass not found")

        val handler = getKoin().get<Command<Any>>(
                clazz = commandHandler::primaryType.get(),
                qualifier = null,
                parameters = null
        )
        return handler.execute(command) as R
    }
}
