package application.pusher

import wsi.router.Context
import wsi.router.idna
import wsi.router.middlewareAuthToken
import wsi.websocket.WebSocketConnectionMiddleware
import wsi.websocket.connections.SessionManager

class NotificationConnect(
        private val sessionManager: SessionManager,
        private val clients: PusherClients
): WebSocketConnectionMiddleware {
    override suspend fun invoke(ctx: Context): Boolean {
        val token = ctx.middlewareAuthToken
        val idna = ctx.idna
        val isAuth = sessionManager.isAuth(token, idna)

        if (isAuth) {
            clients.add(UserConnection(
                    id = if (isAuth) idna else "",
                    isAuth = isAuth,
                    ctx = ctx
            ))
        }

        return isAuth
    }
}
