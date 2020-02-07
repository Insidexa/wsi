package wsi.eventSourcing

class InMemory: EventStore {
    private val events = mutableListOf<Event>()

    override fun saveAll(events: List<Event>) {
        events.forEach(this::save)
    }

    override fun save(event: Event) {
        events.add(event)
    }

    override fun allFor(aggregateType: AggregateType): List<Event> {
        return events.filter { it -> it.aggregateType == aggregateType }
    }

    fun events(): List<Event> {
        return this.events
    }
}