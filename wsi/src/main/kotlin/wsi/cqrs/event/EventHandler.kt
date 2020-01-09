package wsi.cqrs.event

import kotlin.reflect.KClass

annotation class EventHandler(val clazz: KClass<*>)