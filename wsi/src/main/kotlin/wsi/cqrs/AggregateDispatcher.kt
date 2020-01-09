package wsi.cqrs

import wsi.cqrs.event.EventBus

class AggregateDispatcher(
        private val eventBus: EventBus,
        private val em: EntityManager
) {
    suspend fun commit(vararg aggregates: Aggregate) {
        for (aggregate in aggregates) {
            val events = aggregate.popEvents()
            for (event in events) {
//                em.persist(event)
                eventBus.execute(event)
            }
        }
    }
}
