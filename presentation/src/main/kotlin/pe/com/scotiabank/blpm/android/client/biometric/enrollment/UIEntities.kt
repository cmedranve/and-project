package pe.com.scotiabank.blpm.android.client.biometric.enrollment

import com.scotiabank.proofofkey.auth.utilities.error.exception.PokCryptoException

interface DataOfShowPokBiometricException{
    val exception : Exception
}

interface DataOfShowPokCryptoException{
    val exception : PokCryptoException
}
