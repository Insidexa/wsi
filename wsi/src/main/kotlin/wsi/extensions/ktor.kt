package wsi.extensions

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.pingInterval
import io.ktor.http.cio.websocket.timeout
import io.ktor.routing.Route
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.koin.core.Koin
import wsi.router.Context
import wsi.router.respondWs
import wsi.transport.Mapper
import wsi.websocket.*
import java.nio.ByteBuffer
import java.time.Duration
import kotlin.reflect.KClass

private const val PROTOCOL_NOT_SUPPORTED = "Frame %s in not supported. Use only binary frame"

fun Route.registerWSModule(module: WebSocketModule, koin: Koin) {
    module.init()
    val handler = koin.get<WebSocket>(
            clazz = module.webSocketHandler(),
            qualifier = null,
            parameters = null
    )
    val modulePath = module.connectionPath()
    val wsPusher = koin.get<WebSocketPusher>()

    webSocket(modulePath) {

        pingInterval = Duration.ofSeconds(60)
        timeout = Duration.ofSeconds(15)

        val context = Context(
                call,
                incoming,
                outgoing,
                modulePath
        )

        try {
            module.onConnect()?.let { it ->
                val middleware = koin.get<WebSocketConnectionMiddleware>(
                        clazz = it as KClass<*>,
                        qualifier = null,
                        parameters = null
                )

                if (!(middleware::invoke)(context)) {
                    wsPusher.single(context).outgoing.close(Exception("Denied"))

                    return@webSocket
                }
            }

            handler.onConnect(context)

            incoming.consumeEach { frame ->
                when (frame) {
                    is Frame.Binary -> {
                        launch {
                            handler.onMessage(frame, context)
                        }
                    }
                    else -> {
                        wsPusher.single(context).respondWs(
                                WebSocketError(
                                        protocolNotSupported("server"),
                                        PROTOCOL_NOT_SUPPORTED.format(frame::class.simpleName),
                                        ErrorType.TRANSPORT_NOT_SUPPORTED.code
                                )
                        )
                    }
                }
            }
        } catch (error: Error) {
            handler.onError(error, context)
        } finally {
            handler.onClose(context, closeReason)
            module.onClose()?.let { it ->
                val middleware = koin.get<WebSocketCloseMiddleware>(
                        clazz = it as KClass<*>,
                        qualifier = null,
                        parameters = null
                )

                middleware.invoke(context)
            }
        }
    }
}

suspend fun <E>SendChannel<E>.sendJSON(obj: WebSocketResponse) {
    val jsonStr = Mapper.toJSON(obj)
    val buffer = ByteBuffer.wrap( jsonStr.toByteArray() )
    val frame = Frame.Binary(
            fin = true,
            buffer = buffer
    )
    send( frame as E )
}