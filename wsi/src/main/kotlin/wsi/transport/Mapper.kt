package wsi.transport

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlin.reflect.KClass

object Mapper : ObjectMapper() {
    init {
        this.registerModule(KotlinModule())
    }

    inline fun <reified T>mapTo(json: ByteArray): T {
        return this.readValue<T>(json, T::class.java)
    }

    fun mapTo(json: String, dataClass: KClass<out Any>): Any {
        return this.readValue(json, dataClass.java)
    }

    fun toJSON(data: Any): String {
        return this.writeValueAsString(data)
    }

    fun mapTo(data: Any, dataClass: KClass<out Any>): Any {
        return this.convertValue(data, dataClass.java)
    }
}