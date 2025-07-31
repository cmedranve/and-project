package pe.com.scotiabank.blpm.android.client.base.operation.completion

import androidx.core.util.Consumer
import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.analytics.factories.operation.token.validation.ValidationFactory
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.operation.confirmation.carrier.CarrierFromConfirmationConsumer
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FeasibleFrequentOperationModel
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentFactoryForPayment
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentFactoryForTransferOtherAccount
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentFactoryForTransferOtherBank
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentFactoryForTransferOwn
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentPaymentModel
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.NonFeasibleFrequentOperationModel
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.AnalyticModel
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapter
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForAppraisalPayment
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForPayment
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForRecharge
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForTransferOtherAccount
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForTransferOtherBank
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForTransferOwn
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.model.BaseSummaryModel
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardType
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.data.net.RestApiService
import pe.com.scotiabank.blpm.android.data.repository.FrequentOperationDataRepository
import java.lang.ref.WeakReference

class CompletionFlowCoordinatorFactory(
    private val hub: Hub,
    private val confirmRepository: ConfirmRepository,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    fun create(
        transactionId: String,
        isValidatedWithPush: Boolean,
        carrier: CarrierFromConfirmationConsumer,
        origin: ConfirmationOrigin,
        analyticData: OperationAnalyticData,
        analyticConsumerForPushKey: Consumer<AnalyticEventData<*>>,
    ): CompletionFlowCoordinator<out Any, out BaseSummaryModel> = CompletionFlowCoordinator(
        hub = hub,
        weakResources = hub.weakResources,
        weakAppContext = hub.weakAppContext,
        appModel = hub.appModel,
        transactionId = transactionId,
        isValidatedWithPush = isValidatedWithPush,
        carrierFromConfirmationConsumer = carrier,
        analyticData = analyticData,
        confirmationAnalyticConsumer = createAnalyticConsumer(analyticData),
        analyticConsumerForPushKey = analyticConsumerForPushKey,
        confirmModel = createModel(),
        summaryAdapter = createSummaryAdapter(origin),
        frequentOperationModel = createFrequentByOrigin(origin),
        weakParent = weakParent,
        scope = parentScope.newChildScope(),
        dispatcherProvider = hub.dispatcherProvider,
        mutableLiveHolder = hub.mutableLiveHolder,
        userInterface = hub.userInterface,
        uiStateHolder = DelegateUiStateHolder(),
    )

    private fun createSummaryAdapter(
        origin: ConfirmationOrigin
    ): SummaryAdapter<out Any, out BaseSummaryModel> = when (origin) {
        is OwnAccount -> SummaryAdapterForTransferOwn(
            weakResources = hub.weakResources,
            originProduct = origin.originAccount,
            destinationProduct = origin.destinyAccount
        )
        is OtherAccount -> SummaryAdapterForTransferOtherAccount(hub.weakResources, origin.originAccount)
        is TransferOtherBank -> SummaryAdapterForTransferOtherBank(hub.weakResources)
        is Payment -> SummaryAdapterForPayment(
            weakResources = hub.weakResources,
            institutionId = origin.institutionId,
            serviceCode = origin.serviceCode,
            zonalId = origin.zonalId
        )
        is Recharge -> SummaryAdapterForRecharge(hub.weakResources)
        is AppraisalPayment -> SummaryAdapterForAppraisalPayment(hub.weakResources)
    }

    private fun createFrequentByOrigin(
        origin: ConfirmationOrigin,
    ): FrequentOperationModel<out Any, out BaseSummaryModel> {
        if (DashboardType.BUSINESS == hub.appModel.dashboardType) return NonFeasibleFrequentOperationModel()
        return when(origin) {
            is OwnAccount -> FeasibleFrequentOperationModel(
                dispatcherProvider = hub.dispatcherProvider,
                frequentFactory = FrequentFactoryForTransferOwn(),
                repository = createFrequentRepository(),
            )
            is OtherAccount -> FeasibleFrequentOperationModel(
                dispatcherProvider = hub.dispatcherProvider,
                frequentFactory = FrequentFactoryForTransferOtherAccount(),
                repository = createFrequentRepository(),
            )
            is TransferOtherBank -> FeasibleFrequentOperationModel(
                dispatcherProvider = hub.dispatcherProvider,
                frequentFactory = FrequentFactoryForTransferOtherBank(),
                repository = createFrequentRepository(),
            )
            is Payment -> FrequentPaymentModel(FeasibleFrequentOperationModel(
                dispatcherProvider = hub.dispatcherProvider,
                frequentFactory = FrequentFactoryForPayment(),
                repository = createFrequentRepository(),
            ))
            is Recharge, is AppraisalPayment -> NonFeasibleFrequentOperationModel()
        }
    }

    private fun createFrequentRepository(): FrequentOperationDataRepository {
        val api: RestApiService = hub.appModel.sessionRetrofit.create(
            RestApiService::class.java,
        )
        return FrequentOperationDataRepository(api)
    }

    private fun <R: Any> createModel(): ConfirmModel<R> = ConfirmModel(
        dispatcherProvider = hub.dispatcherProvider,
        factoryOfReferenceRequest = FactoryOfReferenceRequest(),
        confirmRepository = confirmRepository,
    )

    private fun createAnalyticConsumer(data: OperationAnalyticData): AnalyticModel {
        val analyticFactory = createAnalyticFactory(data)
        return AnalyticModel(hub.analyticsDataGateway, analyticFactory)
    }

    private fun createAnalyticFactory(
        data: OperationAnalyticData,
    ): ValidationFactory = ValidationFactory(
        systemDataFactory = hub.systemDataFactory,
        information = data.information,
        processType = data.processType,
        company = data.company,
        typeOfDocument = data.typeOfDocument,
        typeOfOriginProduct = data.typeOfOriginProduct,
        originCurrency = data.originCurrency,
        typeOfDestinationProduct = data.typeOfDestinationProduct,
        destinationCurrency = data.destinationCurrency,
        amountText = data.amountText,
        movementType = data.movementType,
        status = data.status,
        paymentType = data.paymentType,
        questionFive = data.questionFive,
        numberOfElements = data.numberOfElements,
        installments = data.installments,
    )
}
