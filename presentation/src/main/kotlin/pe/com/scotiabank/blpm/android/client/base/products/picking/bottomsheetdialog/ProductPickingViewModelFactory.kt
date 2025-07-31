package pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.products.picking.FormatterOfProductName
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.radiobutton.CollectorOfProductRadioButton
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.radiobutton.CollectorOfInstallmentChipsComponent
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardType
import pe.com.scotiabank.blpm.android.data.repository.products.BusinessRepository
import pe.com.scotiabank.blpm.android.data.repository.products.PersonRepository
import pe.com.scotiabank.blpm.android.data.repository.products.ProductRepository
import java.lang.ref.WeakReference
import javax.inject.Inject

class ProductPickingViewModelFactory @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val appModel: AppModel,
    appContext: Context,
    private val personalBankingForProduct: PersonRepository,
    private val businessBankingForProduct: BusinessRepository,
) : ViewModelProvider.Factory {

    private val weakAppContext: WeakReference<Context?> = WeakReference(appContext)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductPickingViewModel::class.java)) {
            return createProductPickingViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    private fun createProductPickingViewModel(): ProductPickingViewModel {
        val model = ProductPickingModel(
            dispatcherProvider = dispatcherProvider,
            productRepository = pickProductRepository(appModel.dashboardType),
        )
        val dataFactory = createDataFactory()
        return ProductPickingViewModel(
            dispatcherProvider = dispatcherProvider,
            weakAppContext = weakAppContext,
            model = model,
            dataFactory = dataFactory,
            availabilityRegistry = createAvailabilityRegistry(),
        )
    }

    private fun pickProductRepository(dashboardType: DashboardType): ProductRepository {

        if (DashboardType.BUSINESS == dashboardType) return businessBankingForProduct

        return personalBankingForProduct
    }

    private fun createDataFactory() = FactoryOfProductSubmission(
        collectorOfProductRadioButton = createCollectorOfProductRadioButton(),
    )

    private fun createCollectorOfProductRadioButton() = CollectorOfProductRadioButton(
        formatterOfProductName = FormatterOfProductName(),
        installmentCollector = CollectorOfInstallmentChipsComponent(weakAppContext),
    )

    private fun createAvailabilityRegistry(): AvailabilityRegistry {
        val ids: Collection<Long> = listOf(
            ProductPickingViewModel.KEY_OF_CONTROLLER_OF_CANVAS_BUTTON,
        )
        return AvailabilityRegistry(ids)
    }
}
