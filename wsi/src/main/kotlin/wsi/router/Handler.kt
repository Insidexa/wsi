package wsi.router

import am.ik.yavi.core.ConstraintViolations
import wsi.transport.Request

interface Handler<T> {
    suspend fun invoke(request: Request<T>, ctx: Context)

    fun guards(): Array<out Any> {
        return arrayOf()
    }

    suspend fun validation(payload: T): ConstraintViolations? {
        return null
    }
}