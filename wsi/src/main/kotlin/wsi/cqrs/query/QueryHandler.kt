package wsi.cqrs.query

import kotlin.reflect.KClass

annotation class QueryHandler(val clazz: KClass<*>)