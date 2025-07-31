package pe.com.scotiabank.blpm.android.client.cards.walletnotinstalled

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.factories.SystemDataFactory
import pe.com.scotiabank.blpm.android.analytics.factories.cards.walletnotinstalled.WalletNotInstalledFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.carrier.HolderOfStringCreation
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.cards.walletnotinstalled.analytics.AnalyticModel
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Named

class ViewModelFactory @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    appContext: Context,
    private val analyticsDataGateway: AnalyticsDataGateway,
    @Named("systemDataFactorySession") private val systemDataFactory: SystemDataFactory,
) : ViewModelProvider.Factory {

    private val weakResources: WeakReference<Resources?> = WeakReference(appContext.resources)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return createViewModel(extras) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    private fun createViewModel(extras: CreationExtras): MainViewModel {

        val holder = HolderOfStringCreation(extras)
        val idRegistry = IdRegistry()

        return MainViewModel(
            factoryOfToolbarComposite = createFactoryOfAppBarComposite(),
            factoryOfMainTopComposite = createFactoryOfMainTopComposite(),
            factoryOfMainBottomComposite = BottomComposite.Factory(dispatcherProvider),
            weakResources = weakResources,
            idRegistry = idRegistry,
            analyticModel = createAnalyticModel(holder),
        )
    }

    private fun createFactoryOfAppBarComposite() = AppBarComposite.Factory(
        dispatcherProvider = dispatcherProvider,
    )

    private fun createFactoryOfMainTopComposite() = MainTopComposite.Factory(
        dispatcherProvider = dispatcherProvider,
        weakResources = weakResources
    )

    private fun createAnalyticModel(holder: HolderOfStringCreation): AnalyticModel {

        val previousSection: String = holder.findBy(AnalyticsConstant.PREVIOUS_SECTION)

        return AnalyticModel(
            analyticsDataGateway = analyticsDataGateway,
            analyticFactory = WalletNotInstalledFactory(systemDataFactory, previousSection)
        )
    }
}
