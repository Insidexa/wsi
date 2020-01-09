package application

import application.pusher.PusherClients
import wsi.router.*
import wsi.transport.Request
import wsi.websocket.ErrorType
import wsi.websocket.WebSocketError
import wsi.websocket.connections.SessionManager

class AuthGuard(
        private val sessionManager: SessionManager,
        private val pc: PusherClients
): Guard {
    override suspend fun invoke(
            handler: Handler<Any>,
            request: Request<Any>,
            ctx: Context): Boolean {
        val reqId = request.getHeader<String>(GUARD_AUTH_KEY)
        val idna = ctx.idna
        val isAuth = sessionManager.isAuth(reqId, idna)
        if (!isAuth) {
            pc.single(idna)?.ctx?.respondWs(
                    WebSocketError(
                            channel = "app.auth",
                            payload = "Xuy tam",
                            errorType = ErrorType.AUTHENTICATION.code
                    )
            )
        }

        return isAuth
    }
}
