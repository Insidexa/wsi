package wsi

import wsi.websocket.DefaultWebSocketPusher

class WsiConfiguration {
    val configPath: String = "application.properties"

    val webSocketPusher = DefaultWebSocketPusher::class
}
