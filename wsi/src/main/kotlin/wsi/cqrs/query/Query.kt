package wsi.cqrs.query

interface Query<T> {
    suspend fun execute(command: T): Any
}
