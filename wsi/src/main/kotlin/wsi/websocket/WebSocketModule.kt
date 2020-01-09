package wsi.websocket

import com.natpryce.konfig.Configuration
import org.koin.core.module.Module
import wsi.router.Handler
import kotlin.reflect.KClass

interface WebSocketModule {
    fun connectionPath(): String

    fun onConnect(): KClass<out WebSocketConnectionMiddleware>? = null

    fun onClose(): KClass<out WebSocketCloseMiddleware>? = null

    fun webSocketHandler(): KClass<out WebSocket> = WebSocketHandler::class

    fun init() {}

    fun getHandlers(): List<KClass<out Handler<out Any>>>

    fun dependencies(config: Configuration): Module
}