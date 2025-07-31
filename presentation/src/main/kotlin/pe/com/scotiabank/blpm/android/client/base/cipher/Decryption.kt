package pe.com.scotiabank.blpm.android.client.base.cipher

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.Cipher

class Decryption(
    private val algorithmModePadding: String = "RSA/ECB/PKCS1Padding",
    private val keyAlgorithm: String = "RSA",
) {

    fun decrypt(encryptedData: String, key: String): String {

        val privateKey: PrivateKey = generatePrivateFrom(key)

        val cipher = Cipher.getInstance(algorithmModePadding)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        val decode: ByteArray = Base64.decode(encryptedData, Base64.DEFAULT)
        val finalDecode = cipher.doFinal(decode)
        val decryptedText = String(finalDecode, StandardCharsets.UTF_8)

        return decryptedText.trimStart()
    }

    private fun generatePrivateFrom(key: String): PrivateKey {
        val keyBytes: ByteArray = Base64.decode(key, Base64.DEFAULT)
        val encodedKeySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(keyAlgorithm)

        return keyFactory.generatePrivate(encodedKeySpec)
    }
}
