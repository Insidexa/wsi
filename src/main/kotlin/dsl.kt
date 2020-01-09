

/*
class WebSocketLifecycle {
    var onConnect: KClass<out WebSocketConnectionMiddleware>? = null
    var onClose: KClass<out WebSocketCloseMiddleware>? = null
}

class RouteDef(
        val clazz: KClass<out Handler<out Any>>,
        val name: String
) {
    var request: KClass<out Any> by Delegates.notNull()
    var guards = listOf<KClass<out Guard>>()
}

class WsRouterDef {
    val routeList = mutableListOf<RouteDef>()

    fun route(clazz: KClass<out Handler<out Any>>, path: String, block: RouteDef.() -> Unit) {
        val route = RouteDef(clazz, path).apply(block)
        routeList.add(route)
    }
}

class WsNamespace(
        val path: String
) {
    var adapter: KClass<out WebSocket> = WebSocketHandler::class
    var container: Module by Delegates.notNull()
    var lifecycle: WebSocketLifecycle? = null
    var router = WsRouterDef()

    fun lifecycle(block: WebSocketLifecycle.() -> Unit): WebSocketLifecycle? {
        lifecycle = WebSocketLifecycle().apply(block)

        return lifecycle
    }

    fun router(block: WsRouterDef.() -> Unit) {
        router.apply(block)
    }
}

class WsiApp {
    val namespaces = mutableListOf<WsNamespace>()
    var container: Module by Delegates.notNull()

    fun namespace(path: String, block: WsNamespace.() -> Unit): WsNamespace {
        val namespace = WsNamespace(path).apply(block)
        namespaces.add(namespace)
        return namespace
    }
}

fun wsApp(block: WsiApp.() -> Unit): WsiApp = WsiApp().apply(block)

val app = wsApp {
    container = org.koin.dsl.module(createdAtStart = true) {
        single { config }
        single { Mapper }
        single<SessionManager>()
        single<WebSocketHandler>()
        single<CommandRouter>()
        single<CommandBus>()
        single<QueryBus>()
        single<EventBus>()
        single {
            PostgreSQLConnectionBuilder.createConnectionPool {
                host = config[ConfigKeys.DB_HOST]
                port = config[ConfigKeys.DB_PORT]
                database = config[ConfigKeys.DB_DATABASE]
                username = config[ConfigKeys.DB_USERNAME]
                result = config[ConfigKeys.DB_PASSWORD]
            }.asSuspending
        }
        single {
            Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2i)
        }
        single<PasswordSecurity>()
        factory<AuthGuard>()
    }

    namespace("factorial") {
        lifecycle {
            onConnect = FactorialModule.FactorialMiddleware::class
        }
        container = module(createdAtStart = true) {
            single<FactorialModule>()
            factory<FactorialHandler>()
            factory<FactorialGuard>()
            single<FactorialQuery>()
            factory<FactorialModule.FactorialMiddleware>()
        }
        router {
            route(FactorialHandler::class, "factorial") {
                request = FactorialHandler.Validation::class
                guards = listOf(
                        FactorialGuard::class
                )
            }
        }
    }
    namespace("auth") {
        container = module(createdAtStart = true) {
            factory<SignInHandler>()
            factory<MeHandler>()
        }
        router {
            route(SignInHandler::class, "signIn") {
                request = SignInHandler.Validation::class
            }
            route(MeHandler::class, "me") {
                request = Empty::class
                guards = listOf(
                        AuthGuard::class
                )
            }
        }
    }
}
*/