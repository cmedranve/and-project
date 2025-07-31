package pe.com.scotiabank.blpm.android.client.app

import android.app.Activity
import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import br.com.hst.issuergp.core.IssuerGP
import com.akamai.botman.CYFMonitor
import com.scotiabank.enhancements.handling.*
import com.scotiabank.enhancements.weakreference.getEmptyWeak
import com.scotiabank.sdk.approuting.AppRouterEvent
import com.scotiabank.sdk.crasherrorreporting.ThrowableReporter
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.atmcardhub.AtmCardHostActivity
import pe.com.scotiabank.blpm.android.client.base.BaseActivity
import pe.com.scotiabank.blpm.android.client.base.approuting.*
import pe.com.scotiabank.blpm.android.client.base.checksecurity.RootChecking
import pe.com.scotiabank.blpm.android.client.base.font.TypefaceProvider
import pe.com.scotiabank.blpm.android.client.base.network.EnvironmentHolder
import pe.com.scotiabank.blpm.android.client.base.session.SessionHost
import pe.com.scotiabank.blpm.android.client.base.session.SessionModel
import pe.com.scotiabank.blpm.android.client.cardsettings.CardSettingHubHostActivity
import pe.com.scotiabank.blpm.android.client.entrypoint.DeepLinkActivity
import pe.com.scotiabank.blpm.android.client.goal.create.GoalCreateHubHostActivity
import pe.com.scotiabank.blpm.android.client.host.shared.HostActivity
import pe.com.scotiabank.blpm.android.client.limit.LimitHostActivity
import pe.com.scotiabank.blpm.android.client.medallia.InitializerOfAppMedallia
import pe.com.scotiabank.blpm.android.client.newpayment.legacybridge.PaymentHostActivity
import pe.com.scotiabank.blpm.android.client.newqrpayment.QrPaymentHostActivity
import pe.com.scotiabank.blpm.android.client.newwebview.enableWebContentsDebugging
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.legacy.DigitalTokenHostActivity
import pe.com.scotiabank.blpm.android.client.p2p.helpcenter.HelpCenterHostActivity
import pe.com.scotiabank.blpm.android.client.p2p.home.LockTimerFinished
import pe.com.scotiabank.blpm.android.client.payment.PaymentHubHostActivity
import pe.com.scotiabank.blpm.android.client.pfm.goal.detail.GoalDetailHostActivity
import pe.com.scotiabank.blpm.android.client.profilesettings.newchangepassword.ChangePasswordActivity
import pe.com.scotiabank.blpm.android.client.profilesettings.security.userdeletion.UserDeletionActivity
import pe.com.scotiabank.blpm.android.client.rewards.group.RewardsHubHostActivity
import pe.com.scotiabank.blpm.android.client.rewards.single.SingleErasureHostActivity
import pe.com.scotiabank.blpm.android.client.scotiapay.affiliation.ScotiaPayAffiliationActivity
import pe.com.scotiabank.blpm.android.client.scotiapay.shared.info.ScotiaPayInfoActivity
import pe.com.scotiabank.blpm.android.client.scotiapay.shared.qr.ScotiaPayQrActivity
import pe.com.scotiabank.blpm.android.client.tasknav.bringActiveScreenToFront
import pe.com.scotiabank.blpm.android.client.tasknav.navigateToHostWhenAppCreatedByLink
import pe.com.scotiabank.blpm.android.client.tasknav.navigateToPersonalDashboard
import pe.com.scotiabank.blpm.android.client.transfer.host.TransferHubHostActivity
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

