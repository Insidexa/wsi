package wsi.websocket

import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.Deferred
import wsi.router.Context

interface WebSocket {
    suspend fun onConnect(ctx: Context)

    suspend fun onMessage(frame: Frame.Binary, ctx: Context)

    suspend fun onError(error: Error, ctx: Context)

    suspend fun onClose(ctx: Context, closeReason: Deferred<CloseReason?>)
}