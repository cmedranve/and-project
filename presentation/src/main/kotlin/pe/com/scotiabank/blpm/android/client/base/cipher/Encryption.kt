package pe.com.scotiabank.blpm.android.client.base.cipher

import android.util.Base64
import pe.com.scotiabank.blpm.android.client.util.Constant
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class Encryption(
    private val algorithmModePadding: String = "RSA/ECB/PKCS1Padding",
    private val keyAlgorithm: String = "RSA",
) {

    fun generateEncryptData(data: ByteArray, key: String): String {

        val publicKey: PublicKey = generatePublicKey(key)

        val cipher: Cipher = Cipher.getInstance(algorithmModePadding)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val finalEncode: ByteArray = cipher.doFinal(data)

        return Base64
            .encodeToString(finalEncode, Base64.DEFAULT)
            .trim()
            .lines()
            .joinToString(Constant.EMPTY_STRING)
    }

    private fun generatePublicKey(key: String): PublicKey {

        val keyBytes: ByteArray = Base64.decode(key, Base64.DEFAULT)
        val keySpec: EncodedKeySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory: KeyFactory = KeyFactory.getInstance(keyAlgorithm)

        return keyFactory.generatePublic(keySpec)
    }
}
