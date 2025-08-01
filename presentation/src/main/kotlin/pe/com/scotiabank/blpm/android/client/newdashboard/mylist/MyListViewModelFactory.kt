package pe.com.scotiabank.blpm.android.client.newdashboard.mylist

import android.content.Context
import android.content.res.Resources
import androidx.core.util.Function
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.ViewModelWithSheetDialog
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CurrencyFormatter
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.base.registry.VisitRegistry
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.disabled.MainTopCompositeForDisabled
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.disabled.MyDisabledListViewModel
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.ConfirmModel
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.DeleteOperationModel
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.FactoryOfRequestEntity
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.HelperForDeletionDialog
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.HelperForMaxSelectionDialog
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.IdRegistry
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.MainTopCompositeForEnabled
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.MyEnabledListViewModel
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.MyListModel
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.OperationMatcher
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.PaymentEnablingWithCreditCard
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.PaymentModel
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.SelectionHelper
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.SummaryHelper
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.TextBuilderForSnackBar
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.submenu.SubMenuComposite
import pe.com.scotiabank.blpm.android.client.templates.FeatureTemplate
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.util.FormatterUtil
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil
import pe.com.scotiabank.blpm.android.data.repository.FrequentOperationDataRepository
import pe.com.scotiabank.blpm.android.data.repository.NewFrequentOperationsDataRepository
import pe.com.scotiabank.blpm.android.data.repository.PaymentDataRepository
import java.lang.ref.WeakReference
import javax.inject.Inject

