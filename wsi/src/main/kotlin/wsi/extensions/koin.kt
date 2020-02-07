package wsi.extensions

import org.koin.core.scope.Scope
import org.koin.experimental.builder.getArguments
import org.koin.experimental.builder.getFirstJavaConstructor
import org.koin.experimental.builder.makeInstance
import kotlin.reflect.KClass

inline fun <reified T : Any> createFrom(kClass: KClass<T>, context: Scope): T {
    lateinit var instance: T

    val ctor = kClass.getFirstJavaConstructor()
    val args = getArguments(ctor, context)
    instance = ctor.makeInstance(args)

    return instance
}
