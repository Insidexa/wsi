package application.factorial

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.core.ConstraintViolations
import wsi.cqrs.AggregateDispatcher
import wsi.router.Context
import wsi.router.Handler
import wsi.router.Route
import wsi.router.respondWs
import wsi.transport.Request
import wsi.websocket.WebSocketResult

@Route("factorial", FactorialHandler.Validation::class)
class FactorialHandler(
        private val aggregateDispatcher: AggregateDispatcher
) : Handler<FactorialHandler.Validation> {
    data class Validation(val number: Int)

    override suspend fun invoke(request: Request<Validation>, ctx: Context) {

        val factorial = FactorialAggregate(request.payload.number)
        factorial.calculate()
        aggregateDispatcher.commit(
                factorial
        )

        ctx.respondWs(
                WebSocketResult(
                        "factorial",
                        1
                )
        )
    }

    override fun guards(): Array<out Any> {
        return arrayOf(
                FactorialGuard::class
        )
    }

    override suspend fun validation(payload: FactorialHandler.Validation): ConstraintViolations {
        return ValidatorBuilder.of<FactorialHandler.Validation>()
                .konstraint(FactorialHandler.Validation::number) {
                    notNull()
                            .greaterThan(1)
                            .lessThan(20)
                }
                .build()
                .validate(payload)
    }

}