class MyListViewModelFactory @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val appModel: AppModel,
    appContext: Context,
    private val newFrequentOperationsRepository: NewFrequentOperationsDataRepository,
    private val frequentOperationRepository: FrequentOperationDataRepository,
    private val paymentRepository: PaymentDataRepository,
) : ViewModelProvider.Factory {

    private val weakResources: WeakReference<Resources?> = WeakReference(appContext.resources)
    private val formatter = CurrencyFormatter(
        formatting = Function(FormatterUtil::format),
    )
    private val matcher = OperationMatcher()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(MyListViewModel::class.java)) {
            return createViewModel(extras) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun createViewModel(extras: CreationExtras): MyListViewModel {

        val navigationFeature: FeatureTemplate = findNavigationFeatureTemplate(appModel.navigationTemplate)
        val myListOption: OptionTemplate = findMyListOptionTemplate(navigationFeature)

        val viewModelForEnabling: ViewModelWithSheetDialog = createViewModelFor(myListOption)

        return MyListViewModel(viewModelForEnabling = viewModelForEnabling)
    }

    private fun createViewModelFor(myListOption: OptionTemplate): ViewModelWithSheetDialog {
        val isDisabled: Boolean = TemplatesUtil.isDisabled(myListOption.type)
        return if (isDisabled) createViewModelForDisabled() else createViewModelForEnabled()
    }

    private fun createViewModelForDisabled() = MyDisabledListViewModel(
        factoryOfToolbarComposite = createFactoryOfAppBarComposite(),
        factoryOfMainTopComposite = createFactoryOfMainTopCompositeForDisabled(),
    )

    private fun createViewModelForEnabled(): MyEnabledListViewModel {
        val myListTemplate: FeatureTemplate = findMyListFeatureTemplate(appModel.navigationTemplate)
        val templateForAddingRecentPayments: OptionTemplate = findTemplateForAddingRecentPayments(myListTemplate)
        val types: Collection<FrequentOperationType> = findFrequentOperationTypes(myListTemplate)
        val idRegistry = IdRegistry()
        val factoryOfMainTopComposite = createFactoryOfMainTopCompositeForEnabled(
            templateForAddingRecentPayments = templateForAddingRecentPayments,
            frequentOperationTypes = types,
            idRegistry = idRegistry,
        )

        return MyEnabledListViewModel(
            dispatcherProvider = dispatcherProvider,
            factoryOfToolbarComposite = createFactoryOfAppBarComposite(),
            factoryOfMainTopComposite = factoryOfMainTopComposite,
            factoryOfMainBottomComposite = BottomComposite.Factory(dispatcherProvider),
            factoryOfAnchoredBottomComposite = BottomComposite.Factory(dispatcherProvider),
            factoryOfSubMenuComposite = SubMenuComposite.Factory(dispatcherProvider, weakResources),
            appModel = appModel,
            weakResources = weakResources,
            myListTemplate = myListTemplate,
            templateForAddingRecentPayments = templateForAddingRecentPayments,
            model = createMyListModel(types),
            deleteModel = createDeleteOperationModel(),
            paymentModel = PaymentModel(dispatcherProvider, paymentRepository),
            confirmModel = createConfirmModel(),
            idRegistry = idRegistry,
            visitRegistry = createVisitRegistry(idRegistry),
            availabilityRegistry = createAvailabilityRegistry(idRegistry),
            selectionHelper = SelectionHelper(),
            summaryHelper = SummaryHelper(),
            helperForMaxSelectionDialog = HelperForMaxSelectionDialog(weakResources),
            paymentEnablingWithCreditCard = PaymentEnablingWithCreditCard(),
            helperForDeletionDialog = HelperForDeletionDialog(weakResources),
            textBuilderForSnackBar = TextBuilderForSnackBar(appModel, weakResources),
        )
    }

    private fun findFrequentOperationTypes(
        myListTemplate: FeatureTemplate,
    ): Collection<FrequentOperationType> {

        val types: MutableCollection<FrequentOperationType> = mutableListOf()

        val templateForTransfers: OptionTemplate = findTemplateForTransfers(myListTemplate)
        addItemIf(templateForTransfers::isVisible, FrequentOperationType.TRANSFER, types)

        val templateForPayments: OptionTemplate = findTemplateForPayments(myListTemplate)
        addItemIf(templateForPayments::isVisible, FrequentOperationType.PAYMENT, types)

        return types
    }

    private inline fun <T: Any> addItemIf(
        predicate: () -> Boolean,
        item: T,
        items: MutableCollection<T>,
    ) {
        if (predicate.invoke()) {
            items.add(item)
        }
    }

    private fun createFactoryOfAppBarComposite() = AppBarComposite.Factory(
        dispatcherProvider = dispatcherProvider,
    )

    private fun createFactoryOfMainTopCompositeForDisabled() = MainTopCompositeForDisabled.Factory(
        dispatcherProvider = dispatcherProvider,
        weakResources = weakResources,
    )

    private fun createFactoryOfMainTopCompositeForEnabled(
        templateForAddingRecentPayments: OptionTemplate,
        frequentOperationTypes: Collection<FrequentOperationType>,
        idRegistry: IdRegistry,
    ) = MainTopCompositeForEnabled.Factory(
        dispatcherProvider = dispatcherProvider,
        appModel = appModel,
        weakResources = weakResources,
        templateForAddingRecentPayments = templateForAddingRecentPayments,
        frequentOperationTypes = frequentOperationTypes,
        idRegistry = idRegistry,
        formatter = formatter,
        matcher = matcher,
    )

    private fun createMyListModel(types: Collection<FrequentOperationType>) = MyListModel(
        dispatcherProvider = dispatcherProvider,
        weakResources = weakResources,
        newFrequentOperationsDataRepository = newFrequentOperationsRepository,
        frequentOperationTypes = types,
    )

    private fun createDeleteOperationModel() = DeleteOperationModel(
        dispatcherProvider = dispatcherProvider,
        repository = frequentOperationRepository,
    )

    private fun createConfirmModel() = ConfirmModel(
        dispatcherProvider = dispatcherProvider,
        weakResources = weakResources,
        factory = FactoryOfRequestEntity(),
        repository = paymentRepository,
    )

    private fun createVisitRegistry(idRegistry: IdRegistry): VisitRegistry {
        val maxNumberAllowedById: Map<Long, Int> = mapOf(
            idRegistry.tabIdOfSummary to 1,
            idRegistry.buttonIdOfSummaryRetry to 3,
            FrequentOperationType.TRANSFER.id to 1,
            FrequentOperationType.TRANSFER.retryId to 3,
            FrequentOperationType.PAYMENT.id to 1,
            FrequentOperationType.PAYMENT.retryId to 3,
        )
        return VisitRegistry(maxNumberAllowedById)
    }

    private fun createAvailabilityRegistry(idRegistry: IdRegistry): AvailabilityRegistry {
        val ids: Collection<Long> = listOf(
            idRegistry.buttonIdOfSummaryRetry,
            FrequentOperationType.TRANSFER.retryId,
            FrequentOperationType.PAYMENT.retryId,
        )
        return AvailabilityRegistry(ids)
    }
}