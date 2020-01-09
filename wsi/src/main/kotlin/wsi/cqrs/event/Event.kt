package wsi.cqrs.event

interface Event<T> {
    suspend fun handle(event: T)
}
