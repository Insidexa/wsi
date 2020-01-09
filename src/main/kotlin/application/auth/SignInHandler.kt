package application.auth

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.core.ConstraintViolations
import io.ktor.features.origin
import org.joda.time.DateTime
import wsi.router.*
import wsi.transport.Request
import wsi.websocket.WebSocketResult
import wsi.websocket.connections.Identifier
import wsi.websocket.connections.Session
import wsi.websocket.connections.SessionManager

@Route("signIn", SignInHandler.Validation::class)
class SignInHandler(
        private val sessionManager: SessionManager
): Handler<SignInHandler.Validation> {
    data class Validation(
            val email: String,
            val password: String,
            val browser: String
    )

    override suspend fun invoke(request: Request<Validation>, ctx: Context) {
        val key = sessionManager.encode(request.payload.email)
        val session = Session(
                key = key,
                ip = ctx.call.request.origin.remoteHost,
                browser = request.payload.browser,
                date = DateTime.now().toString(),
                clientUUID = Identifier(ctx.idna)
        )
        sessionManager.register(session)

        ctx.respondWs(
                WebSocketResult(
                        "signin",
                        key
                )
        )
    }

    override suspend fun validation(payload: Validation): ConstraintViolations {
        return ValidatorBuilder.of<Validation>()
                .konstraint(Validation::browser) {
                    notNull()
                }
                .konstraint(Validation::email) {
                    notNull()
                            .email()
                }
                .konstraint(Validation::password) {
                    notNull()
                            .greaterThanOrEqual(8)
                }
                .build()
                .validate(payload)
    }
}
