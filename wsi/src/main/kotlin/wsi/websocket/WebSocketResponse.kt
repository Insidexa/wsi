package wsi.websocket

enum class ErrorType(val code: Int) {
    APP_ERROR(1001),

    VALIDATION(2001),
    PARSE_REQUEST(2002),
    INVALID_JSON_SCHEMA(2003),

    HANDLER_NOT_FOUND(3001),
    TRANSPORT_NOT_SUPPORTED(3002),

    AUTHENTICATION(4001),
}

sealed class WebSocketResponse
data class WebSocketResult(
        val channel: String,
        val payload: Any
) : WebSocketResponse()

data class WebSocketError(
        val channel: String,
        val payload: Any? = null,
        val errorType: Int
) : WebSocketResponse()