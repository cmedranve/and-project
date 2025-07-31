package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.non

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.scotiabank.canvaspe.confirmation.entity.CanvasConfirmationEntity
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.factories.AnalyticsBaseConstant
import pe.com.scotiabank.blpm.android.analytics.factories.SystemDataFactory
import pe.com.scotiabank.blpm.android.analytics.factories.operation.token.validation.ValidationFactory
import pe.com.scotiabank.blpm.android.analytics.factories.operation.token.validation.ValidationInformation
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.carrier.HolderOfIntCreation
import pe.com.scotiabank.blpm.android.client.base.carrier.HolderOfParcelableCreation
import pe.com.scotiabank.blpm.android.client.base.carrier.HolderOfStringCreation
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.operation.MoneyOperationType
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.*
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.*
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapter
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForAppraisalPayment
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForPayment
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForRecharge
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForTransferOtherAccount
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForTransferOtherBank
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForTransferOwn
import pe.com.scotiabank.blpm.android.client.base.receipt.ReceiptActivity
import pe.com.scotiabank.blpm.android.client.model.BaseSummaryModel
import pe.com.scotiabank.blpm.android.client.model.PaymentSummaryModel
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.model.TransferSummaryModel
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardType
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.analytics.RechargeAnalytics
import pe.com.scotiabank.blpm.android.data.entity.ConfirmationEntity
import pe.com.scotiabank.blpm.android.data.entity.PaymentConfirmationEntity
import pe.com.scotiabank.blpm.android.data.entity.RechargeConfirmationEntity
import pe.com.scotiabank.blpm.android.data.repository.*
import pe.com.scotiabank.blpm.android.data.repository.payment.appraisal.AppraisalRepository
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Named