class AppModel(
    val weakApp: WeakReference<out Application?>,
    throwableReporter: ThrowableReporter,
    val environmentHolder: EnvironmentHolder,
    private val handlerOfCheckSecurity: HandlerOfCheckSecurity,
    private val browserOpener: BrowserOpener,
    typefaceProvider: TypefaceProvider,
    cookieCleaner: CookieCleaner,
    pushOtpCookieCleaner: PushOtpCookieCleaner,
    legacyStaticKeyCleaner: LegacyStaticKeyCleaner,
    commercialNotificationCleaner: CommercialNotificationCleaner,
    initializerOfAppMedallia: InitializerOfAppMedallia,
    storeOfAppPackageInfo: StoreOfAppPackageInfo,
    storeOfAssistanceUi: StoreOfAssistanceUi,
    builderOfTimerFacade: MutableTimerFacade.Builder,
    builderOfAppRoutingDelegate: AppRoutingDelegate.Builder,
    val directoryDeleterFacade: DirectoryDeleterFacade,
    sessionModel: SessionModel,
    delegateOfFbLogger: DelegateOfFbLogger,
    ): TypefaceProvider by typefaceProvider,
    EnvironmentHolder by environmentHolder,
    StoreOfAppPackageInfo by storeOfAppPackageInfo,
    StoreOfAssistanceUi by storeOfAssistanceUi,
    StoreOfActivityLifecycle,
    TimerFacade,
    AppRoutingModel,
    HolderOfServerDate,
    SessionHost by sessionModel,
    HolderOfFbLogger by delegateOfFbLogger,
    RootChecking by handlerOfCheckSecurity
{

    override var isAppCreatedByMainLauncher: Boolean = false
        private set

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            ActivityEvent::class,
            InstancePredicate(ActivityEvent::filterOnCreated),
            InstanceHandler(::onActivityCreated)
        )
        .add(
            ActivityEvent::class,
            InstancePredicate(ActivityEvent::filterOnResumed),
            InstanceHandler(::onActivityResumed)
        )
        .add(
            ActivityEvent::class,
            InstancePredicate(ActivityEvent::filterOnPaused),
            InstanceHandler(::onActivityPaused)
        )
        .add(
            BrowserEvent::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::openBrowser)
        )
        .add(
            WebViewEvent::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::routeToEitherNativeOrWebView)
        )
        .add(
            NavigationEvent::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::routeToEitherNativeOrWebView)
        )
        .build()
    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private var weakActiveActivity: WeakReference<out Activity?> = getEmptyWeak()

    private val timerFacade: TimerFacade = builderOfTimerFacade.build(
        Runnable(::onTimerFinished),
        Runnable(::onPlinLockTimerFinished),
    )

    private val appRoutingModel: AppRoutingModel = builderOfAppRoutingDelegate.build(selfReceiver)

    override var serverDate: String = Constant.EMPTY_STRING

    init {
        enableWebContentsDebugging()
        weakApp.get()?.let(::initializeFirebaseAnalytics)
        setUpUncaughtExceptionHandler(throwableReporter)
        registerActivityLifecycleCallbacks(selfReceiver, weakApp)
        handlerOfCheckSecurity.checkIntegrity()
        directoryDeleterFacade.deleteSecuredDirectories()
        cookieCleaner.clearCookiesIfFirstInstall()
        pushOtpCookieCleaner.clearCookiesIfAppUpdated()
        legacyStaticKeyCleaner.clean()
        commercialNotificationCleaner.clearLegacyNotificationFlags()
        weakApp.get()?.let(initializerOfAppMedallia::initialize)
        weakApp.get()?.let(CYFMonitor::initialize)
        weakApp.get()?.let(IssuerGP::init)
        weakApp.get()?.let(::configureAppDynamicShortcuts)
        weakApp.get()?.let(::createNotificationChannel)
    }

    private fun onActivityCreated(activityEvent: ActivityEvent) {
        val weakActivity: WeakReference<out Activity?> = activityEvent.weakActivity
        weakActivity.get()?.window?.setHideOverlayWindows()
        weakActivity.get()?.let(::disableOverviewScreenshot)
    }

    private fun onActivityResumed(activityEvent: ActivityEvent) {
        val weakActivity: WeakReference<out Activity?> = activityEvent.weakActivity
        applyOrClearSecureSurface(weakActivity)
        attemptPutMetadataOfFirstActivity(weakActivity)
        val shouldExpireSession: Boolean = weakActivity.get()?.let(::shouldExpireSession) ?: false
        if (shouldExpireSession) timerFacade.startInactivityTimer() else timerFacade.cancelTimers()
    }

    private fun applyOrClearSecureSurface(weakActivity: WeakReference<out Activity?>) {
        val activityTypes: List<KClass<out AppCompatActivity>> = listOf(
            AtmCardHostActivity::class,
            CardSettingHubHostActivity::class,
            GoalCreateHubHostActivity::class,
            LimitHostActivity::class,
            PaymentHostActivity::class,
            QrPaymentHostActivity::class,
            DigitalTokenHostActivity::class,
            PaymentHubHostActivity::class,
            GoalDetailHostActivity::class,
            ChangePasswordActivity::class,
            UserDeletionActivity::class,
            RewardsHubHostActivity::class,
            SingleErasureHostActivity::class,
            ScotiaPayAffiliationActivity::class,
            ScotiaPayInfoActivity::class,
            ScotiaPayQrActivity::class,
            TransferHubHostActivity::class,
            HelpCenterHostActivity::class,
        )
        val isActivityTypeFound: Boolean = weakActivity.get()
            ?.let { activity -> activityTypes.contains(activity::class) }
            ?: false
        if (isActivityTypeFound) return
        weakActivity.get()?.window?.clearWindowFromSecureSurface()
    }

    private fun attemptPutMetadataOfFirstActivity(weakActivity: WeakReference<out Activity?>) {
        if (weakActivity.get() is HostActivity || weakActivity.get() is DeepLinkActivity) return
        isAppCreatedByMainLauncher = true
        weakActiveActivity = weakActivity
    }

    private fun onActivityPaused(activityEvent: ActivityEvent) {
        val weakActivity: WeakReference<out Activity?> = activityEvent.weakActivity
        weakActivity.get()?.window?.applySecureSurfaceOnWindow()
    }

    private fun onTimerFinished() {
        receiveEvent(FinishedSessionEvent)
        val baseActivity: BaseActivity = weakActiveActivity.get() as? BaseActivity ?: return
        val message: String = baseActivity.getString(R.string.forced_end_session_message)
        baseActivity.showMessageSession(message, true)
    }

    override fun startGlobalTimer() = timerFacade.startGlobalTimer()

    override fun startInactivityTimer() = timerFacade.startInactivityTimer()

    override fun cancelTimers() = timerFacade.cancelTimers()

    private fun onPlinLockTimerFinished() {
        receiveEvent(LockTimerFinished)
    }

    override fun startPlinLockTimer() = timerFacade.startPlinLockTimer()

    override fun resetPlinLockTimer() = timerFacade.resetPlinLockTimer()

    override fun handleLink(deepLinkUri: String) {
        appRoutingModel.handleLink(deepLinkUri)
    }

    override fun attemptFindRoutingEvent(): AppRouterEvent? {
        return appRoutingModel.attemptFindRoutingEvent()
    }

    private fun openBrowser(event: BrowserEvent) {
        browserOpener.open(event, weakActiveActivity)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun routeToEitherNativeOrWebView(event: SealedRouterEvent) = when {
        // When app is created either by a dynamic link or a push notification.
        isAppCreatedByMainLauncher.not() -> attemptNavigatingToLaunchWhenAppCreatedByLink()
        // When user is logged-in and app has been either at the Dashboard or at another screen
        // on top of Dashboard.
        isOpenedSession -> weakActiveActivity.get()?.let(::navigateToPersonalDashboard)
        // When user is logged-out and app has been either at an screen of Login or
        // Password Recovery or On-boarding flows. Open the active screen so then the event
        // on hold will be routed as soon as the user is logged-in.
        else -> attemptBringingActiveScreenToFront()
    }

    fun attemptNavigatingToLaunchWhenAppCreatedByLink() {
        weakApp.get()?.let(::navigateToHostWhenAppCreatedByLink)
    }

    fun attemptBringingActiveScreenToFront() {
        weakActiveActivity.get()?.let(::bringActiveScreenToFront)
    }

    override fun clearRoutingEvent() {
        appRoutingModel.clearRoutingEvent()
    }
}
