package wsi.router

import kotlin.reflect.KClass

annotation class Route(
        val name: String,
        val dataClass: KClass<out Any>)