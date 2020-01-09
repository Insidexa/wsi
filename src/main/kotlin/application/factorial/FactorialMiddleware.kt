package application.factorial

import wsi.router.Context
import wsi.websocket.WebSocketConnectionMiddleware
import wsi.websocket.connections.SessionManager

class FactorialMiddleware(
        private val sessionManager: SessionManager
): WebSocketConnectionMiddleware {
    override suspend fun invoke(ctx: Context): Boolean {
        return true
    }
}
