package wsi.websocket

fun validation(channel: String): String {
    return "$channel.request"
}

fun commandNotFound(channel: String): String {
    return "$channel.commandNotFound"
}

fun requestParsingError(channel: String): String {
    return "$channel.requestParsingError"
}

fun invalidJSONSchema(channel: String): String {
    return "$channel.invalidSchema"
}

fun internal(channel: String): String {
    return "$channel.internal"
}

fun protocolNotSupported(channel: String): String {
    return "$channel.protocolNotSupported"
}