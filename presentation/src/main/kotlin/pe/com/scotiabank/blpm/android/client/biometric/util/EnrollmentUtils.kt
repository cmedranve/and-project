package pe.com.scotiabank.blpm.android.client.biometric.util

import android.content.Context
import android.text.SpannableStringBuilder
import com.scotiabank.proofofkey.auth.core.entities.BiometricPromptFieldModel
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.font.TypefaceProvider
import pe.com.scotiabank.blpm.android.client.biometric.enrollmentV2.digitalkeybiometricV2.PreRegisterResponseModelV2
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.Util
import pe.com.scotiabank.blpm.android.data.entity.biometric.BiometricPreRegisterResponseEntity

fun Context.createBannerMessage(typefaceProvider: TypefaceProvider): SpannableStringBuilder {
    val fullMessage = getString(R.string.biometric_buddy_tip_description_enrollment)
    val bold = getString(R.string.biometric_buddy_tip_description_enrollment_bold)
    return Util.setSpannableTypeFace(fullMessage, typefaceProvider.boldTypeface, bold)
}

fun Context.createBiometricPromptFieldModel(): BiometricPromptFieldModel =
    BiometricPromptFieldModel(
        getString(R.string.biometric_title),
        Constant.EMPTY_STRING,
        getString(R.string.biometric_prompt_description_enrollment),
        getString(R.string.biometric_cancel_button)
    )

fun PreRegisterResponseModelV2.updateModel(biometricPreRegisterResponseEntity: BiometricPreRegisterResponseEntity) {
    authCode = biometricPreRegisterResponseEntity.authCode
    correlationId =
        biometricPreRegisterResponseEntity.correlationId
    isNeedRegister =
        biometricPreRegisterResponseEntity.isNeedRegister
}