class ViewModelFactory @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val appModel: AppModel,
    appContext: Context,
    private val personalBankingForTransfer: TransferDataRepository,
    private val businessBankingForTransfer: TransferBusinessDataRepository,
    private val personalBankingForPayment: PaymentDataRepository,
    private val businessBankingForPayment: PaymentBusinessDataRepository,
    private val appraisalRepository: AppraisalRepository,
    private val frequentRepository: FrequentOperationDataRepository,
    private val analyticsDataGateway: AnalyticsDataGateway,
    @Named("systemDataFactorySession") private val systemDataFactory: SystemDataFactory,
) : ViewModelProvider.Factory {

    private val weakResources: WeakReference<Resources?> = WeakReference(appContext.resources)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ValidationViewModel::class.java)) {
            return createViewModel(extras) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    private fun createViewModel(extras: CreationExtras): ValidationViewModel<out Any, out BaseSummaryModel> {
        val holderOfStringCreation = HolderOfStringCreation(extras)
        val holderOfIntCreation = HolderOfIntCreation(extras)
        val holderOfParcelableCreation = HolderOfParcelableCreation(extras)

        val nameOfOperationType: String = holderOfStringCreation
            .findBy(Constant.MONEY_OPERATION_TYPE)
            .uppercase()
        val operationType: MoneyOperationType = MoneyOperationType.valueOf(nameOfOperationType)

        return pickViewModel(operationType, holderOfStringCreation, holderOfIntCreation, holderOfParcelableCreation)
    }

    private fun pickViewModel(
        operationType: MoneyOperationType,
        holderOfStringCreation: HolderOfStringCreation,
        holderOfIntCreation: HolderOfIntCreation,
        holderOfParcelableCreation: HolderOfParcelableCreation,
    ): ValidationViewModel<out Any, out BaseSummaryModel> = when (operationType) {

        MoneyOperationType.TRANSFER_OWN -> createTransferOwn(holderOfStringCreation, holderOfIntCreation, holderOfParcelableCreation)
        MoneyOperationType.TRANSFER_OTHER_ACCOUNT -> createTransferOtherAccount(holderOfStringCreation, holderOfIntCreation, holderOfParcelableCreation)
        MoneyOperationType.TRANSFER_OTHER_BANK -> createTransferOtherBank(holderOfStringCreation, holderOfIntCreation, holderOfParcelableCreation)
        MoneyOperationType.PAYMENT -> createPayment(holderOfStringCreation, holderOfIntCreation, holderOfParcelableCreation)
        MoneyOperationType.RECHARGE -> createRecharge(holderOfStringCreation, holderOfIntCreation, holderOfParcelableCreation)
        MoneyOperationType.APPRAISAL_PAYMENT -> createAppraisalPayment(holderOfStringCreation, holderOfIntCreation, holderOfParcelableCreation)
        else -> throw IllegalArgumentException("Unknown Money Operation Type Class: " + operationType.name)
    }

    private fun createTransferOwn(
        holderOfStringCreation: HolderOfStringCreation,
        holderOfIntCreation: HolderOfIntCreation,
        holderOfParcelableCreation: HolderOfParcelableCreation,
    ): ValidationViewModel<out Any, out BaseSummaryModel> {

        val originProduct: ProductModel = holderOfParcelableCreation.findBy(Constant.ORIGIN_ENTITY)
        val destinationProduct: ProductModel = holderOfParcelableCreation.findBy(Constant.DESTINATION_ENTITY)
        val canvasEntity: CanvasConfirmationEntity = holderOfParcelableCreation.findBy(Constant.CANVAS_CONFIRMATION_ENTITY)

        val transferRepository: TransferRepository = pickTransferRepository()
        val confirmModel: ConfirmModel<ConfirmationEntity> = ConfirmModel(
            dispatcherProvider = dispatcherProvider,
            factoryOfReferenceRequest = FactoryOfReferenceRequest(),
            confirmRepository = ConfirmRepository(transferRepository::confirmTransfer),
        )

        val summaryAdapter: SummaryAdapter<ConfirmationEntity, TransferSummaryModel> = SummaryAdapterForTransferOwn(
            weakResources = weakResources,
            originProduct = originProduct,
            destinationProduct = destinationProduct,
        )

        return ValidationViewModel(
            appModel = appModel,
            weakResources = weakResources,
            dataOnBackDialog = createDataOnBackDialog(holderOfStringCreation),
            confirmModel = confirmModel,
            canvasEntity = canvasEntity,
            summaryAdapter = summaryAdapter,
            frequentOperationModel = pickFrequentOperationModelForTransferOwn(appModel.dashboardType),
            holderOfStringCreation = holderOfStringCreation,
            holderOfIntCreation = holderOfIntCreation,
            analyticModel = createAnalyticModel(holderOfStringCreation, holderOfIntCreation),
        )
    }

    private fun pickTransferRepository(): TransferRepository {

        if (DashboardType.BUSINESS == appModel.dashboardType) return businessBankingForTransfer

        return personalBankingForTransfer
    }

    private fun createDataOnBackDialog(
        holder: HolderOfStringCreation,
    ) = DataOnBackDialog(
        title = holder.findBy(DataOnBackDialog.TITLE_KEY),
        message = holder.findBy(DataOnBackDialog.MESSAGE_KEY),
    )

    private fun pickFrequentOperationModelForTransferOwn(
        dashboardType: DashboardType,
    ): FrequentOperationModel<ConfirmationEntity, TransferSummaryModel> {

        if (DashboardType.BUSINESS == dashboardType) {
            return NonFeasibleFrequentOperationModel()
        }

        return FeasibleFrequentOperationModel(
            dispatcherProvider = dispatcherProvider,
            frequentFactory = FrequentFactoryForTransferOwn(),
            repository = frequentRepository,
        )
    }

    private fun createAnalyticModel(holderOfStringCreation: HolderOfStringCreation, holderOfIntCreation: HolderOfIntCreation): AnalyticModel {
        val analyticFactory = createAnalyticFactory(holderOfStringCreation, holderOfIntCreation)
        return AnalyticModel(analyticsDataGateway, analyticFactory)
    }

    private fun createAnalyticFactory(holderOfStringCreation: HolderOfStringCreation, holderOfIntCreation: HolderOfIntCreation): ValidationFactory {

        val operationName: String = holderOfStringCreation.findBy(ValidationInformation.OPERATION_NAME)
        val information: ValidationInformation = ValidationInformation.valueOf(operationName)

        val numberOfInstallments: Int = holderOfIntCreation.findBy(ReceiptActivity.PARAM_INSTALLMENTS_NUMBER)

        return ValidationFactory(
            systemDataFactory = systemDataFactory,
            information = information,
            processType = holderOfStringCreation.findBy(AnalyticsBaseConstant.TYPE_PROCESS),
            company = holderOfStringCreation.findBy(AnalyticsConstant.COMPANY),
            typeOfDocument = holderOfStringCreation.findBy(RechargeAnalytics.TYPE_VOUCHER_RECHARGES),
            typeOfOriginProduct = holderOfStringCreation.findBy(AnalyticsBaseConstant.TYPE_OF_ORIGIN_ACCOUNT),
            originCurrency = holderOfStringCreation.findBy(AnalyticsBaseConstant.ORIGIN_CURRENCY),
            typeOfDestinationProduct = holderOfStringCreation.findBy(AnalyticsBaseConstant.TYPE_OF_DESTINATION_ACCOUNT),
            destinationCurrency = holderOfStringCreation.findBy(AnalyticsBaseConstant.DESTINATION_CURRENCY),
            amountText = holderOfStringCreation.findBy(AnalyticsBaseConstant.AMOUNT),
            movementType = holderOfStringCreation.findBy(AnalyticsBaseConstant.TYPE_OF_MOVEMENT),
            status = holderOfStringCreation.findBy(AnalyticsConstant.STATUS),
            paymentType = holderOfStringCreation.findBy(AnalyticsConstant.TYPE_PAY),
            questionFive = holderOfStringCreation.findBy(AnalyticsConstant.QUESTION_FIVE),
            numberOfElements = holderOfIntCreation.findBy(AnalyticsConstant.ELEMENTS_NUMBER),
            installments = if (numberOfInstallments == 0) AnalyticsConstant.NOT_INTALLMENTS else numberOfInstallments.toString(),
        )
    }

    private fun createTransferOtherAccount(
        holderOfStringCreation: HolderOfStringCreation,
        holderOfIntCreation: HolderOfIntCreation,
        holderOfParcelableCreation: HolderOfParcelableCreation,
    ): ValidationViewModel<out Any, out BaseSummaryModel> {

        val originProduct: ProductModel = holderOfParcelableCreation.findBy(Constant.ORIGIN_ENTITY)
        val canvasEntity: CanvasConfirmationEntity = holderOfParcelableCreation.findBy(Constant.CANVAS_CONFIRMATION_ENTITY)

        val transferRepository: TransferRepository = pickTransferRepository()
        val confirmModel: ConfirmModel<ConfirmationEntity> = ConfirmModel(
            dispatcherProvider = dispatcherProvider,
            factoryOfReferenceRequest = FactoryOfReferenceRequest(),
            confirmRepository = ConfirmRepository(transferRepository::confirmTransfer),
        )

        val summaryAdapter: SummaryAdapter<ConfirmationEntity, TransferSummaryModel> = SummaryAdapterForTransferOtherAccount(
            weakResources = weakResources,
            originProduct = originProduct,
        )

        return ValidationViewModel(
            appModel = appModel,
            weakResources = weakResources,
            dataOnBackDialog = createDataOnBackDialog(holderOfStringCreation),
            confirmModel = confirmModel,
            canvasEntity = canvasEntity,
            summaryAdapter = summaryAdapter,
            frequentOperationModel = pickFrequentOperationModelForTransferOtherAccount(appModel.dashboardType),
            holderOfStringCreation = holderOfStringCreation,
            holderOfIntCreation = holderOfIntCreation,
            analyticModel = createAnalyticModel(holderOfStringCreation, holderOfIntCreation),
        )
    }

    private fun pickFrequentOperationModelForTransferOtherAccount(
        dashboardType: DashboardType,
    ): FrequentOperationModel<ConfirmationEntity, TransferSummaryModel> {

        if (DashboardType.BUSINESS == dashboardType) {
            return NonFeasibleFrequentOperationModel()
        }

        return FeasibleFrequentOperationModel(
            dispatcherProvider = dispatcherProvider,
            frequentFactory = FrequentFactoryForTransferOtherAccount(),
            repository = frequentRepository,
        )
    }

    private fun createTransferOtherBank(
        holderOfStringCreation: HolderOfStringCreation,
        holderOfIntCreation: HolderOfIntCreation,
        holderOfParcelableCreation: HolderOfParcelableCreation,
    ): ValidationViewModel<out Any, out BaseSummaryModel> {

        val canvasEntity: CanvasConfirmationEntity = holderOfParcelableCreation.findBy(Constant.CANVAS_CONFIRMATION_ENTITY)

        val transferRepository: TransferRepository = pickTransferRepository()
        val confirmModel: ConfirmModel<ConfirmationEntity> = ConfirmModel(
            dispatcherProvider = dispatcherProvider,
            factoryOfReferenceRequest = FactoryOfReferenceRequest(),
            confirmRepository = ConfirmRepository(transferRepository::confirmTransfer),
        )

        val summaryAdapter: SummaryAdapter<ConfirmationEntity, TransferSummaryModel> = SummaryAdapterForTransferOtherBank(
            weakResources = weakResources,
        )

        return ValidationViewModel(
            appModel = appModel,
            weakResources = weakResources,
            dataOnBackDialog = createDataOnBackDialog(holderOfStringCreation),
            confirmModel = confirmModel,
            canvasEntity = canvasEntity,
            summaryAdapter = summaryAdapter,
            frequentOperationModel = pickFrequentOperationModelForTransferOtherBank(appModel.dashboardType),
            holderOfStringCreation = holderOfStringCreation,
            holderOfIntCreation = holderOfIntCreation,
            analyticModel = createAnalyticModel(holderOfStringCreation, holderOfIntCreation),
        )
    }

    private fun pickFrequentOperationModelForTransferOtherBank(
        dashboardType: DashboardType,
    ): FrequentOperationModel<ConfirmationEntity, TransferSummaryModel> {

        if (DashboardType.BUSINESS == dashboardType) {
            return NonFeasibleFrequentOperationModel()
        }

        return FeasibleFrequentOperationModel(
            dispatcherProvider = dispatcherProvider,
            frequentFactory = FrequentFactoryForTransferOtherBank(),
            repository = frequentRepository,
        )
    }

    private fun createPayment(
        holderOfStringCreation: HolderOfStringCreation,
        holderOfIntCreation: HolderOfIntCreation,
        holderOfParcelableCreation: HolderOfParcelableCreation,
    ): ValidationViewModel<out Any, out BaseSummaryModel> {

        val canvasEntity: CanvasConfirmationEntity = holderOfParcelableCreation.findBy(Constant.CANVAS_CONFIRMATION_ENTITY)

        val paymentRepository: PaymentRepository = pickPaymentRepository()
        val confirmModel: ConfirmModel<PaymentConfirmationEntity> = ConfirmModel(
            dispatcherProvider = dispatcherProvider,
            factoryOfReferenceRequest = FactoryOfReferenceRequest(),
            confirmRepository = ConfirmRepository(paymentRepository::confirmPayment),
        )

        val summaryAdapter: SummaryAdapter<PaymentConfirmationEntity, PaymentSummaryModel> = SummaryAdapterForPayment(
            weakResources = weakResources,
            institutionId = holderOfStringCreation.findBy(Constant.INSTITUTION_ID),
            serviceCode = holderOfStringCreation.findBy(Constant.SERVICE_CODE),
            zonalId = holderOfStringCreation.findBy(Constant.ZONAL_ID),
        )

        return ValidationViewModel(
            appModel = appModel,
            weakResources = weakResources,
            dataOnBackDialog = createDataOnBackDialog(holderOfStringCreation),
            confirmModel = confirmModel,
            canvasEntity = canvasEntity,
            summaryAdapter = summaryAdapter,
            frequentOperationModel = pickFrequentOperationModelForPayment(appModel.dashboardType),
            holderOfStringCreation = holderOfStringCreation,
            holderOfIntCreation = holderOfIntCreation,
            analyticModel = createAnalyticModel(holderOfStringCreation, holderOfIntCreation),
        )
    }

    private fun pickPaymentRepository(): PaymentRepository {

        if (DashboardType.BUSINESS == appModel.dashboardType) return businessBankingForPayment

        return personalBankingForPayment
    }

    private fun pickFrequentOperationModelForPayment(
        dashboardType: DashboardType,
    ): FrequentOperationModel<PaymentConfirmationEntity, PaymentSummaryModel> {

        if (DashboardType.BUSINESS == dashboardType) {
            return NonFeasibleFrequentOperationModel()
        }

        val delegate: FrequentOperationModel<PaymentConfirmationEntity, PaymentSummaryModel> = FeasibleFrequentOperationModel(
            dispatcherProvider = dispatcherProvider,
            frequentFactory = FrequentFactoryForPayment(),
            repository = frequentRepository,
        )

        return FrequentPaymentModel(delegate)
    }

    private fun createRecharge(
        holderOfStringCreation: HolderOfStringCreation,
        holderOfIntCreation: HolderOfIntCreation,
        holderOfParcelableCreation: HolderOfParcelableCreation,
    ): ValidationViewModel<out Any, out BaseSummaryModel> {

        val canvasEntity: CanvasConfirmationEntity = holderOfParcelableCreation.findBy(Constant.CANVAS_CONFIRMATION_ENTITY)

        val confirmModel: ConfirmModel<RechargeConfirmationEntity> = ConfirmModel(
            dispatcherProvider = dispatcherProvider,
            factoryOfReferenceRequest = FactoryOfReferenceRequest(),
            confirmRepository = ConfirmRepository(personalBankingForPayment::confirmRecharge),
        )

        val summaryAdapter: SummaryAdapter<RechargeConfirmationEntity, TransferSummaryModel> = SummaryAdapterForRecharge(
            weakResources = weakResources,
        )

        return ValidationViewModel(
            appModel = appModel,
            weakResources = weakResources,
            dataOnBackDialog = createDataOnBackDialog(holderOfStringCreation),
            confirmModel = confirmModel,
            canvasEntity = canvasEntity,
            summaryAdapter = summaryAdapter,
            frequentOperationModel = NonFeasibleFrequentOperationModel(),
            holderOfStringCreation = holderOfStringCreation,
            holderOfIntCreation = holderOfIntCreation,
            analyticModel = createAnalyticModel(holderOfStringCreation, holderOfIntCreation),
        )
    }

    private fun createAppraisalPayment(
        holderOfStringCreation: HolderOfStringCreation,
        holderOfIntCreation: HolderOfIntCreation,
        holderOfParcelableCreation: HolderOfParcelableCreation,
    ): ValidationViewModel<out Any, out BaseSummaryModel> {

        val canvasEntity: CanvasConfirmationEntity = holderOfParcelableCreation.findBy(Constant.CANVAS_CONFIRMATION_ENTITY)

        val confirmModel: ConfirmModel<PaymentConfirmationEntity> = ConfirmModel(
            dispatcherProvider = dispatcherProvider,
            factoryOfReferenceRequest = FactoryOfReferenceRequest(),
            confirmRepository = ConfirmRepository(appraisalRepository::confirmPayment),
        )

        val summaryAdapter: SummaryAdapter<PaymentConfirmationEntity, PaymentSummaryModel> = SummaryAdapterForAppraisalPayment(
            weakResources = weakResources,
        )

        return ValidationViewModel(
            appModel = appModel,
            weakResources = weakResources,
            dataOnBackDialog = createDataOnBackDialog(holderOfStringCreation),
            confirmModel = confirmModel,
            canvasEntity = canvasEntity,
            summaryAdapter = summaryAdapter,
            frequentOperationModel = NonFeasibleFrequentOperationModel(),
            holderOfStringCreation = holderOfStringCreation,
            holderOfIntCreation = holderOfIntCreation,
            analyticModel = createAnalyticModel(holderOfStringCreation, holderOfIntCreation),
        )
    }
}
