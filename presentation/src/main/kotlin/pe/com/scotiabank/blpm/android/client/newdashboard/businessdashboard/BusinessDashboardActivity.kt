package pe.com.scotiabank.blpm.android.client.newdashboard.businessdashboard

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.Menu
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.analytics.factories.newdashboard.NewDashboardFactory
import pe.com.scotiabank.blpm.android.analytics.firebase.FirebaseAnalyticsDataGateway
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.applySecureSurfaceOnWindow
import pe.com.scotiabank.blpm.android.client.app.clearWindowFromSecureSurface
import pe.com.scotiabank.blpm.android.client.base.BaseBusinessBindingActivity
import pe.com.scotiabank.blpm.android.client.base.BindingInflaterOfActivity
import pe.com.scotiabank.blpm.android.client.base.launcheditor.EnablerOfScreenshotControl
import pe.com.scotiabank.blpm.android.client.base.session.FilterOfSessionEvent
import pe.com.scotiabank.blpm.android.client.base.session.SessionEvent
import pe.com.scotiabank.blpm.android.client.databinding.ActivityNewDashboard2Binding
import pe.com.scotiabank.blpm.android.client.newdashboard.Handler
import pe.com.scotiabank.blpm.android.client.newdashboard.NewDashboardPagerAdapter
import pe.com.scotiabank.blpm.android.client.products.dashboard.HomeFragment
import pe.com.scotiabank.blpm.android.client.profilesettings.myaccount.IntentionAccount
import pe.com.scotiabank.blpm.android.client.profilesettings.myaccount.IntentionAnalyticEvent
import pe.com.scotiabank.blpm.android.client.profilesettings.myaccount.MyAccountFragment
import pe.com.scotiabank.blpm.android.client.profilesettings.myaccount.MyAccountViewModel
import pe.com.scotiabank.blpm.android.client.profilesettings.myaccount.MyAccountViewModelFactory
import pe.com.scotiabank.blpm.android.client.scotiapay.ScotiaPayFragment
import pe.com.scotiabank.blpm.android.client.scotiapay.ScotiaPayViewModel
import pe.com.scotiabank.blpm.android.client.scotiapay.ScotiaPayViewModelFactory
import pe.com.scotiabank.blpm.android.client.scotiapay.shared.findTemplateForContactPay
import pe.com.scotiabank.blpm.android.client.tasknav.clearThenNavigateToHost
import pe.com.scotiabank.blpm.android.client.templates.FeatureTemplate
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.DateUtil
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil
import pe.com.scotiabank.blpm.android.client.util.lifecycle.LifecycleUtil
import javax.inject.Inject

