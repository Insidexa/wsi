package application.factorial

import wsi.router.Context
import wsi.router.Guard
import wsi.router.Handler
import wsi.transport.Request

class FactorialGuard: Guard {
    override suspend fun invoke(
            handler: Handler<Any>,
            request: Request<Any>,
            ctx: Context): Boolean {
        return true
    }
}