package wsi.eventSourcing

typealias AggregateId = String
typealias AggregateType = Any
typealias EventType = Any

data class Event(
        val aggregateId: AggregateId,
        val aggregateType: AggregateType,
        val version: String,
        val eventType: EventType,
        val payload: Any
)

interface EventStore {
    fun save(event: Event)

    fun saveAll(events: List<Event>)

    fun allFor(aggregateType: AggregateType): List<Event>
}