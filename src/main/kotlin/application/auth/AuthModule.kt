package application.auth

import application.AuthGuard
import com.natpryce.konfig.Configuration
import com.natpryce.konfig.Key
import com.natpryce.konfig.stringType
import de.mkammerer.argon2.Argon2Factory
import org.jetbrains.exposed.sql.Database
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.experimental.builder.factory
import org.koin.experimental.builder.single
import wsi.router.Handler
import wsi.websocket.WebSocketModule
import kotlin.reflect.KClass

class AuthModule: WebSocketModule {
    val DB_HOST = Key("DB_HOST", stringType)
    val DB_DRIVER = Key("DB_DRIVER", stringType)
    val DB_USERNAME = Key("DB_USERNAME", stringType)
    val DB_PASSWORD = Key("DB_PASSWORD", stringType)

    override fun connectionPath(): String = "auth"

    override fun dependencies(config: Configuration): Module {
        return module {
            factory<SignInHandler>()
            factory<MeHandler>()
            single {
                Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2i)
            }
            single<PasswordSecurity>()
            factory<AuthGuard>()
            single {
                Database.connect(
                        url = config[DB_HOST],
                        driver = config[DB_DRIVER],
                        user = config[DB_USERNAME],
                        password = config[DB_PASSWORD]
                )

            }
        }
    }

    override fun getHandlers(): List<KClass<out Handler<out Any>>> {
        return listOf(
                MeHandler::class,
                SignInHandler::class
        )
    }
}
