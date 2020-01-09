package wsi.router

import org.koin.core.KoinComponent
import wsi.transport.Request
import wsi.websocket.*
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

typealias Path = String
typealias Action = String

class CommandRouter(
        private val pusher: WebSocketPusher
): KoinComponent {
    private val moduleHandlers = mutableMapOf<Path, Map<Action, RouteCommand>>()

    fun registerModuleHandlers(module: WebSocketModule) {
        val handlers = module
                .getHandlers()
                .map { it ->
                    val handler = createRouteCommand(it)
                    handler.name to handler
                }
                .toMap()

        moduleHandlers[module.connectionPath()] = handlers
    }

    private fun createRouteCommand(handler: KClass<out Handler<out Any>>): RouteCommand {
        val route = handler.findAnnotation<Route>()!!
        return RouteCommand(route.name, handler, route.dataClass)
    }

    fun findHandlerByCommand(path: Path, command: String): RouteCommand? {
        val moduleHandlers = this.moduleHandlers[path]
        return moduleHandlers?.get(command)
    }

    suspend fun dispatch(command: RouteCommand, request: Request<Any>, ctx: Context) {
        val koin = this.getKoin()
        val handler = koin.get<Handler<Any>>(
                clazz = command.handler,
                qualifier = null,
                parameters = null
        )
        val guards = handler.guards()

        guards.forEach { it ->
            val guard = koin.get<Guard>(
                    clazz = it as KClass<*>,
                    qualifier = null,
                    parameters = null
            )
            if (guard.invoke(handler, request, ctx)) return@forEach else return
        }

        try {
            val res = handler.validation(request.payload)
            if (res?.isValid == false) {
                pusher.single(ctx).respondWs(
                        WebSocketError(
                                validation(request.command),
                                res.details(),
                                ErrorType.VALIDATION.code
                        )
                )
                return
            }
            handler.invoke(request, ctx)
        } catch (exception: Exception) {
            pusher.single(ctx).respondWs(
                    WebSocketError(
                            internal("server"),
                            exception.localizedMessage,
                            ErrorType.PARSE_REQUEST.code
                    )
            )
        }
    }

}