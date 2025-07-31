package pe.com.scotiabank.blpm.android.client.base

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import pe.com.scotiabank.blpm.android.analytics.factories.DashboardAnalyticsFactory
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.assistance.ContextualAssistanceHelper
import pe.com.scotiabank.blpm.android.client.assistance.model.AssistanceItem
import pe.com.scotiabank.blpm.android.client.assistance.model.CoachMarkItem
import pe.com.scotiabank.blpm.android.client.assistance.model.CoachMarkModel
import pe.com.scotiabank.blpm.android.client.assistance.model.FullScreenItem
import pe.com.scotiabank.blpm.android.client.base.session.entities.CommercialNotificationStatus
import pe.com.scotiabank.blpm.android.client.biometric.enrollment.EnrollmentBiometricActivity
import pe.com.scotiabank.blpm.android.client.features.dashboard.products.GoalDialogEvent
import pe.com.scotiabank.blpm.android.client.messaging.NotificationSetting
import pe.com.scotiabank.blpm.android.client.messaging.activate.ActivateNotificationActivity
import pe.com.scotiabank.blpm.android.client.messaging.disabledso.DisabledNotificationsOsActivity
import pe.com.scotiabank.blpm.android.client.messaging.hasNotificationsPermission
import pe.com.scotiabank.blpm.android.client.messaging.otherdevice.ActivatedOtherDeviceNotificationActivity
import pe.com.scotiabank.blpm.android.client.newdashboard.ProfileRestrictedEvent
import pe.com.scotiabank.blpm.android.client.newdashboard.NewDashboardActivity
import pe.com.scotiabank.blpm.android.client.newwebview.NewWebViewActivity
import pe.com.scotiabank.blpm.android.client.profilesettings.security.MyAccountSecurityActivity
import pe.com.scotiabank.blpm.android.client.security.NotificationSharedPreferences
import pe.com.scotiabank.blpm.android.client.util.CoachMarkUtil
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.Constant.DASHBOARD_SOURCE_VALUE
import pe.com.scotiabank.blpm.android.client.util.Constant.UPDATE_PERSONAL_DATA_PARAM
import pe.com.scotiabank.blpm.android.client.util.ContextualAssistanceConstant
import pe.com.scotiabank.blpm.android.client.util.EventWrapper
import pe.com.scotiabank.blpm.android.client.util.GatesUtil
import pe.com.scotiabank.blpm.android.client.util.ProfileTypeUtil
import pe.com.scotiabank.blpm.android.client.util.SharedPreferencesUtil
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil
import pe.com.scotiabank.blpm.android.client.util.accesibility.AccessibilityUtil
import pe.com.scotiabank.blpm.android.client.util.analytics.MenuAnalytics
import pe.com.scotiabank.blpm.android.client.util.analytics.TrackDataEntity
import pe.com.scotiabank.blpm.android.client.util.webview.WebViewConstant
import pe.com.scotiabank.blpm.android.client.whatsnew.WhatsNewActivity
import pe.com.scotiabank.blpm.android.client.whatsnew.model.WhatsNew
import pe.com.scotiabank.blpm.android.ui.list.items.richtext.style.TypefaceSpan
import pe.com.scotiabank.blpm.android.ui.information.BubbleCoachMark
import pe.com.scotiabank.blpm.android.ui.information.CoachMark
import pe.com.scotiabank.joy.android.data.BuildConfig
import javax.inject.Inject