class BusinessDashboardActivity : BaseBusinessBindingActivity<ActivityNewDashboard2Binding>(),
    Handler.HandlerListener {

    companion object {
        @JvmStatic
        fun getCallingIntent(context: Context?) =
            Intent(context, BusinessDashboardActivity::class.java)
    }

    private val fragmentsDashboard = mutableListOf<Fragment>()
    private var homeFragment: HomeFragment? = null
    private var myAccountFragment: MyAccountFragment? = null
    private var scotiaPayFragment: ScotiaPayFragment? = null

    @Inject
    lateinit var myAccountViewModelFactory: MyAccountViewModelFactory

    @Inject
    lateinit var myScotiaPayViewModelFactory: ScotiaPayViewModelFactory

    private fun getMyAccountViewModel(): MyAccountViewModel {
        return ViewModelProvider(this, myAccountViewModelFactory)[MyAccountViewModel::class.java]
    }

    private fun getMyContactPaymentViewModel(): ScotiaPayViewModel {
        return ViewModelProvider(this, myScotiaPayViewModelFactory)[ScotiaPayViewModel::class.java]
    }

    @Inject
    lateinit var businessDashboardViewModelFactory: BusinessDashboardViewModelFactory

    @Inject
    lateinit var dashboardFactory: NewDashboardFactory

    private val viewModel: BusinessDashboardViewModel by lazy {
        ViewModelProvider(
            owner = this,
            factory = businessDashboardViewModelFactory,
        )[BusinessDashboardViewModel::class.java]
    }

    private val analyticsDataGateway by lazy {
        FirebaseAnalyticsDataGateway(FirebaseAnalytics.getInstance(this), this)
    }

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            SessionEvent::class,
            InstancePredicate(FilterOfSessionEvent::filterInLoggedOut),
            InstanceHandler(::handleSessionLoggedOut)
        )
        .build()
    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    override fun getBindingInflater() =
        BindingInflaterOfActivity(ActivityNewDashboard2Binding::inflate)

    override fun getSettingToolbar() = false

    override fun getToolbarTitle() = Constant.EMPTY_STRING

    override fun additionalInitializer() {
        binding.cloCenter.isVisible = true
        DateUtil.setServerDate(appModel.serverDate)
        setupTemplates()
        setUpViewModel()
        setupDashboardPagerAdapter()
        setupHandler()
        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = handleBackEvent()
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
        binding.cloCenter.isVisible = false
    }

    override fun onNavigationItemSelected(idLabel: Int) {
        val menuName: String = pickNameForMenu(idLabel)

        if (idLabel == R.id.navigation_profile) {
            getMyAccountViewModel().receiveEvent(IntentionAccount.NOTIFY_CLICK_ON_MY_ACCOUNT_TAB)
        }

        if (idLabel == R.id.navigation_contact_pay) {
            getMyContactPaymentViewModel().receiveEvent(IntentionAccount.NOTIFY_CLICK_ON_MY_PAYMENTS_TAB)
        }
        sendMenuAnalytics(menuName)
    }

    private fun pickNameForMenu(idLabel: Int): String = when (idLabel) {
        R.id.navigation_home -> NewDashboardFactory.START_MENU
        R.id.navigation_profile -> NewDashboardFactory.MY_ACCOUNT_MENU
        else -> Constant.HYPHEN_STRING
    }

    private fun setupTemplates() {
        val featureTemplate = TemplatesUtil.getFeature(appModel.navigationTemplate, TemplatesUtil.NAVIGATION_KEY)
        addHomeFragmentByTemplate(featureTemplate)
        addContactPayFragment()
        addMenuFragmentByTemplate(featureTemplate)
    }

    private fun addMenuFragmentByTemplate(featureTemplate: FeatureTemplate) {
        if (TemplatesUtil.getOperation(featureTemplate, TemplatesUtil.MORE_KEY).isVisible) {
            myAccountFragment = MyAccountFragment()
            myAccountFragment?.let(fragmentsDashboard::add)
            binding.bnDashboard.menu
                .add(Menu.NONE, R.id.navigation_profile, Menu.NONE, R.string.tab_my_account)
                .setIcon(R.drawable.ic_tab_my_account)
        }
    }

    private fun addContactPayFragment() {
        val optionTemplate: OptionTemplate = findTemplateForContactPay(appModel.navigationTemplate)
        val isContactPayVisible: Boolean = optionTemplate.isVisible
        if (isContactPayVisible.not()) return

        scotiaPayFragment = ScotiaPayFragment()
        scotiaPayFragment?.let(fragmentsDashboard::add)
        binding.bnDashboard.menu
            .add(Menu.NONE, R.id.navigation_contact_pay, Menu.NONE, R.string.tab_contact_pay)
            .setIcon(R.drawable.ic_tab_contact_pay)
    }

    private fun addHomeFragmentByTemplate(featureTemplate: FeatureTemplate) {
        if (TemplatesUtil.getOperation(featureTemplate, TemplatesUtil.HOME_KEY).isVisible) {
            homeFragment = HomeFragment.newInstance(false)
            homeFragment?.let(fragmentsDashboard::add)
            binding.bnDashboard.menu
                .add(Menu.NONE, R.id.navigation_home, Menu.NONE, R.string.tab_home)
                .setIcon(R.drawable.ic_tab_home)
        }
    }

    private fun setupDashboardPagerAdapter() {
        val newDashboardPagerAdapter = NewDashboardPagerAdapter(
            supportFragmentManager,
            fragmentsDashboard
        )
        binding.vpDashboard.run {
            adapter = newDashboardPagerAdapter
            offscreenPageLimit = fragmentsDashboard.size
            setPagingEnabled(false)
        }
    }

    private fun setupHandler() {
        val handler = Handler(binding.vpDashboard, fragmentsDashboard, this)
        binding.bnDashboard.setOnItemSelectedListener(handler::onNavigationClick)
    }

    private fun sendMenuAnalytics(name: String) {
        analyticsDataGateway.setCurrentScreen(NewDashboardFactory.SCREEN_NAME)
        dashboardFactory.createBottomMenuEvent(name).let(analyticsDataGateway::sendEventV2)
    }

    private fun setUpViewModel() {
        setupObservers()
        viewModel.setUp(selfReceiver)
    }

    private fun setupObservers() {
        viewModel.getLoadingV2().observe(this, Observer(::observeShowHideLoading))
        viewModel.getErrorMessageV2().observe(this, Observer(::observeErrorMessage))
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleSessionLoggedOut(event: SessionEvent) {
        LifecycleUtil.runOnResume(this, Runnable(::navigateToHost))
    }

    private fun navigateToHost() {
        clearThenNavigateToHost(applicationContext)
    }

    private fun handleBackEvent() {
        showLogoutMessage()
    }

    fun showLogoutMessage() {
        AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
            .setTitle(R.string.action_log_out)
            .setCancelable(false)
            .setMessage(R.string.logout_message)
            .setPositiveButton(getString(R.string.yes)) { _: DialogInterface?, _: Int ->
                appModel.receiveEvent(IntentionAnalyticEvent.POP_UP_YES)
                viewModel.logout()
            }
            .setNegativeButton(getString(R.string.no)) { _: DialogInterface?, _: Int ->
                appModel.receiveEvent(IntentionAnalyticEvent.POP_UP_NOT)
            }
            .create().show()
    }

    override fun onResume() {
        super.onResume()
        applyOrClearSecureSurface()
    }

    private fun applyOrClearSecureSurface() {
        val isEnabler = appModel.environmentHolder is EnablerOfScreenshotControl
        if (!isEnabler) return
        val enabler = appModel.environmentHolder as EnablerOfScreenshotControl
        if (enabler.isScreenshotEnabled) {
            window.clearWindowFromSecureSurface()
            return
        }
        window.applySecureSurfaceOnWindow()
    }
}