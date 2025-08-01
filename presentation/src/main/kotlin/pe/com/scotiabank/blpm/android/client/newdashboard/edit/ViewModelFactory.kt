package pe.com.scotiabank.blpm.android.client.newdashboard.edit

import android.content.Context
import android.content.res.Resources
import android.icu.text.NumberFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import pe.com.scotiabank.blpm.android.client.base.LegacyViewModel
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.carrier.HolderOfParcelableCreation
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.number.DoubleParser
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CollectorOfEditableAmount
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CollectorOfHorizontalCurrencyAmount
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CollectorOfSelectableCurrency
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.payment.EditPaymentModel
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.payment.EditPaymentViewModel
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.payment.FactoryOfPaymentRequestEntity
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.payment.MainTopCompositeForPayment
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.transfer.AmountHelper
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.transfer.EditTransferModel
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.transfer.EditTransferViewModel
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.transfer.FactoryOfAttributeRequest
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.transfer.FactoryOfTransferRequestEntity
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.transfer.MainTopCompositeForTransfer
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.FREQUENT_OPERATION
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.data.repository.FrequentOperationDataRepository
import java.lang.ref.WeakReference
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

class ViewModelFactory @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val defaultLocale: Locale,
    @Named("generalNumberFormat") private val numberFormat: NumberFormat,
    appContext: Context,
    private val frequentOperationsDataRepository: FrequentOperationDataRepository,
) : ViewModelProvider.Factory {

    private val weakResources: WeakReference<Resources?> = WeakReference(appContext.resources)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(EditOperationViewModel::class.java)) {
            return createViewModel(extras) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    private fun createViewModel(extras: CreationExtras): EditOperationViewModel {

        val holderOfParcelableCreation = HolderOfParcelableCreation(extras)
        val frequentOperation: FrequentOperationModel = holderOfParcelableCreation.findBy(
            idName = FREQUENT_OPERATION,
        )

        val viewModelForType: LegacyViewModel = createViewModelFor(frequentOperation)
        return EditOperationViewModel(viewModelForType = viewModelForType)
    }

    private fun createViewModelFor(frequentOperation: FrequentOperationModel): LegacyViewModel {

        val type: String = frequentOperation.type.uppercase()

        if (FrequentOperationType.TRANSFER.typeFromNetworkCall == type) {
            return createEditTransferViewModel(frequentOperation)
        }

        return createEditPaymentViewModel(frequentOperation)
    }

    private fun createEditTransferViewModel(
        frequentOperation: FrequentOperationModel,
    ): EditTransferViewModel {

        val doubleParser = DoubleParser(numberFormat = numberFormat)

        return EditTransferViewModel(
            factoryOfToolbarComposite = createFactoryOfAppBarComposite(),
            factoryOfMainTopComposite = createFactoryOfMainTopCompositeForTransfer(frequentOperation),
            factoryOfMainBottomComposite = BottomComposite.Factory(dispatcherProvider),
            weakResources = weakResources,
            amountHelper = AmountHelper(weakResources, doubleParser),
            frequentOperation = frequentOperation,
            model = createEditTransferModel(),
        )
    }

    private fun createFactoryOfAppBarComposite() = AppBarComposite.Factory(
        dispatcherProvider = dispatcherProvider,
    )

    private fun createFactoryOfMainTopCompositeForTransfer(
        frequentOperation: FrequentOperationModel,
    ) = MainTopCompositeForTransfer.Factory(
        dispatcherProvider = dispatcherProvider,
        collectorOfEditableName = createCollectorOfEditableName(),
        collectorOfCurrencyAmount = createCollectorOfHorizontalCurrencyAmount(frequentOperation),
    )

    private fun createCollectorOfHorizontalCurrencyAmount(
        frequentOperation: FrequentOperationModel,
    ) = CollectorOfHorizontalCurrencyAmount(
        collectorOfSelectableCurrency = createCollectorOfSelectableCurrency(frequentOperation),
        collectorOfEditableAmount = CollectorOfEditableAmount(defaultLocale, weakResources),
    )

    private fun createCollectorOfSelectableCurrency(
        frequentOperation: FrequentOperationModel,
    ): CollectorOfSelectableCurrency {

        val defaultCurrency: Currency = Currency.identifyBy(frequentOperation.currency)
        val currencies: Collection<Currency> = listOf(Currency.PEN, Currency.USD)

        return CollectorOfSelectableCurrency(currencies, defaultCurrency, weakResources)
    }

    private fun createCollectorOfEditableName(): CollectorOfEditableName {
        val helper: HelperForOperation = HelperForCustomerOperation(weakResources)
        return CollectorOfEditableName(helper)
    }

    private fun createEditTransferModel() = EditTransferModel(
        dispatcherProvider = dispatcherProvider,
        factoryOfRequestEntity = createFactoryOfTransferRequestEntity(),
        repository = frequentOperationsDataRepository,
    )

    private fun createFactoryOfTransferRequestEntity() = FactoryOfTransferRequestEntity(
        factoryOfAttributeRequest = FactoryOfAttributeRequest(),
    )

    private fun createEditPaymentViewModel(
        frequentOperation: FrequentOperationModel,
    ) = EditPaymentViewModel(
        factoryOfToolbarComposite = createFactoryOfAppBarComposite(),
        factoryOfMainTopComposite = createFactoryOfMainTopCompositeForPayment(),
        factoryOfMainBottomComposite = BottomComposite.Factory(dispatcherProvider),
        weakResources = weakResources,
        frequentOperation = frequentOperation,
        model = createEditPaymentModel(),
    )

    private fun createFactoryOfMainTopCompositeForPayment() = MainTopCompositeForPayment.Factory(
        dispatcherProvider = dispatcherProvider,
        collectorOfEditableName = createCollectorOfEditableName(),
    )

    private fun createEditPaymentModel() = EditPaymentModel(
        dispatcherProvider = dispatcherProvider,
        factoryOfRequestEntity = FactoryOfPaymentRequestEntity(),
        repository = frequentOperationsDataRepository,
    )
}