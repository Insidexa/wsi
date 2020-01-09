package wsi.websocket.connections

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

data class Session(
        val clientUUID: Identifier,
        val key: String,
        val ip: String,
        val location: String? = null,
        val browser: String,
        val date: String
)
data class Identifier(val uuid: String)

class SessionManager {
    private val charset = charset("UTF8")
    private val publicKey = readPublicKey("public.der")
    private val privateKey = readPrivateKey("private.der")
    private val sessions = mutableListOf<Session>()

    fun register(session: Session) {
        sessions.add(session)
    }

    fun userSessions(id: Identifier): List<Session> {
        return sessions.filter { it -> it.clientUUID == id }
    }

    fun isAuth(key: String?, idna: String?): Boolean {
        if (key == null || idna == null) {
            return false
        }

        return sessions.find { it-> it.key == key && it.clientUUID.uuid == idna } != null
    }

    fun hasSessionById(id: Identifier): Boolean {
        return sessions.find { it.clientUUID == id } != null
    }

    fun unregister(key: String) {
        val session = sessions.find { it.key == key }
        sessions.remove(session)
    }

    fun detectTheft() {

    }

    @Throws(IOException::class)
    private fun readFileBytes(filename: String): ByteArray {
        val path = Paths.get(filename)
        return Files.readAllBytes(path)
    }

    @Throws(IOException::class, NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    private fun readPublicKey(filename: String): PublicKey {
        val publicSpec = X509EncodedKeySpec(readFileBytes(filename))
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(publicSpec)
    }

    @Throws(IOException::class, NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    private fun readPrivateKey(filename: String): PrivateKey {
        val keySpec = PKCS8EncodedKeySpec(readFileBytes(filename))
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }

    @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidKeyException::class, IllegalBlockSizeException::class, BadPaddingException::class)
    fun encrypt(key: PublicKey, plaintext: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(plaintext)
    }

    @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidKeyException::class, IllegalBlockSizeException::class, BadPaddingException::class)
    fun decrypt(key: PrivateKey, ciphertext: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(ciphertext)
    }

    fun decode(secret: String): String {
        val recoveredMessage = decrypt(privateKey, Base64.getDecoder().decode(secret))

        return String(recoveredMessage, charset)
    }

    fun encode(text: String): String {
        val message = text.toByteArray(charset)

        return Base64.getEncoder().encodeToString(encrypt(publicKey, message))
    }
}