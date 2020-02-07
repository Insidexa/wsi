package wsi.server

interface ServerStarted {
    fun invoke()
}

interface ServerStarting {
    fun invoke()
}

interface ServerStopping {
    fun invoke()
}

interface ServerStopped {
    fun invoke()
}
