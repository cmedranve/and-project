package pe.com.scotiabank.blpm.android.client.cards

import android.content.Context
import android.content.pm.PackageManager
import br.com.hst.issuergp.core.IssuerGP
import br.com.hst.issuergp.data.model.Callback
import br.com.hst.issuergp.data.model.GooglePayToken
import br.com.hst.issuergp.data.model.TokenStatus
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.errorhandling.ErrorReceiver
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.exception.ExceptionWithResource
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardBrand
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.util.Constant
import java.lang.ref.WeakReference
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GooglePayValidator(
    private val weakAppContext: WeakReference<Context?>,
    private val optionTemplate: OptionTemplate
) {

    val isFeatureFlagVisibleForGooglePay: Boolean
        get() = optionTemplate.isVisible

    private val exception: ExceptionWithResource by lazy {
        ExceptionWithResource(R.string.exception_message_generic)
    }

    suspend fun isTokenizedCard(
        cardNumber: String,
        cardBrand: AtmCardBrand,
    ): Boolean = suspendCoroutine { continuation ->

        val panLast4: String = cardNumber.takeLast(Constant.FOUR)
        val activeTokenCallback = ActiveTokenCallback(
            panLast4 = panLast4,
            cardBrand = cardBrand,
            onFinish = continuation::resume,
            onError = continuation::resumeWithException
        )

        weakAppContext.get()
            ?.let { appContext -> IssuerGP.getTokens(appContext, activeTokenCallback) }
            ?: continuation.resumeWithException(exception)
    }

    fun isTokenizedCard(
        cardNumber: String,
        cardBrand: AtmCardBrand,
        receiver: InstanceReceiver,
        errorReceiver: ErrorReceiver,
    ) {
        val panLast4: String = cardNumber.takeLast(Constant.FOUR)
        val activeTokenCallback = ActiveTokenCallbackLegacy(
            panLast4 = panLast4,
            cardBrand = cardBrand,
            receiver = receiver,
            errorReceiver = errorReceiver,
        )

        weakAppContext.get()
            ?.let { appContext -> IssuerGP.getTokens(appContext, activeTokenCallback) }
            ?: errorReceiver.receive(exception)
    }

    fun isGoogleWalletInstalled(): Boolean = try {
        val context: Context = weakAppContext.get() ?: throw ExceptionWithResource(R.string.exception_message_generic)
        context.packageManager.getPackageInfo(GOOGLE_WALLET_PACKAGE_NAME, PackageManager.GET_ACTIVITIES)
        true
    } catch (exception: PackageManager.NameNotFoundException) {
        false
    }

    class ActiveTokenCallback(
        private val panLast4: String,
        private val cardBrand: AtmCardBrand,
        private val onFinish: (Boolean) -> Unit,
        private val onError: (Throwable) -> Unit,
    ) : Callback<List<GooglePayToken>> {

        override fun onError(throwable: Throwable) = onError.invoke(throwable)

        override fun onFinish(data: List<GooglePayToken>) {
            val isActive: Boolean = data.any(::tokenStatePredicate)
            onFinish.invoke(isActive)
        }

        private fun tokenStatePredicate(googlePayToken: GooglePayToken): Boolean {
            if (googlePayToken.physicalPanLast4 != panLast4) return false
            if (googlePayToken.cardNetwork != cardBrand.cardNetwork) return false
            return googlePayToken.tokenState == TokenStatus.ACTIVE
        }
    }

    class ActiveTokenCallbackLegacy(
        private val panLast4: String,
        private val cardBrand: AtmCardBrand,
        private val receiver: InstanceReceiver,
        private val errorReceiver: ErrorReceiver,
    ) : Callback<List<GooglePayToken>> {

        override fun onError(throwable: Throwable) {
            errorReceiver.receive(throwable)
        }

        override fun onFinish(data: List<GooglePayToken>) {
            val isActive: Boolean = data.any(::tokenStatePredicate)
            receiver.receive(isActive)
        }

        private fun tokenStatePredicate(googlePayToken: GooglePayToken): Boolean {
            if (googlePayToken.physicalPanLast4 != panLast4) return false
            if (googlePayToken.cardNetwork != cardBrand.cardNetwork) return false
            return googlePayToken.tokenState == TokenStatus.ACTIVE
        }
    }

    companion object {
        private const val GOOGLE_WALLET_PACKAGE_NAME = "com.google.android.apps.walletnfcrel"
    }
}
