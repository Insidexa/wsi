package wsi.router

import io.ktor.application.ApplicationCall
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import wsi.extensions.sendJSON
import wsi.websocket.IDNA_KEY
import wsi.websocket.MIDDLEWARE_AUTH_KEY
import wsi.websocket.WebSocketResponse

data class Context(val call: ApplicationCall,
                   val incoming: ReceiveChannel<Frame>,
                   val outgoing: SendChannel<Frame>,
                   val path: String
)

suspend fun Context.respondWs(json: WebSocketResponse) {
    this.outgoing.sendJSON(json)
}

val Context.middlewareAuthToken: String?
    get() = call.request.queryParameters[MIDDLEWARE_AUTH_KEY]

val Context.idna: String
    get() = call.request.queryParameters[IDNA_KEY]!!