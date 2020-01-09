package wsi.cqrs.command

import kotlin.reflect.KClass

annotation class CommandHandler(val clazz: KClass<*>)