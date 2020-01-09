package wsi.cqrs.command

interface Command<T> {
    suspend fun execute(command: T): Any
}
