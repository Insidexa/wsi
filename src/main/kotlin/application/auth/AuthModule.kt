package application.auth

import application.AuthGuard
import com.natpryce.konfig.Configuration
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
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
    val DB_PORT = Key("DB_PORT", intType)
    val DB_DATABASE = Key("DB_DATABASE", stringType)
    val DB_USERNAME = Key("DB_USERNAME", stringType)
    val DB_PASSWORD = Key("DB_PASSWORD", stringType)
    val DB_PARAMS = Key("DB_PARAMS", stringType)

    override fun connectionPath(): String = "auth"

    override fun dependencies(config: Configuration): Module {
        val dbUrl = "jdbc:postgresql://"
                .plus(config[DB_HOST])
                .plus(":${config[DB_PORT]}")
                .plus("/${config[DB_DATABASE]}")
                .plus("?${config[DB_PARAMS]}")

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
                        url = dbUrl,
                        driver = "org.postgresql.Driver",
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
