package application.auth

import com.natpryce.konfig.Configuration
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import de.mkammerer.argon2.Argon2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PasswordSecurity(
        val config: Configuration,
        val argon2: Argon2
) {
    val ARGON2_ITERATIONS = Key("SECURITY_ARGON2_ITERATIONS", intType)
    val ARGON2_MEMORY = Key("SECURITY_ARGON2_MEMORY", intType)
    val ARGON2_PARALLELISM = Key("SECURITY_ARGON2_PARALLELISM", intType)

    suspend fun hash(password: String): String {
        return withContext(Dispatchers.IO) {
            argon2.hash(
                    config[ARGON2_ITERATIONS],
                    config[ARGON2_MEMORY],
                    config[ARGON2_PARALLELISM],
                    password)
        }
    }

    suspend fun verify(hash: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            argon2.verify(hash, password)
        }
    }
}