package wsi.cqrs

interface DomainEvent

open class Aggregate {
    private val events = mutableListOf<DomainEvent>()

    fun apply(vararg events: DomainEvent) {
        this.events.addAll(events)
    }

    fun popEvents(): List<DomainEvent> {
        val events = this.events.toList()
        this.events.clear()

        return events
    }
}
