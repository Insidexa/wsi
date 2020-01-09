package application.auth

import application.AuthGuard
import application.pusher.PusherClients
import wsi.router.*
import wsi.transport.Request
import wsi.websocket.WebSocketResult

class Empty

@Route("me", Empty::class)
class MeHandler(val pc: PusherClients): Handler<Empty> {

    override suspend fun invoke(request: Request<Empty>, ctx: Context) {
        pc.single(ctx.idna)?.let { conn ->
            conn.ctx.respondWs(
                    WebSocketResult(
                            "me",
                            1
                    )
            )
        }
    }

    override fun guards(): Array<out Any> {
        return arrayOf(
                AuthGuard::class
        )
    }
}
