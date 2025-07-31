package pe.com.scotiabank.blpm.android.client.biometric.enrollment

import com.scotiabank.proofofkey.auth.utilities.error.exception.PokCryptoException

class MutableDataOfShowPokCryptoException(
    override val exception: PokCryptoException,
) : DataOfShowPokCryptoException
