package wsi.transport

class Request<T>(val command: String,
                 val payload: T,
                 val meta: MutableMap<String, Any> = mutableMapOf()
) {

    inline fun <reified T> getHeader(key: String, default: Any? = null): T? {
        return this.meta.getOrDefault(key, default) as T
    }

    fun hasHeader(key: String): Boolean {
        return this.meta.containsKey(key)
    }
}