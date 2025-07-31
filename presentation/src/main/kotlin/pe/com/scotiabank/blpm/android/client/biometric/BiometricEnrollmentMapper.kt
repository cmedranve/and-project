package pe.com.scotiabank.blpm.android.client.biometric

import pe.com.scotiabank.blpm.android.client.model.biometric.BiometricConfigurationModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.biometric.UserAuthenticatorEntity

/**
 * TODO: Remove @JvmStatic when Kotlin migration is done
 **/

object BiometricEnrollmentMapper {

    @JvmStatic
    fun UserAuthenticatorEntity.Data.transformToBiometricConfigurationModel() = if (operations.isEmpty()) {
        BiometricConfigurationModel(isLoginEnabled = true, isTxEnabled = false)
    } else {
        BiometricConfigurationModel(
                isLoginEnabled = operations.any { operation -> operation == Constant.LOGIN_OPERATION },
                isTxEnabled = operations.any { operation -> operation == Constant.TX_OPERATION }
        )
    }

    @JvmStatic
    fun BiometricConfigurationModel.toReversedOperations() = mutableListOf<String>().apply {
        if (isLoginEnabled) {
            add(Constant.LOGIN_OPERATION)
        }
        if (isTxEnabled) {
            add(Constant.TX_OPERATION)
        }
    }
}
