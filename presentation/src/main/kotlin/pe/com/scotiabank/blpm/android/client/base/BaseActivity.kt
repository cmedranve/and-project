package pe.com.scotiabank.blpm.android.client.base

import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NavUtils
import androidx.fragment.app.Fragment
import com.google.firebase.messaging.FirebaseMessaging
import dagger.android.support.DaggerAppCompatActivity
import pe.com.scotiabank.blpm.android.analytics.AnalyticsUserGateway
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.analytics.BaseAnalyticEvent
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.newwebview.NewWebViewActivity
import pe.com.scotiabank.blpm.android.client.p2p.onboarding.affiliation.analitycs.PlinAffiliationAlertAnalyticsConstants
import pe.com.scotiabank.blpm.android.client.restrictedprofile.alert.OpenMarketAlertActivity
import pe.com.scotiabank.blpm.android.client.restrictedprofile.alert.RestrictedProfileAlertActivity
import pe.com.scotiabank.blpm.android.client.tasknav.clearThenNavigateToHost
import pe.com.scotiabank.blpm.android.client.util.*
import pe.com.scotiabank.blpm.android.client.util.analytics.*
import pe.com.scotiabank.blpm.android.client.util.exception.ErrorMessageFactory
import pe.com.scotiabank.blpm.android.client.util.exception.RetrofitException
import pe.com.scotiabank.blpm.android.client.util.listener.AlertDialogListener
import pe.com.scotiabank.blpm.android.client.util.webview.WebViewConstant
import pe.com.scotiabank.blpm.android.data.domain.exception.DefaultErrorBundle
import pe.com.scotiabank.blpm.android.data.exception.FinishedSessionException
import pe.com.scotiabank.blpm.android.data.exception.ForceUpdateException
import java.util.*
import javax.inject.Inject

/**
 * Base [android.app.Activity] class for every Activity in this application.
 */
