package pe.com.scotiabank.blpm.android.client.biometric.enrollment

import pe.com.scotiabank.blpm.android.client.biometric.enrollmentV2.digitalkeybiometricV2.PreRegisterResponseModelV2

interface EnrollmentBiometricView {
    fun showBiometricPrompt(model: PreRegisterResponseModelV2?)
}
