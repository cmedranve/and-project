package pe.com.scotiabank.blpm.android.client.cards.walletnotinstalled

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.client.base.BaseBindingActivity
import pe.com.scotiabank.blpm.android.client.base.BindingInflaterOfActivity
import pe.com.scotiabank.blpm.android.client.base.carrier.feedFrom
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.databinding.ActivityPortableBinding
import pe.com.scotiabank.blpm.android.ui.list.ComposerOfAppBarAndMain
import pe.com.scotiabank.blpm.android.ui.list.SpaceMeter
import javax.inject.Inject

class GoogleWalletNotInstalledActivity: BaseBindingActivity<ActivityPortableBinding>() {

    private val creationExtras = MutableCreationExtras()
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: MainViewModel by viewModels(
        extrasProducer = ::creationExtras,
        factoryProducer = ::viewModelFactory,
    )

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            Intention::class,
            InstancePredicate(::filterDownloadGoogleWalletAction),
            InstanceHandler(::handleDownloadGoogleWalletAction)
        )
        .add(
            Intention::class,
            InstancePredicate(::filterCloseActivityAction),
            InstanceHandler(::handleCloseActivityAction)
        )
        .build()
    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val spaceMeter: SpaceMeter by lazy {
        SpaceMeter()
    }

    override fun getBindingInflater() = BindingInflaterOfActivity(ActivityPortableBinding::inflate)

    override fun getSettingToolbar(): Boolean = false

    override fun getToolbarTitle(): String = Constant.EMPTY_STRING

    override fun isOpenedSessionRequired(): Boolean = false

    override fun shouldExpireSession(): Boolean = false

    override fun additionalInitializer() {
        creationExtras.feedFrom(intent)
        spaceMeter.register(binding)
        setUpObservers()
        viewModel.setUpUi(selfReceiver)
    }

    private fun setUpObservers() {
        ComposerOfAppBarAndMain.compose(this, binding, viewModel)
    }

    private fun filterDownloadGoogleWalletAction(
        intention: Intention
    ): Boolean = Intention.DOWNLOAD_GOOGLE_WALLET == intention

    @Suppress("UNUSED_PARAMETER")
    private fun handleDownloadGoogleWalletAction(intention: Intention) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_WALLET_MARKET_URL))
        startActivity(intent)
        finish()
    }

    private fun filterCloseActivityAction(
        intention: Intention
    ): Boolean = Intention.CLOSE_ACTIVITY == intention

    @Suppress("UNUSED_PARAMETER")
    private fun handleCloseActivityAction(intention: Intention) {
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.recyclingState = binding.rvMainItems.layoutManager?.onSaveInstanceState()
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        spaceMeter.unregister()
        super.onDestroy()
    }

    companion object {

        private const val GOOGLE_WALLET_MARKET_URL = "https://play.google.com/store/apps/details?id=com.google.android.apps.walletnfcrel"
    }

}
