package wsi.websocket

import wsi.router.Context

const val MIDDLEWARE_AUTH_KEY = "reqId"
const val IDNA_KEY = "idna"

interface WebSocketConnectionMiddleware {
    suspend fun invoke(ctx: Context): Boolean
}

interface WebSocketCloseMiddleware {
    suspend fun invoke(ctx: Context): Boolean
}