abstract class BaseBindingActivity<B : ViewBinding> : BaseActivity(),
    ContextualAssistanceInterface {

    companion object {
        const val PREVIOUS_SECTION_KEY_FIREBASE = "PREVIOUS_SECTION_KEY_FIREBASE"
        const val HAS_NOTIFICATIONS_PERMISSION_ENABLED = "HAS_NOTIFICATIONS_PERMISSION_ENABLED"
    }

    protected lateinit var binding: B

    var toolbar: Toolbar? = null
    private var _toolbarTitleView: TextView? = null
    private var isBlackArrow = true
    private var _settingToolbar = false
    private lateinit var trackDataEntity: TrackDataEntity

    // Contextual Assistance
    private var items: List<AssistanceItem> = emptyList()
    private var isInitialize = false
    private var _screenNameTag: String = Constant.EMPTY_STRING
    private var positionContextualAssistanceList = 0

    @JvmField
    @Inject
    var notificationSharedPreferences: NotificationSharedPreferences? = null

    private val launcherOfActivateNotification = registerLauncherOfActivateNotification()

    private val launcherOfActivateTxBiometric = registerLauncherOfActivateTxBiometric()

    private fun registerLauncherOfActivateTxBiometric(): ActivityResultLauncher<Intent> {
        val callback: ActivityResultCallback<ActivityResult> = ActivityResultCallback(
            ::handleResultFromActivateTxBiometric
        )
        return registerLauncherForActivityResult(callback)
    }

    private fun registerLauncherOfActivateNotification(): ActivityResultLauncher<Intent> {
        val callback: ActivityResultCallback<ActivityResult> = ActivityResultCallback { result ->
            handleResultFromActivateNotification(result, true)
        }
        return registerLauncherForActivityResult(callback)
    }

    private val launcherOfActivatedAnotherDeviceNotification =
        registerLauncherOfActivatedAnotherDeviceNotification()

    private fun registerLauncherOfActivatedAnotherDeviceNotification(): ActivityResultLauncher<Intent> {
        val callback: ActivityResultCallback<ActivityResult> = ActivityResultCallback { result ->
            handleResultFromActivateNotification(result, false)
        }
        return registerLauncherForActivityResult(callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getBindingInflater().inflate(layoutInflater)
        if (this !is BaseChildV2Activity<*, *>) {
            val view = binding.root
            setContentView(view)
        }
        _screenNameTag = screenNameTag
        _settingToolbar = getSettingToolbar()
        trackDataEntity = TrackDataEntity()
        toolbar = findViewById(R.id.toolbar)
        _toolbarTitleView = findViewById(R.id.title_toolbar)
        if (_settingToolbar) {
            setupToolbar()
            setIconToolbar(isBlackArrow)
        }
        if (!isFinishing && (isOpenedSessionRequired() && isOpenedSession || !isOpenedSessionRequired())) {
            additionalInitializer()
        }
        prepareContextualAssistance()
    }

    protected abstract fun getBindingInflater(): BindingInflaterOfActivity<B>

    private fun prepareContextualAssistance() {
        // Early return if we are skipping directly to QR activity to avoid registering coachmarks
        // as shown when the user won't see them.
        if (intent.getBooleanExtra(Constant.QR_DEEPLINK, false)) {
            return
        }
        val operation = TemplatesUtil.getOperation(
            appModel.navigationTemplate,
            TemplatesUtil.CROSS_FUNCTIONALITY_KEY,
            TemplatesUtil.CONTEXTUAL_ASSISTANCE_KEY
        )
        val isContextualAssistanceVisible: Boolean = operation.isVisible
        if (isContextualAssistanceVisible) {
            showContextualAssistance()
        } else {
            items = mutableListOf()
        }
    }

    private fun showContextualAssistance() {
        items = appModel.contextualAssistance.getScreenByTag(_screenNameTag)
        if (items.isEmpty()) return

        items = validateContextualAssistance(items)

        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            when {
                isReadyForDisplayUpdatePersonalDataFullscreen() -> launchUpdatePersonalDataWebView()
                isGoingToOpenDisabledOsScreen() -> showNotificationsAreDisabled()
                appModel.whatsNew.items.isNotEmpty() -> showNewFeaturesActivity()
                statusOfNotificationActivation() -> setupNotificationActivation()
                validateRestrictedProfile() -> appModel.receiveEvent(ProfileRestrictedEvent.SHOW_RESTRICTED)
                validateOpenMarket() -> appModel.receiveEvent(ProfileRestrictedEvent.SHOW_OPEN_MARKET)
                validatePokActivation() -> showPokActivation()
                items.isEmpty() -> appModel.receiveEvent(GoalDialogEvent.SHOW_GOAL_DIALOG)
                else -> items[positionContextualAssistanceList]
                    .let(this::prepareContextualAssistance)
            }

        }
        handler.postDelayed(runnable, Constant.CONTEXTUAL_ASSISTANCE_DELAY.toLong())
    }

    open fun showPokActivation() {
        val intent = Intent(this, EnrollmentBiometricActivity::class.java)
        launcherOfActivateTxBiometric.launch(intent)
    }

    open fun validatePokActivation(): Boolean = false

    private fun showNewFeaturesActivity() {
        appModel.whatsNew = WhatsNew(
            appModel.whatsNew.title,
            appModel.whatsNew.items
        )
        Intent(this, WhatsNewActivity::class.java).let(::startActivity)
    }

    private fun validateRestrictedProfile(): Boolean {
        val isRestrictedProfile: Boolean = ProfileTypeUtil.checkRestrictedProfile(appModel.profile)
        return !SharedPreferencesUtil.isRestrictedAlertViewed(this) && isRestrictedProfile
    }

    private fun validateOpenMarket(): Boolean {
        val isOpenMarketProfile: Boolean = ProfileTypeUtil.checkNonClientProfile(appModel.profile)
        return !SharedPreferencesUtil.isOpenMarketAlertViewed(this) && isOpenMarketProfile
    }

    override fun getScreenNameTag(): String = Constant.EMPTY_STRING

    /**
     * This function returns a boolean that controls the toolbar setup
     *
     * @return boolean that can be override
     */
    open fun getSettingToolbar(): Boolean = true

    private fun validateContextualAssistance(items: List<AssistanceItem>): List<AssistanceItem> {
        return ContextualAssistanceHelper.filterContextualAssistance(
            appModel,
            applicationContext,
            items,
        )
    }

    private fun prepareContextualAssistance(assistanceItem: AssistanceItem) {
        when {
            FullScreenItem.CLASS.equals(assistanceItem.itemClass, ignoreCase = true) -> {
                prepareFullScreenAssistance(assistanceItem)
            }
            CoachMarkItem.CLASS.equals(assistanceItem.itemClass, ignoreCase = true) -> {
                prepareCoachMark(assistanceItem)
            }
        }
    }

    private fun prepareCoachMark(assistanceItem: AssistanceItem) {
        val coachMarkItem = assistanceItem as CoachMarkItem
        if (shouldSkipCoachMark(coachMarkItem)) {
            return
        }
        Handler(Looper.getMainLooper()).postDelayed(
            { showCoachMark(coachMarkItem) },
            Constant.COACH_MARK_DELAY.toLong()
        )
    }

    private fun prepareFullScreenAssistance(assistanceItem: AssistanceItem) {
        //We have validated items for nullability already in showContextualAssistance()
        ContextualAssistanceHelper.saveViewedFinal(
            applicationContext,
            items[positionContextualAssistanceList].id
        )
        if (assistanceItem.id.contains(ContextualAssistanceConstant.LOOP_2_PAY) && this is NewDashboardActivity) {
            (this as NewDashboardActivity).validateAppearLoop2Pay()
        }
    }

    private fun shouldSkipCoachMark(coachMarkItem: CoachMarkItem): Boolean {
        val correctId =
            coachMarkItem.id.equals(ContextualAssistanceConstant.NEW_L2P, ignoreCase = true)
        if (this is NewDashboardActivity) {
            val shouldAppear = (this as NewDashboardActivity).validateAppearLoop2PayCoachmark()
            return correctId && !shouldAppear
        }
        return false
    }

    private fun showCoachMark(coachMarkItem: CoachMarkItem) {
        findView(coachMarkItem.viewId)?.let { view ->
            showLocalCoachmark(view, coachMarkItem)
        } ?: sendShowCoachMark(coachMarkItem)
    }

    private fun showLocalCoachmark(view: View, coachMarkItem: CoachMarkItem) {
        val bubbleCoachMark = buildCoachMark(view, coachMarkItem)
        if (isInitialize) {
            bubbleCoachMark.show()
        } else {
            isInitialize = true
            window.decorView.rootView.post { bubbleCoachMark.show() }
        }
    }

    private fun sendShowCoachMark(coachMarkItem: CoachMarkItem) {
        //We have validated items for nullability already in showContextualAssistance()
        ContextualAssistanceHelper.saveViewedFinal(
            applicationContext,
            items[positionContextualAssistanceList].id
        )
        val coachMarkModel = CoachMarkModel(
            _screenNameTag,
            coachMarkItem.id,
            coachMarkItem.viewId,
            coachMarkItem.title,
            coachMarkItem.description,
            coachMarkItem.buttonType
        )
        appModel.receiveEvent(coachMarkModel)
    }

    private fun findView(byId: String?): View? {
        return when (byId) {
            ContextualAssistanceConstant.NAVIGATION_MORE -> findViewById(R.id.navigation_profile)
            ContextualAssistanceConstant.NAVIGATION_L2P -> findViewById(R.id.navigation_contacts)
            else -> {
                val identifier = resources.getIdentifier(
                    byId,
                    ContextualAssistanceConstant.ID_IDENTIFIER,
                    applicationContext.packageName
                )
                findViewById(identifier)
            }
        }
    }

    override fun buildCoachMark(view: View, coachMarkItem: CoachMarkItem): CoachMark {
        val primaryButtonListener: View.OnClickListener?
        val secondaryButtonListener: View.OnClickListener?
        when (coachMarkItem.buttonType) {
            BubbleCoachMark.TYPE_NEXT -> {
                primaryButtonListener = View.OnClickListener { nextContextualAssistance() }
                secondaryButtonListener = View.OnClickListener { skipCouchMark() }
            }
            BubbleCoachMark.TYPE_TRY -> {
                primaryButtonListener = View.OnClickListener { view.callOnClick() }
                secondaryButtonListener = View.OnClickListener { nextContextualAssistance() }
            }
            BubbleCoachMark.TYPE_FINISH -> {
                primaryButtonListener = View.OnClickListener { nextContextualAssistance() }
                secondaryButtonListener = null
            }
            else -> {
                primaryButtonListener = null
                secondaryButtonListener = null
            }
        }
        var drawable: Drawable? = null
        if (coachMarkItem.viewId == ContextualAssistanceConstant.NAVIGATION_L2P) {
            drawable = AppCompatResources.getDrawable(this, pe.com.scotiabank.blpm.android.ui.R.drawable.il_p2p_logo_42)
        }
        return CoachMarkUtil.buildCoachMark(
            view,
            coachMarkItem,
            this,
            drawable,
            primaryButtonListener,
            secondaryButtonListener,
            null
        )!!
    }

    private fun skipCouchMark() {
        var assistanceItem: AssistanceItem
        do {
            //We have validated items for nullability already in showContextualAssistance()
            assistanceItem = items[positionContextualAssistanceList]
            if (CoachMarkItem.CLASS.equals(assistanceItem.itemClass, ignoreCase = true)) {
                ContextualAssistanceHelper.saveViewedFinal(applicationContext, assistanceItem.id)
                positionContextualAssistanceList++
            } else {
                break
            }
        } while (positionContextualAssistanceList < items.size)
        items = validateContextualAssistance(items)
        positionContextualAssistanceList = Constant.ZERO
        if (items.isNotEmpty()) {
            val item = items[positionContextualAssistanceList]
            prepareContextualAssistance(item)
        }
    }

    override fun nextContextualAssistance() {
        //We have validated items for nullability already in showContextualAssistance()
        positionContextualAssistanceList++
        if (positionContextualAssistanceList < items.size) {
            val assistanceItem = items[positionContextualAssistanceList]
            prepareContextualAssistance(assistanceItem)
        } else {
            appModel.receiveEvent(GoalDialogEvent.SHOW_GOAL_DIALOG)
        }
    }

    protected fun setupToolbar() {
        toolbar?.setBackgroundColor(ContextCompat.getColor(this, com.scotiabank.canvascore.R.color.canvascore_brand_white))
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, com.scotiabank.canvascore.R.color.canvascore_brand_black))
        _toolbarTitleView?.setTextColor(
            ContextCompat.getColor(
                this,
                com.scotiabank.canvascore.R.color.canvascore_brand_black
            )
        )
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = Constant.EMPTY_STRING
        }
        getToolbarTitle()?.let(this::setupToolbarTitle)
    }

    protected fun setupToolbarTitle(toolbarTitle: String) {
        _toolbarTitleView?.text = if (intent.hasExtra(Constant.TOOLBAR_TITLE)) {
            intent.getStringExtra(Constant.TOOLBAR_TITLE)
        } else {
            toolbarTitle
        }
        AccessibilityUtil.setHeading(_toolbarTitleView)
    }

    protected fun setItemMenuTextColor(menu: Menu, @ColorRes colorRes: Int) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val foregroundColorSpan = ForegroundColorSpan(ContextCompat.getColor(this, colorRes))
            val spanString = SpannableString(item.title.toString()).apply {
                setSpan(foregroundColorSpan, 0, length, 0)
                setSpan(TypefaceSpan(appModel.boldTypeface), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            item.title = spanString
        }
    }

    protected abstract fun additionalInitializer()
    protected abstract fun getToolbarTitle(): String?

    protected fun setToolbarTitle(title: String?) {
        _toolbarTitleView?.text = title
    }

    protected fun setIconToolbar(isDefault: Boolean) {
        isBlackArrow = isDefault
        if (isBlackArrow) {
            toolbar?.setNavigationIcon(com.scotiabank.canvascore.R.drawable.canvascore_icon_back)
        } else {
            toolbar?.apply {
                setNavigationIcon(com.scotiabank.canvascore.R.drawable.canvascore_bottomsheet_close)
                setNavigationContentDescription(R.string.close_accessibility)
            }
        }
    }

    open fun observeShowHideLoading(loadingV2: EventWrapper<Boolean>?) {
        loadingV2?.run {
            if (!hasBeenHandled() && contentIfNotHandled) {
                showProgressDialog()
            } else {
                dismissProgressDialog()
            }
        }
    }

    open fun observeErrorMessage(errorMessage: EventWrapper<BaseAppearErrorMessage>?) {
        errorMessage?.run {
            if (!hasBeenHandled()) {
                showErrorMessage(contentIfNotHandled.throwable)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            sendViewClickDataToFireBase()
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        saveViewedContextualAssistance()
        super.onDestroy()
    }

    private fun saveViewedContextualAssistance() = items.run {
        if (isNotEmpty()
            && positionContextualAssistanceList < size - 1
            && get(positionContextualAssistanceList).id == ContextualAssistanceConstant.LOOP_2_PAY
        ) {
            val assistanceItem = get(positionContextualAssistanceList + 1)
            ContextualAssistanceHelper.saveViewedFinal(applicationContext, assistanceItem.id)
        }
    }

    private fun sendViewClickDataToFireBase() {
        trackDataEntity.apply {
            eventCategory = MenuAnalytics.PROPERTY_EVENT_CATEGORY_VALUE_TOP_NAV
            eventAction = MenuAnalytics.PROPERTY_EVENT_ACTION_VALUE
            eventLabel = MenuAnalytics.PROPERTY_EVENT_LABEL_VALUE_BACK
            currentScreen = Constant.EMPTY_STRING
            step = Constant.EMPTY_STRING
            typeProcess = Constant.EMPTY_STRING
            logEvent = MenuAnalytics.PROPERTY_EVENT_NAME
        }
        setTrackDataProperties(trackDataEntity)
        prepareDataForTrack(Bundle())
        sendClickView()
    }

    protected fun hideIcon() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setHomeButtonEnabled(false)
        }
    }

    private fun statusOfNotificationActivation(): Boolean {
        val isLinkageAllowed = TemplatesUtil.getOperation(
            appModel.navigationTemplate,
            TemplatesUtil.PUSH_NOTIFICATION,
            NotificationSetting.NOTIFICATION_VINCULATION_OPTION
        ).isVisible

        if (!isLinkageAllowed) return false

        val skipOption = NotificationSetting.getSkipValue(notificationSharedPreferences)
        if (skipOption) return false

        val notOption = NotificationSetting.getNotValue(notificationSharedPreferences)
        if (notOption) return false

        if (CommercialNotificationStatus.OTHER_USER == appModel.commercialNotificationStatus) {
            return true
        }

        val shouldActivationAppear: Boolean = NotificationSetting.shouldActivationAppear(notificationSharedPreferences)
        if (shouldActivationAppear.not()) return false

        return CommercialNotificationStatus.NEW_USER == appModel.commercialNotificationStatus
    }

    private fun isGoingToOpenDisabledOsScreen(): Boolean {
        val isDeviceLinked = !notificationSharedPreferences
            ?.let(NotificationSetting::getVinculationId)
            .isNullOrEmpty()
        val isNotificationAllowed = NotificationManagerCompat.from(this).areNotificationsEnabled()
        return isDeviceLinked && !isNotificationAllowed
    }

    private fun setupNotificationActivation(): Unit = when (appModel.commercialNotificationStatus) {
        CommercialNotificationStatus.NEW_USER -> showUIForActivateNotification()
        CommercialNotificationStatus.OTHER_USER -> showUIForActivateNotificationFromAnotherDevice()
        else -> Unit
    }

    private fun showUIForActivateNotification() {
        val intent = Intent(this, ActivateNotificationActivity::class.java)
            .putExtra(PREVIOUS_SECTION_KEY_FIREBASE, DashboardAnalyticsFactory.DASHBOARD)
            .putExtra(HAS_NOTIFICATIONS_PERMISSION_ENABLED, hasNotificationsPermission(this))
        launcherOfActivateNotification.launch(intent)
    }

    private fun showUIForActivateNotificationFromAnotherDevice() {
        if (appModel.profile.isPushOtpEnabled) return
        val intent = Intent(this, ActivatedOtherDeviceNotificationActivity::class.java)
            .putExtra(PREVIOUS_SECTION_KEY_FIREBASE, DashboardAnalyticsFactory.DASHBOARD)
            .putExtra(HAS_NOTIFICATIONS_PERMISSION_ENABLED, hasNotificationsPermission(this))
        launcherOfActivatedAnotherDeviceNotification.launch(intent)
    }

    private fun showNotificationsAreDisabled() {
        if (appModel.profile.isPushOtpEnabled) return
        val intent = Intent(this, DisabledNotificationsOsActivity::class.java)
        startActivity(intent)
    }

    private fun isReadyForDisplayUpdatePersonalDataFullscreen(): Boolean {
        val operation = TemplatesUtil.getOperation(
            navigation = appModel.navigationTemplate,
            featureName = TemplatesUtil.MORE_KEY,
            optionName = TemplatesUtil.AML_FLOW_KEY,
        )
        val isVisible: Boolean = operation.isVisible
        return (appModel.profile.client?.isDataUpdateRequired ?: false) && isVisible
    }

    private fun launchUpdatePersonalDataWebView() {
        val editProfilePath: String = BuildConfig.UPDATE_PERSONAL_DATA.replace(
            UPDATE_PERSONAL_DATA_PARAM,
            DASHBOARD_SOURCE_VALUE,
        )
        val url: String = GatesUtil.getGateUrl(appModel, editProfilePath)
        val intent = Intent(this, NewWebViewActivity::class.java)
            .putExtra(WebViewConstant.URL_KEY, url)
            .putExtra(WebViewConstant.DISABLE_RETURN_KEY, Constant.NO_RETURN)
        startActivity(intent)
    }

    private fun handleResultFromActivateNotification(
        activityResult: ActivityResult,
        isFromActivate: Boolean,
    ) {
        val resultCode: Int = activityResult.resultCode
        val isOk: Boolean = Activity.RESULT_OK == resultCode
        val isCancel: Boolean = resultCode == Activity.RESULT_CANCELED
        if (isOk && this is NewDashboardActivity) {
            (this as? NewDashboardActivity)?.youActivatedNotificationsCorrectly(isFromActivate)
        } else if (isCancel && this is NewDashboardActivity) {
            (this as? NewDashboardActivity)?.activateNotificationsInPhoneConfigs(isFromActivate)
        }
        if (validatePokActivation()) {
            showPokActivation()
        }
    }

    private fun handleResultFromActivateTxBiometric(activityResult: ActivityResult) {
        val message: String = activityResult.data?.getStringExtra(MyAccountSecurityActivity.KEY_BIOMETRIC_CONFIG_MSG) ?: Constant.EMPTY_STRING
        val functionalType: String = activityResult.data?.getStringExtra(EnrollmentBiometricActivity.KEY_BIOMETRIC_FUNCTIONAL_TYPE) ?: Constant.EMPTY_STRING
        val analyticsMessage: String = activityResult.data?.getStringExtra(EnrollmentBiometricActivity.KEY_BIOMETRIC_ANALYTICS_MESSAGE) ?: Constant.EMPTY_STRING
        val resultCode: Int = activityResult.resultCode
        val isOk: Boolean = Activity.RESULT_OK == resultCode
        val previousSection = Constant.EMPTY_STRING
        if (isOk && this is NewDashboardActivity) {
            (this as NewDashboardActivity).youActivatedTxBiometricCorrectly(
                message,
                functionalType,
                analyticsMessage,
                previousSection
            )
        }
    }
}
