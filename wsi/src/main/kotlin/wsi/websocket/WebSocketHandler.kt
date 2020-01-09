package wsi.websocket

import com.fasterxml.jackson.core.JsonParseException
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.Deferred
import org.slf4j.LoggerFactory
import wsi.cqrs.event.EventBus
import wsi.router.*
import wsi.transport.Mapper
import wsi.transport.Request

data class WebSocketLifecycleConnect(val ctx: Context)
data class WebSocketLifecycleError(val ctx: Context, val error: Error)
data class ClientCommandNotFound(val ctx: Context, val request: Request<Any>)
data class ClientDispatchCommand(val ctx: Context, val command: RouteCommand)
data class ClientRequestError(val ctx: Context, val error: JsonParseException)
data class WebSocketLifecycleClose(val ctx: Context, val closeReason: Deferred<CloseReason?>)
data class InternalError(val ctx: Context, val exception: Exception)
data class JSONSchemaError(val ctx: Context, val exception: Exception)

private const val ROUTE_NOT_FOUND = "Route %s not found"
private const val REQUEST_PARSE_ERROR = "Request contains invalid body"
private const val JSON_INVALID_SCHEMA = "JSON Invalid schema"
private const val INTERNAL_ERROR = "Internal error"

class WebSocketHandler(private val router: CommandRouter,
                       private val mapper: Mapper,
                       private val eventBus: EventBus,
                       private val pusher: WebSocketPusher
) : WebSocket {

    private val log = LoggerFactory.getLogger("wsi.${WebSocketHandler::class.simpleName}")

    override suspend fun onConnect(ctx: Context) {
        log.info("onConnect [{}]", ctx.idna)
        eventBus.execute(WebSocketLifecycleConnect(ctx))
    }

    override suspend fun onError(error: Error, ctx: Context) {
        log.error("onError [{}]: {}", ctx.idna, error.message)
        eventBus.execute(WebSocketLifecycleError(ctx, error))
    }

    override suspend fun onMessage(frame: Frame.Binary, ctx: Context) {
        log.info("onMessage [{}]", ctx.idna)
        try {
            val length = frame.buffer.remaining()
            val byteArray = ByteArray(length)
            frame.buffer.get(byteArray)

            val request = mapper.mapTo<Request<Any>>(byteArray)
            val command = router.findHandlerByCommand(ctx.path, request.command)

            when (command) {
                null -> {
                    eventBus.execute(ClientCommandNotFound(ctx, request))
                    pusher.single(ctx).respondWs(WebSocketError(
                            commandNotFound("app"),
                            ROUTE_NOT_FOUND.format(request.command),
                            ErrorType.HANDLER_NOT_FOUND.code))
                }
                else -> {
                    eventBus.execute(ClientDispatchCommand(ctx, command))
                    router.dispatch(
                            command,
                            Request(
                                    request.command,
                                    mapper.mapTo(request.payload, command.dataClass),
                                    request.meta
                            ),
                            ctx
                    )
                }
            }
        } catch (exception: Exception) {
            handleException(exception, ctx)
        }
    }

    override suspend fun onClose(ctx: Context, closeReason: Deferred<CloseReason?>) {
        log.info("onClose [{}]", ctx.idna)
        eventBus.execute(WebSocketLifecycleClose(ctx, closeReason))
    }

    private suspend fun handleException(exception: Exception, ctx: Context) {
        when (exception) {
            is JsonParseException -> {
                eventBus.execute(ClientRequestError(ctx, exception))
                pusher.single(ctx).respondWs(WebSocketError(
                        requestParsingError("app"),
                        REQUEST_PARSE_ERROR,
                        ErrorType.PARSE_REQUEST.code))
            }
            is IllegalArgumentException -> {
                eventBus.execute(JSONSchemaError(ctx, exception))
                pusher.single(ctx).respondWs(WebSocketError(
                        invalidJSONSchema("app"),
                        JSON_INVALID_SCHEMA,
                        ErrorType.INVALID_JSON_SCHEMA.code))

            }
            else -> {
                eventBus.execute(InternalError(ctx, exception))
                pusher.single(ctx).respondWs(WebSocketError(
                        internal("app"),
                        INTERNAL_ERROR,
                        ErrorType.APP_ERROR.code))
            }
        }
    }
}