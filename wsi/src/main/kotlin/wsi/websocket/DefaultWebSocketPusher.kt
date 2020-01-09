package wsi.websocket

interface WebSocketPusher {
    fun <T>single(id: T): T = id
}

class DefaultWebSocketPusher: WebSocketPusher {
    override fun <T>single(id: T): T = id
}