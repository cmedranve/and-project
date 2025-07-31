package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.personalbanking

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.fasterxml.jackson.databind.ObjectMapper
import com.scotiabank.canvaspe.confirmation.entity.CanvasConfirmationEntity
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
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
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FeasibleFrequentOperationModel
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentFactoryForPayment
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentFactoryForTransferOtherAccount
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentFactoryForTransferOtherBank
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentPaymentModel
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.NonFeasibleFrequentOperationModel
import pe.com.scotiabank.blpm.android.client.base.operation.token.request.personalbanking.TokenRequestModelForPersonalBanking
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.AnalyticModel
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.ConfirmModel
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.ConfirmRepository
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.DataOnBackDialog
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.FactoryOfReferenceRequest
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.SmartKeyModel
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapter
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForPayment
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForRecharge
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForTransferOtherAccount
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapterForTransferOtherBank
import pe.com.scotiabank.blpm.android.client.model.BaseSummaryModel
import pe.com.scotiabank.blpm.android.client.model.PaymentSummaryModel
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.model.TransferSummaryModel
import pe.com.scotiabank.blpm.android.client.model.security.SecurityAuthModel
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardType
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.analytics.RechargeAnalytics
import pe.com.scotiabank.blpm.android.data.entity.ConfirmationEntity
import pe.com.scotiabank.blpm.android.data.entity.PaymentConfirmationEntity
import pe.com.scotiabank.blpm.android.data.entity.RechargeConfirmationEntity
import pe.com.scotiabank.blpm.android.data.net.RestSmartKeyApiService
import pe.com.scotiabank.blpm.android.data.repository.FrequentOperationDataRepository
import pe.com.scotiabank.blpm.android.data.repository.PaymentDataRepository
import pe.com.scotiabank.blpm.android.data.repository.TransferDataRepository
import pe.com.scotiabank.blpm.android.data.repository.otp.SmartKeyRepository
import pe.com.scotiabank.blpm.android.data.repository.security.SecurityAuthRepository
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Named

