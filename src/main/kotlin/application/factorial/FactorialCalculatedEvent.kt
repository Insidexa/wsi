package application.factorial

import wsi.cqrs.DomainEvent
import wsi.cqrs.event.Event
import wsi.cqrs.event.EventHandler
import java.math.BigInteger

data class FactorialCalculated(val result: BigInteger): DomainEvent

@EventHandler(FactorialCalculated::class)
class FactorialCalculatedEvent: Event<FactorialCalculated> {
    override suspend fun handle(event: FactorialCalculated) {
        println(event.result)
    }
}
