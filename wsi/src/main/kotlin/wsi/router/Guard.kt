package wsi.router

import wsi.transport.Request

const val GUARD_AUTH_KEY = "reqId"

interface Guard {
    suspend fun invoke(handler: Handler<Any>,
                       request: Request<Any>,
                       ctx: Context): Boolean
}