class ViewModelFactory @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val objectMapper: ObjectMapper,
    private val appModel: AppModel,
    appContext: Context,
    securityAuthRepository: SecurityAuthRepository,
    private val transferRepository: TransferDataRepository,
    private val paymentRepository: PaymentDataRepository,
    private val frequentRepository: FrequentOperationDataRepository,
    private val analyticsDataGateway: AnalyticsDataGateway,
    @Named("systemDataFactorySession") private val systemDataFactory: SystemDataFactory,
) : ViewModelProvider.Factory {

    private val weakAppContext: WeakReference<Context?> = WeakReference(appContext)
    private val weakResources: WeakReference<Resources?> = WeakReference(appContext.resources)
    private val factoryOfBiometricBuddyTip = FactoryOfBiometricBuddyTip(appModel, weakResources)
    private val tokenRequestModelForPersonalBanking = TokenRequestModelForPersonalBanking(
        dispatcherProvider = dispatcherProvider,
        repository = securityAuthRepository,
    )
    private val validationModel = ValidationModel(dispatcherProvider, securityAuthRepository)

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

        MoneyOperationType.TRANSFER_OTHER_ACCOUNT -> createTransferOtherAccount(holderOfStringCreation, holderOfIntCreation, holderOfParcelableCreation)
        MoneyOperationType.TRANSFER_OTHER_BANK -> createTransferOtherBank(holderOfStringCreation, holderOfIntCreation, holderOfParcelableCreation)
        MoneyOperationType.PAYMENT -> createPayment(holderOfStringCreation, holderOfIntCreation, holderOfParcelableCreation)
        MoneyOperationType.RECHARGE -> createRecharge(holderOfStringCreation, holderOfIntCreation, holderOfParcelableCreation)
        else -> throw IllegalArgumentException("Unknown Money Operation Type Class: " + operationType.name)
    }

    private fun createTransferOtherAccount(
        holderOfStringCreation: HolderOfStringCreation,
        holderOfIntCreation: HolderOfIntCreation,
        holderOfParcelableCreation: HolderOfParcelableCreation,
    ): ValidationViewModel<out Any, out BaseSummaryModel> {

        val originProduct: ProductModel = holderOfParcelableCreation.findBy(Constant.ORIGIN_ENTITY)
        val canvasEntity: CanvasConfirmationEntity = holderOfParcelableCreation.findBy(Constant.CANVAS_CONFIRMATION_ENTITY)
        val securityAuth: SecurityAuthModel = holderOfParcelableCreation.findBy(Constant.SECURITY_AUTH)

        val summaryAdapter: SummaryAdapter<ConfirmationEntity, TransferSummaryModel> = SummaryAdapterForTransferOtherAccount(
            weakResources = weakResources,
            originProduct = originProduct,
        )

        val confirmModel: ConfirmModel<ConfirmationEntity> = ConfirmModel(
            dispatcherProvider = dispatcherProvider,
            factoryOfReferenceRequest = FactoryOfReferenceRequest(),
            confirmRepository = ConfirmRepository(transferRepository::confirmTransfer),
        )

        val frequentOperationModel: FrequentOperationModel<ConfirmationEntity, TransferSummaryModel> = FeasibleFrequentOperationModel(
            dispatcherProvider = dispatcherProvider,
            frequentFactory = FrequentFactoryForTransferOtherAccount(),
            repository = frequentRepository,
        )

        return ValidationViewModel(
            appModel = appModel,
            weakAppContext = weakAppContext,
            weakResources = weakResources,
            dataOnBackDialog = createDataOnBackDialog(holderOfStringCreation),
            helperForAuthError = HelperForAuthErrorDialog(weakResources),
            factoryOfBiometricBuddyTip = factoryOfBiometricBuddyTip,
            tokenRequestModel = tokenRequestModelForPersonalBanking,
            smartKeyModel = createSmartKeyModel(),
            validationModel = validationModel,
            confirmModel = confirmModel,
            canvasEntity = canvasEntity,
            securityAuth =  securityAuth,
            summaryAdapter = summaryAdapter,
            frequentOperationModel = frequentOperationModel,
            holderOfStringCreation = holderOfStringCreation,
            holderOfIntCreation = holderOfIntCreation,
            analyticModel = createAnalyticModel(holderOfStringCreation, holderOfIntCreation),
        )
    }

    private fun createDataOnBackDialog(
        holder: HolderOfStringCreation,
    ) = DataOnBackDialog(
        title = holder.findBy(DataOnBackDialog.TITLE_KEY),
        message = holder.findBy(DataOnBackDialog.MESSAGE_KEY),
    )

    private fun createSmartKeyModel(): SmartKeyModel {
        val api: RestSmartKeyApiService = appModel.sessionRetrofit.create(RestSmartKeyApiService::class.java)
        val repository = SmartKeyRepository(api, objectMapper)
        return SmartKeyModel(dispatcherProvider, repository)
    }

    private fun createAnalyticModel(holderOfStringCreation: HolderOfStringCreation, holderOfIntCreation: HolderOfIntCreation): AnalyticModel {
        val analyticFactory = createAnalyticFactory(holderOfStringCreation, holderOfIntCreation)
        return AnalyticModel(analyticsDataGateway, analyticFactory)
    }

    private fun createAnalyticFactory(holderOfStringCreation: HolderOfStringCreation, holderOfIntCreation: HolderOfIntCreation): ValidationFactory {

        val operationName: String = holderOfStringCreation.findBy(ValidationInformation.OPERATION_NAME)
        val information: ValidationInformation = ValidationInformation.valueOf(operationName)

        return ValidationFactory(
            systemDataFactory = systemDataFactory,
            information = information,
            processType = holderOfStringCreation.findBy(AnalyticsConstant.TYPE_PROCESS),
            company = holderOfStringCreation.findBy(AnalyticsConstant.COMPANY),
            typeOfDocument = holderOfStringCreation.findBy(RechargeAnalytics.TYPE_VOUCHER_RECHARGES),
            typeOfOriginProduct = holderOfStringCreation.findBy(AnalyticsConstant.TYPE_OF_ORIGIN_ACCOUNT),
            originCurrency = holderOfStringCreation.findBy(AnalyticsConstant.ORIGIN_CURRENCY),
            typeOfDestinationProduct = holderOfStringCreation.findBy(AnalyticsConstant.TYPE_OF_DESTINATION_ACCOUNT),
            destinationCurrency = holderOfStringCreation.findBy(AnalyticsConstant.DESCRIPTION),
            amountText = holderOfStringCreation.findBy(AnalyticsConstant.AMOUNT),
            movementType = holderOfStringCreation.findBy(AnalyticsConstant.TYPE_MOVEMENT),
            status = holderOfStringCreation.findBy(AnalyticsConstant.STATUS),
            paymentType = holderOfStringCreation.findBy(AnalyticsConstant.TYPE_PAY),
            questionFive = holderOfStringCreation.findBy(AnalyticsConstant.QUESTION_FIVE),
            numberOfElements = holderOfIntCreation.findBy(AnalyticsConstant.ELEMENTS_NUMBER),
            installments = holderOfStringCreation.findBy(AnalyticsConstant.INSTALLMENTS_NUMBER)
        )
    }

    private fun createTransferOtherBank(
        holderOfStringCreation: HolderOfStringCreation,
        holderOfIntCreation: HolderOfIntCreation,
        holderOfParcelableCreation: HolderOfParcelableCreation,
    ): ValidationViewModel<out Any, out BaseSummaryModel> {

        val canvasEntity: CanvasConfirmationEntity = holderOfParcelableCreation.findBy(Constant.CANVAS_CONFIRMATION_ENTITY)
        val securityAuth: SecurityAuthModel = holderOfParcelableCreation.findBy(Constant.SECURITY_AUTH)

        val summaryAdapter: SummaryAdapter<ConfirmationEntity, TransferSummaryModel> = SummaryAdapterForTransferOtherBank(
            weakResources = weakResources,
        )

        val confirmModel: ConfirmModel<ConfirmationEntity> = ConfirmModel(
            dispatcherProvider = dispatcherProvider,
            factoryOfReferenceRequest = FactoryOfReferenceRequest(),
            confirmRepository = ConfirmRepository(transferRepository::confirmTransfer),
        )

        val frequentOperationModel: FrequentOperationModel<ConfirmationEntity, TransferSummaryModel> = FeasibleFrequentOperationModel(
            dispatcherProvider = dispatcherProvider,
            frequentFactory = FrequentFactoryForTransferOtherBank(),
            repository = frequentRepository,
        )

        return ValidationViewModel(
            appModel = appModel,
            weakAppContext = weakAppContext,
            weakResources = weakResources,
            dataOnBackDialog = createDataOnBackDialog(holderOfStringCreation),
            helperForAuthError = HelperForAuthErrorDialog(weakResources),
            factoryOfBiometricBuddyTip = factoryOfBiometricBuddyTip,
            tokenRequestModel = tokenRequestModelForPersonalBanking,
            smartKeyModel = createSmartKeyModel(),
            validationModel = validationModel,
            confirmModel = confirmModel,
            canvasEntity = canvasEntity,
            securityAuth =  securityAuth,
            summaryAdapter = summaryAdapter,
            frequentOperationModel = frequentOperationModel,
            holderOfStringCreation = holderOfStringCreation,
            holderOfIntCreation = holderOfIntCreation,
            analyticModel = createAnalyticModel(holderOfStringCreation, holderOfIntCreation),
        )
    }

    private fun createPayment(
        holderOfStringCreation: HolderOfStringCreation,
        holderOfIntCreation: HolderOfIntCreation,
        holderOfParcelableCreation: HolderOfParcelableCreation,
    ): ValidationViewModel<out Any, out BaseSummaryModel> {

        val canvasEntity: CanvasConfirmationEntity = holderOfParcelableCreation.findBy(Constant.CANVAS_CONFIRMATION_ENTITY)
        val securityAuth: SecurityAuthModel = holderOfParcelableCreation.findBy(Constant.SECURITY_AUTH)

        val summaryAdapter: SummaryAdapter<PaymentConfirmationEntity, PaymentSummaryModel> = SummaryAdapterForPayment(
            weakResources = weakResources,
            institutionId = holderOfStringCreation.findBy(Constant.INSTITUTION_ID),
            serviceCode = holderOfStringCreation.findBy(Constant.SERVICE_CODE),
            zonalId = holderOfStringCreation.findBy(Constant.ZONAL_ID),
        )

        val confirmModel: ConfirmModel<PaymentConfirmationEntity> = ConfirmModel(
            dispatcherProvider = dispatcherProvider,
            factoryOfReferenceRequest = FactoryOfReferenceRequest(),
            confirmRepository = ConfirmRepository(paymentRepository::confirmPayment),
        )

        return ValidationViewModel(
            appModel = appModel,
            weakAppContext = weakAppContext,
            weakResources = weakResources,
            dataOnBackDialog = createDataOnBackDialog(holderOfStringCreation),
            helperForAuthError = HelperForAuthErrorDialog(weakResources),
            factoryOfBiometricBuddyTip = factoryOfBiometricBuddyTip,
            tokenRequestModel = tokenRequestModelForPersonalBanking,
            smartKeyModel = createSmartKeyModel(),
            validationModel = validationModel,
            confirmModel = confirmModel,
            canvasEntity = canvasEntity,
            securityAuth =  securityAuth,
            summaryAdapter = summaryAdapter,
            frequentOperationModel = pickFrequentOperationModelForPayment(appModel.dashboardType),
            holderOfStringCreation = holderOfStringCreation,
            holderOfIntCreation = holderOfIntCreation,
            analyticModel = createAnalyticModel(holderOfStringCreation, holderOfIntCreation),
        )
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
        val securityAuth: SecurityAuthModel = holderOfParcelableCreation.findBy(Constant.SECURITY_AUTH)

        val summaryAdapter: SummaryAdapter<RechargeConfirmationEntity, TransferSummaryModel> = SummaryAdapterForRecharge(
            weakResources = weakResources,
        )

        val confirmModel: ConfirmModel<RechargeConfirmationEntity> = ConfirmModel(
            dispatcherProvider = dispatcherProvider,
            factoryOfReferenceRequest = FactoryOfReferenceRequest(),
            confirmRepository = ConfirmRepository(paymentRepository::confirmRecharge),
        )

        return ValidationViewModel(
            appModel = appModel,
            weakAppContext = weakAppContext,
            weakResources = weakResources,
            dataOnBackDialog = createDataOnBackDialog(holderOfStringCreation),
            helperForAuthError = HelperForAuthErrorDialog(weakResources),
            factoryOfBiometricBuddyTip = factoryOfBiometricBuddyTip,
            tokenRequestModel = tokenRequestModelForPersonalBanking,
            smartKeyModel = createSmartKeyModel(),
            validationModel = validationModel,
            confirmModel = confirmModel,
            canvasEntity = canvasEntity,
            securityAuth =  securityAuth,
            summaryAdapter = summaryAdapter,
            frequentOperationModel = NonFeasibleFrequentOperationModel(),
            holderOfStringCreation = holderOfStringCreation,
            holderOfIntCreation = holderOfIntCreation,
            analyticModel = createAnalyticModel(holderOfStringCreation, holderOfIntCreation),
        )
    }
}
