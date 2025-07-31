package pe.com.scotiabank.blpm.android.client.biometric.enrollment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.app.PushOtpFlowChecker
import pe.com.scotiabank.blpm.android.data.domain.interactor.DigitalKeyValidationUseCase
import pe.com.scotiabank.blpm.android.data.domain.interactor.LoginUseCase
import pe.com.scotiabank.blpm.android.data.domain.interactor.ResendOtpUseCase
import pe.com.scotiabank.blpm.android.data.domain.interactor.SmartKeyJoyUseCase
import pe.com.scotiabank.blpm.android.data.domain.interactor.biometric.BiometricPreRegisterUseCase
import javax.inject.Inject

class EnrollmentBiometricViewModelFactory @Inject constructor(
    private val appModel: AppModel,
    private val useCase: SmartKeyJoyUseCase,
    private val digitalKeyValidationUseCase: DigitalKeyValidationUseCase,
    private val biometricPreRegisterUseCase: BiometricPreRegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val resendOtpUseCase: ResendOtpUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnrollmentBiometricViewModel::class.java)) {
            return EnrollmentBiometricViewModel(
                appModel,
                PushOtpFlowChecker(appModel),
                useCase,
                digitalKeyValidationUseCase,
                biometricPreRegisterUseCase,
                loginUseCase,
                resendOtpUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }
}
