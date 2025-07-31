package pe.com.scotiabank.blpm.android.client.base

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.util.*
import pe.com.scotiabank.blpm.android.client.util.accesibility.AccessibilityUtil
import pe.com.scotiabank.blpm.android.client.util.analytics.MenuAnalytics
import pe.com.scotiabank.blpm.android.client.util.analytics.TrackDataEntity

abstract class BaseBusinessBindingActivity<B : ViewBinding> : BaseActivity() {

    protected lateinit var binding: B

    var toolbar: Toolbar? = null
    private var _toolbarTitleView: TextView? = null
    private var isBlackArrow = true
    private var _settingToolbar = false
    private lateinit var trackDataEntity: TrackDataEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getBindingInflater().inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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
    }

    protected abstract fun getBindingInflater(): BindingInflaterOfActivity<B>

    /**
     * This function returns a boolean that controls the toolbar setup
     *
     * @return boolean that can be override
     */
    open fun getSettingToolbar(): Boolean = true

    protected fun setupToolbar() {
        toolbar?.setBackgroundColor(ContextCompat.getColor(this, com.scotiabank.canvascore.R.color.canvascore_brand_white))
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, com.scotiabank.canvascore.R.color.canvascore_brand_black))
        _toolbarTitleView?.setTextColor(ContextCompat.getColor(this, com.scotiabank.canvascore.R.color.canvascore_brand_black))
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = Constant.EMPTY_STRING
        }
        getToolbarTitle()?.let(this::setupToolbarTitle)
    }

    private fun setupToolbarTitle(toolbarTitle: String) {
        _toolbarTitleView?.text = if (intent.hasExtra(Constant.TOOLBAR_TITLE)) {
            intent.getStringExtra(Constant.TOOLBAR_TITLE)
        } else {
            toolbarTitle
        }
        AccessibilityUtil.setHeading(_toolbarTitleView)
    }

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

    protected abstract fun additionalInitializer()


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

}
