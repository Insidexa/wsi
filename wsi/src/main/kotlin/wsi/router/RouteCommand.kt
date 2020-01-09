package wsi.router

import kotlin.reflect.KClass

data class RouteCommand(val name: String,
                        val handler: KClass<out Any>,
                        val dataClass: KClass<out Any>)