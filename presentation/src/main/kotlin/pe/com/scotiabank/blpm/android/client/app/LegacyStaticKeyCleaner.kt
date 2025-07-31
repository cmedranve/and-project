package pe.com.scotiabank.blpm.android.client.app

import pe.com.scotiabank.blpm.android.client.nosession.keygenerator.KeysGenerator

class LegacyStaticKeyCleaner(
    private val keysGenerator: KeysGenerator,
    private val alias: String,
) {

    fun clean() = keysGenerator.removeKeyPair(alias)
}