abstract class BaseActivity : DaggerAppCompatActivity(), LauncherRegistryForActivityResult,
    HandlerOfAppRouting {

    private val deferredFragmentTransactions: Queue<DeferredFragmentTransaction> = ArrayDeque()
    private var progressDialog: ProgressDialog? = null
    private var isRunning = false
    private var alertDialogListener: AlertDialogListener? = null
    private var qrDialogListener: AlertDialogListener? = null
    private var errorDialogListener: AlertDialogListener? = null
    private var alertDialogListenerYesNoWithResponseCode: AlertDialogListenerYesNoWithResponseCode? = null
    protected var onDeleteListener: OnDeleteListener? = null
    private lateinit var trackDataEntity: TrackDataEntity
    private var bundleToTrack: Bundle? = null
    private val joyAlertDialog = JoyAlertDialog()
    @Inject
    protected lateinit var appModel: AppModel
    @Inject
    lateinit var baseAnalyticEvent: BaseAnalyticEvent
    @Inject
    lateinit var analyticsUserGateway: AnalyticsUserGateway

    @JvmField
    protected var showMessageHandledByChild = false

    companion object {

        protected const val MARKET_APP = "market://details?id="
        private const val MARKET_URL = "https://play.google.com/store/apps/details?id="

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    protected val isOpenedSession: Boolean
        get() = appModel.isOpenedSession

    open fun isOpenedSessionRequired(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        //We force no night mode so that there're no issues with canvascore library in the UI.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        isRunning = true
        if (isOpenedSessionRequired() && !isOpenedSession) {
            clearThenNavigateToHost(applicationContext)
        }
    }

    override fun onResume() {
        super.onResume()
        isRunning = true
        if (!isFinishing) {
            attemptHandleRoutingEvent(appModel)
        }
    }

    override fun onPause() {
        super.onPause()
        isRunning = false
    }

    override fun onPostResume() {
        super.onPostResume()
        while (!deferredFragmentTransactions.isEmpty()) {
            deferredFragmentTransactions.remove().commit()
        }
    }

    protected fun replaceFragment(containerViewId: Int, fragment: Fragment) {
        if (!isRunning) {
            replaceFragmentDeferred(containerViewId, fragment)
        } else {
            replaceFragmentInternal(containerViewId, fragment)
        }
    }

    private fun replaceFragmentDeferred(containerViewId: Int, fragment: Fragment) {
        object : DeferredFragmentTransaction() {
            override fun commit() {
                replaceFragmentInternal(this.containerViewId, this.fragment)
            }
        }.apply {
            this.containerViewId = containerViewId
            this.fragment = fragment
        }.let(deferredFragmentTransactions::add)
    }

    /**
     * Replaces a [Fragment] to this activity's layout.
     *
     * @param containerViewId The container view to where replace the fragment.
     * @param fragment        The fragment to be replaced.
     */
    protected fun replaceFragmentInternal(containerViewId: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction().run {
            replace(containerViewId, fragment)
            commitAllowingStateLoss()
        }
    }

    protected fun replaceFragmentWithAnimationInternal(containerViewId: Int, fragment: Fragment, enter: Int, exit: Int) {
        supportFragmentManager.beginTransaction().run {
            setCustomAnimations(enter, exit)
            replace(containerViewId, fragment)
            commitAllowingStateLoss()
        }
    }

    fun showProgressDialog() {
        progressDialog = progressDialog ?: ProgressDialog(this, R.style.MyAlertDialogStyle)
        progressDialog?.run {
            if (!isFinishing && !isShowing) {
                setProgressStyle(ProgressDialog.STYLE_SPINNER)
                setCancelable(false)
                setMessage(getString(R.string.loading))
                show()
            }
        }
    }

    fun dismissProgressDialog() {
        if (!isFinishing) {
            progressDialog?.dismiss()
        }
    }

    @JvmOverloads
    fun showMessageDialog(message: String?,
                          title: String? = null,
                          positiveButton: String? = getString(R.string.accept),
                          positiveListener: DialogInterface.OnClickListener? = null,
                          negativeButton: String? = null,
                          negativeListener: DialogInterface.OnClickListener? = null) {
        if (isFinishing) {
            return
        }
        if (joyAlertDialog.isShowing && !isFinishing) {
            joyAlertDialog.dismiss()
        }
        joyAlertDialog.create(
            context = this,
            message = message,
            title = title,
            positiveButton = positiveButton,
            positiveListener = positiveListener,
            qrPositiveListener = getQrPositiveListener(),
            negativeButton = negativeButton,
            negativeListener = negativeListener
        )
    }

    private fun getQrPositiveListener(): DialogInterface.OnClickListener? {
        return qrDialogListener?.let {
            DialogInterface.OnClickListener { _: DialogInterface?, _: Int -> it.onClickPositive(Constant.ZERO) }
        }
    }

    private fun getErrorPositiveListener(): DialogInterface.OnClickListener? {
        return errorDialogListener?.let {
            DialogInterface.OnClickListener { _: DialogInterface?, _: Int -> it.onClickPositive(Constant.BACK_CODE) }
        }
    }

    //TODO() When Kotlin is completely implemented in Joy unify this with default parameters with the showMessageDialog() function
    fun showMessageDialog(title: String?, message: String?, bundle: Bundle, origin: String?, trackDataEntity: TrackDataEntity) {
        if (isFinishing) {
            return
        }
        joyAlertDialog.create(
            context = this,
            title = title,
            message = message,
            cancellableListener = { trackDialogCancel(trackDataEntity, origin, bundle) }
        )
    }

    private fun trackDialogCancel(trackDataEntity: TrackDataEntity, origin: String?, bundle: Bundle) = with(trackDataEntity) {
        step = Constant.HYPHEN_STRING
        typeProcess = Constant.HYPHEN_STRING
        eventCategory = Constant.HYPHEN_STRING
        eventAction = AnalyticsBaseConstant.CLICK
        eventLabel = AnalyticsBaseConstant.CLOSE
        currentScreen = Constant.HYPHEN_STRING
        logEvent = Constant.HYPHEN_STRING
        prepareDataForTrack(bundle)
        sendClickView()
    }

    private fun showForceUpdateDialog(message: String?) {
        if (isFinishing) {
            return
        }
        joyAlertDialog.create(
            context = this,
            title = getString(R.string.update_title),
            message = message,
            positiveListener = { _, _ -> getUpdateDialogPositiveClick() },
            cancellableListener = { finish() }
        )
    }

    private fun getUpdateDialogPositiveClick() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_APP + packageName)))
        } catch (anfe: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL + packageName)))
        }
        finish()
    }

    fun showDeleteMessageDialog(title: String?, message: String?) {
        if (isFinishing) {
            return
        }
        joyAlertDialog.create(
            context = this,
            message = message,
            title = title,
            positiveButton = getString(R.string.delete_fcc),
            positiveListener = { _, _ ->  onDeleteListener?.deleteFavoriteCc() },
            negativeButton = getString(R.string.cancel_fcc),
            negativeListener = { _, _ -> onDeleteListener?.cancelDeleteCProcess() }
        )
    }

    fun setAlertDialogListener(alertDialogListener: AlertDialogListener?) {
        this.alertDialogListener = alertDialogListener
    }

    fun setQrDialogListener(qrDialogListener: AlertDialogListener?) {
        this.qrDialogListener = qrDialogListener
    }

    fun setErrorDialogListener(positiveDialogListener: AlertDialogListener?) {
        this.errorDialogListener = positiveDialogListener
    }

    fun setAlertDialogListenerYesNoWithResponseCode(alertDialogListenerYesNoWithResponseCode: AlertDialogListenerYesNoWithResponseCode?) {
        this.alertDialogListenerYesNoWithResponseCode = alertDialogListenerYesNoWithResponseCode
    }

    fun showMessageDialogInterface(title: String?, message: String?, code: Int) {
        DialogUtil.showMessageDialogInterface(this, alertDialogListener, title, message, code)
    }

    fun showMessageDialogInterfaceYesNoWithResponseCode(
        title: String?,
        message: String?,
        code: Int,
        responseCode: String?,
        acceptText: String?,
        cancelText: String?
    ) {
        if (isFinishing) {
            return
        }
        joyAlertDialog.create(
            context = this,
            message = message,
            title = title,
            positiveButton = acceptText,
            positiveListener = { _, _ ->
                alertDialogListenerYesNoWithResponseCode?.onClickPositiveYesNoResponseCode(
                    code,
                    responseCode
                )
            },
            negativeButton = cancelText,
            negativeListener = { dialog, _ -> if (isFinishing) {
                dialog.dismiss()
            }}
        )
    }

    fun showMessageSession(message: String?) {
        appModel.removeCookiesFromMemory()
        DialogUtil.showMessageSession(this, message, false)
    }

    fun showMessageSession(message: String?, isForced: Boolean) {
        appModel.removeCookiesFromMemory()
        DialogUtil.showMessageSession(this, message, isForced)
    }

    fun showAlertDialogFinishOperation(nameOperationTitle: String?, nameOperationMessage: String?) {
        if (isFinishing) {
            return
        }
        val title = String.format(getString(R.string.cancel_this_operation_x), nameOperationTitle)
        val message = String.format(getString(R.string.are_you_sure_to_cancel_this_operation), nameOperationMessage)
        joyAlertDialog.create(
            context = this,
            title = title,
            message = message,
            positiveButton = getString(R.string.yes),
            positiveListener = { _, _ -> finish() },
            negativeButton = getString(R.string.no)
        )

    }

    fun navigateToWebView(url: String?) {
        val urlKey = GatesUtil.getGateUrl(appModel, url)
        val intent = Intent(this, NewWebViewActivity::class.java)
            .putExtra(WebViewConstant.URL_KEY, urlKey)
        startActivity(intent)
    }

    interface AlertDialogListenerYesNoWithResponseCode {
        fun onClickPositiveYesNoResponseCode(code: Int, responseCode: String?)
    }

    interface OnDeleteListener {
        fun deleteFavoriteCc()
        fun cancelDeleteCProcess()
    }

    protected fun setTrackDataProperties(trackDataEntity: TrackDataEntity) {
        this.trackDataEntity = trackDataEntity
    }

    open fun shouldExpireSession(): Boolean = true

    private fun sendScreenData() {
        AnalyticsTrackCurrentScreenEntityBuilder()
                .context(this)
                .screenBundle(Bundle())
                .logEventBundle(bundleToTrack)
                .currentScreen(trackDataEntity.currentScreen)
                .logEvent(trackDataEntity.logEvent)
                .build()
                .also(FirebaseUtil::trackCurrentScreen)
    }

    fun sendClickView() {
        AnalyticsTrackClickEntityBuilder()
                .context(this)
                .params(bundleToTrack)
                .eventCategory(trackDataEntity.eventCategory)
                .eventAction(trackDataEntity.eventAction)
                .eventLabel(trackDataEntity.eventLabel)
                .currentScreen(trackDataEntity.currentScreen)
                .logEvent(trackDataEntity.logEvent)
                .build()
                .also(FirebaseUtil::trackClickButton)
    }

    protected fun prepareDataForTrack(bundle: Bundle) {
        bundleToTrack = commonDataForTrack(bundle)
    }

    private fun commonDataForTrack(bundle: Bundle): Bundle = with(trackDataEntity) {
        if (!step.isNullOrEmpty()) {
            bundle.putString(AnalyticsBaseConstant.STEP, step)
        }
        if (!typeProcess.isNullOrEmpty()) {
            bundle.putString(AnalyticsBaseConstant.TYPE_PROCESS, typeProcess)
        }
        if (!typeSubProcess.isNullOrEmpty()) {
            bundle.putString(AnalyticsBaseConstant.TYPE_SUBPROCESS, typeSubProcess)
        }
        return bundle
    }

    fun sendData(trackDataEntity: TrackDataEntity, bundle: Bundle) {
        setTrackDataProperties(trackDataEntity)
        prepareDataForTrack(bundle)
        sendScreenData()
    }

    open fun showErrorMessage(throwable: Throwable?) {
        showErrorMessage(throwable, null)
    }

    open fun showErrorMessage(throwable: Throwable?, analyticsMessage: ((String, String) -> Unit)?) = when (throwable) {
        is FinishedSessionException -> showMessageSession(throwable.message)
        is ForceUpdateException -> showForceUpdateDialog(throwable.message)
        else -> {
            val retrofitException = handleShowMessageOnChild(throwable)
            if (showMessageHandledByChild) {
                showMessageHandledByChild = false
            } else {
                analyticsMessage?.invoke(retrofitException.responseCode, retrofitException.message
                        ?: Constant.EMPTY_STRING)
                showMessage(retrofitException)
            }
        }
    }

    //Method to be overriden by child in case child handled show message
    protected open fun handleShowMessageOnChild(throwable: Throwable?): RetrofitException {
        return getRetrofitException(throwable)
    }

    //Util method for mapping a throwable to an instance of Retrofit Exception
    protected fun getRetrofitException(throwable: Throwable?): RetrofitException {
        return DefaultErrorBundle(throwable as Exception?).let { errorBundle ->
            ErrorMessageFactory.createWithCode(this, errorBundle.exception)
        }
    }

    protected fun showMessage(retrofitException: RetrofitException) = with(retrofitException) {
        when (responseCode) {
            Constant.ERROR_FIRST_TRY -> showMessageDialog(message)
            Constant.ERROR_THIRD_TRY -> showMessageDialogInterface(null, message, Constant.BACK_CODE)
            Constant.SCOTIA_POINTS_DETAIL_ERROR -> showMessageDialog(getString(R.string.scotia_points_default_message))
            Constant.RECIPIENT_DETAIL_ERROR -> showMessageDialogDetailError(message)
            else -> showMessageDialog(message, positiveListener = getErrorPositiveListener())
        }
    }

    fun showDisabledAlert(templateType: String?) {
        when {
            TemplatesUtil.DISABLED.equals(templateType, ignoreCase = true) -> {
                showMessageDialog(getString(R.string.templates_disabled_warning))
            }
            TemplatesUtil.DISABLED_RP.equals(templateType, ignoreCase = true) -> {
                val bundle = Bundle()
                bundle.putString(
                    AnalyticsBaseConstant.PREVIOUS_SECTION,
                    PlinAffiliationAlertAnalyticsConstants.PREVIOUS_SECTION_LOGIN
                )
                startActivity(
                    Intent(this, RestrictedProfileAlertActivity::class.java).putExtras(bundle)
                )
            }
            TemplatesUtil.DISABLED_OM.equals(templateType, ignoreCase = true) -> {
                startActivity(Intent(this, OpenMarketAlertActivity::class.java))
            }
        }
    }

    fun getInputAlertDialogView(resource: Int): View {
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        return LayoutInflater.from(this).inflate(resource, viewGroup, false)
    }

    fun getAlertDialog(view: View?): AlertDialog {
        return AlertDialog.Builder(this).setView(view).create()
    }

    private fun showMessageDialogDetailError(message: String?) {
        val messageError: String = message ?: return
        val listener = DialogInterface.OnClickListener { _: DialogInterface?, _: Int -> goToHome() }
        showMessageDialog(
            message = messageError,
            positiveButton = getString(R.string.finished_to_home),
            positiveListener = listener
        )
    }

    private fun goToHome() {
        val intent: Intent = NavUtils.getParentActivityIntent(this) ?: return
        setResult(RESULT_OK, intent)
        NavUtils.navigateUpFromSameTask(this)
    }

    override fun onDestroy() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = false
        super.onDestroy()
    }
}
