package pe.com.scotiabank.blpm.android.client.base.receipt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.factories.AnalyticsBaseConstant
import pe.com.scotiabank.blpm.android.analytics.factories.SystemDataFactory
import pe.com.scotiabank.blpm.android.analytics.factories.loan.success.LoanSuccessFactory
import pe.com.scotiabank.blpm.android.analytics.factories.payment.creditcard.success.CreditCardSuccessFactory
import pe.com.scotiabank.blpm.android.analytics.factories.payment.creditcard.summary.CreditCardSummaryFactory
import pe.com.scotiabank.blpm.android.analytics.factories.receipt.ReceiptFactory
import pe.com.scotiabank.blpm.android.analytics.factories.receipt.ReceiptInformation
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsUtil
import pe.com.scotiabank.blpm.android.client.base.carrier.HolderOfParcelableCreation
import pe.com.scotiabank.blpm.android.client.base.carrier.HolderOfStringCreation
import pe.com.scotiabank.blpm.android.client.model.CreditCardModel
import pe.com.scotiabank.blpm.android.client.payment.creditcard.analytic.success.CreditCardSuccessAnalyticModel
import pe.com.scotiabank.blpm.android.client.payment.creditcard.analytic.summary.CreditCardSummaryAnalyticModel
import pe.com.scotiabank.blpm.android.client.payment.creditcard.step3.CreditCardConfirmationActivity
import pe.com.scotiabank.blpm.android.client.payment.loan.analytic.success.LoanSuccessAnalyticModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import javax.inject.Inject
import javax.inject.Named

class ReceiptViewModelFactory @Inject constructor(
    private val analyticsDataGateway: AnalyticsDataGateway,
    @Named("systemDataFactorySession") private val systemDataFactory: SystemDataFactory,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ReceiptViewModel::class.java)) {
            return createViewModel(extras) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    private fun createViewModel(extras: CreationExtras): ReceiptViewModel {
        val holderOfStringCreation = HolderOfStringCreation(extras)
        val holderOfParcelableCreation = HolderOfParcelableCreation(extras)

        val analyticModel = createAnalyticModel(holderOfStringCreation)
        val loanSuccessAnalyticModel = createLoanSuccessAnalyticModel(holderOfStringCreation)
        val creditCardSuccessAnalyticModel = createCreditCardSuccessAnalyticModel(
            holderOfParcelableCreation = holderOfParcelableCreation,
            holderOfStringCreation = holderOfStringCreation,
        )
        val creditCardSummaryAnalyticModel = createCreditCardSummaryAnalyticModel(
            holderOfParcelableCreation = holderOfParcelableCreation,
            holderOfStringCreation = holderOfStringCreation,
        )

        return ReceiptViewModel(
            analyticModel = analyticModel,
            loanSuccessAnalyticModel = loanSuccessAnalyticModel,
            creditCardSuccessAnalyticModel = creditCardSuccessAnalyticModel,
            creditCardSummaryAnalyticModel = creditCardSummaryAnalyticModel,
        )
    }

    private fun createAnalyticModel(holder: HolderOfStringCreation): AnalyticModel {
        val analyticFactory = createAnalyticFactory(holder)
        return AnalyticModel(analyticsDataGateway, analyticFactory)
    }

    private fun createAnalyticFactory(holder: HolderOfStringCreation): ReceiptFactory {

        val operationName: String = holder.findBy(ReceiptInformation.OPERATION_NAME)
        val information: ReceiptInformation = handleReceiptInformation(operationName)

        return ReceiptFactory(
            systemDataFactory = systemDataFactory,
            information = information,
            processType = holder.findBy(AnalyticsBaseConstant.TYPE_PROCESS),
            typeOfOriginProduct = holder.findBy(AnalyticsBaseConstant.TYPE_OF_ORIGIN_ACCOUNT),
            originCurrency = holder.findBy(AnalyticsBaseConstant.ORIGIN_CURRENCY),
            typeOfDestinationProduct = holder.findBy(AnalyticsBaseConstant.TYPE_OF_DESTINATION_ACCOUNT),
            destinationCurrency = holder.findBy(AnalyticsBaseConstant.DESTINATION_CURRENCY),
            amountText = holder.findBy(AnalyticsBaseConstant.AMOUNT),
            authMode = holder.findBy(ReceiptActivity.PARAM_AUTH_MODE),
            description = holder.findBy(AnalyticsBaseConstant.DESCRIPTION),
            movementType = holder.findBy(AnalyticsBaseConstant.TYPE_OF_MOVEMENT),
        )
    }

    private fun createLoanSuccessAnalyticModel(
        holderOfStringCreation: HolderOfStringCreation
    ): LoanSuccessAnalyticModel {
        val loanName: String = holderOfStringCreation.findBy(ReceiptActivity.PARAM_LOAN_NAME)
        val loanCurrency: String = holderOfStringCreation.findBy(ReceiptActivity.PARAM_LOAN_CURRENCY)
        val totalAmount: String = holderOfStringCreation.findBy(ReceiptActivity.PARAM_LOAN_TOTAL_AMOUNT)
        val installmentsNumber: String = holderOfStringCreation.findBy(ReceiptActivity.PARAM_LOAN_INSTALLMENTS_NUMBER)
        val monthlyFee: String = holderOfStringCreation.findBy(ReceiptActivity.PARAM_LOAN_MONTHLY_FEE)
        val paymentType: String = holderOfStringCreation.findBy(ReceiptActivity.PARAM_LOAN_PAYMENT_TYPE)
        val originAccountType: String = holderOfStringCreation.findBy(ReceiptActivity.PARAM_LOAN_ORIGIN_ACCOUNT_TYPE)
        val originCurrency: String = holderOfStringCreation.findBy(ReceiptActivity.PARAM_LOAN_ORIGIN_CURRENCY)
        val description: String = holderOfStringCreation.findBy(ReceiptActivity.PARAM_LOAN_DESCRIPTION)

        val analyticFactory = createLoanSuccessAnalyticFactory(
            accountType = loanName,
            amount = totalAmount,
            currency = loanCurrency,
            installmentsNumber = installmentsNumber,
            monthlyFee = monthlyFee,
            paymentType = paymentType,
            originAccountType = originAccountType,
            originCurrency = originCurrency,
            description = description,
        )

        return LoanSuccessAnalyticModel(analyticsDataGateway, analyticFactory)
    }

    private fun createLoanSuccessAnalyticFactory(
        accountType: String,
        amount: String,
        currency: String,
        installmentsNumber: String,
        monthlyFee: String,
        paymentType: String,
        originAccountType: String,
        originCurrency: String,
        description: String,
    ): LoanSuccessFactory {
        val normalizedAccountType: String = AnalyticsUtil.normalizeHyphenText(accountType)
        val normalizedOriginAccount: String = AnalyticsUtil.normalizeHyphenText(originAccountType)
            .replace(Constant.DOT, Constant.EMPTY_STRING)

        return LoanSuccessFactory(
            systemDataFactory = systemDataFactory,
            accountType = normalizedAccountType,
            amount = amount,
            currency = currency,
            description = description,
            installmentsNumber = installmentsNumber,
            monthlyFee = monthlyFee,
            originAccountType = normalizedOriginAccount,
            originCurrency = originCurrency,
            paymentType = paymentType,
        )
    }

    private fun handleReceiptInformation(operationName: String): ReceiptInformation = try {
        ReceiptInformation.valueOf(operationName)
    } catch (throwable: Throwable) {
        ReceiptInformation.NONE
    }

    private fun createCreditCardSuccessAnalyticModel(
        holderOfParcelableCreation: HolderOfParcelableCreation,
        holderOfStringCreation: HolderOfStringCreation,
    ): CreditCardSuccessAnalyticModel {

        val creditCardModel: CreditCardModel? = holderOfParcelableCreation.getBy(CreditCardConfirmationActivity.CREDIT_CARD_MODEL)
        val amountPen: String = holderOfStringCreation.findBy(AnalyticsConstant.AMOUNT_PEN)
        val amountUsd: String = holderOfStringCreation.findBy(AnalyticsConstant.AMOUNT_USD)
        val currency: String = holderOfStringCreation.findBy(AnalyticsConstant.CURRENCY)
        val originAccountType: String = holderOfStringCreation.findBy(AnalyticsConstant.TYPE_OF_ORIGIN_ACCOUNT)
        val originCurrency: String = holderOfStringCreation.findBy(AnalyticsConstant.ORIGIN_CURRENCY)
        val payType: String = holderOfStringCreation.findBy(AnalyticsConstant.TYPE_PAY)
        val processRoute: String = holderOfStringCreation.findBy(AnalyticsConstant.ROUTE_PROCESS)
        val questionTwo: String = holderOfStringCreation.findBy(AnalyticsConstant.QUESTION_TWO)

        val analyticFactory = createCreditCardSuccessFactory(
            creditCardModel = creditCardModel,
            amountPen = amountPen,
            amountUsd = amountUsd,
            currency = currency,
            originAccountType = originAccountType,
            originCurrency = originCurrency,
            payType = payType,
            processRoute = processRoute,
            questionTwo = questionTwo,
        )

        return CreditCardSuccessAnalyticModel(analyticsDataGateway, analyticFactory)
    }

    private fun createCreditCardSuccessFactory(
        creditCardModel: CreditCardModel?,
        amountPen: String,
        amountUsd: String,
        currency: String,
        originAccountType: String,
        originCurrency: String,
        payType: String,
        processRoute: String,
        questionTwo: String,
    ): CreditCardSuccessFactory {

        val brand: String = creditCardModel?.creditCardBrand.orEmpty()
        val classifier: String = creditCardModel?.classifier.orEmpty()
        val cardType: String = getCardTypeAnalytics(brand, classifier)
        val company: String = creditCardModel?.entityName.orEmpty()
        val companyNormalized: String = AnalyticsUtil.normalizeHyphenText(company)
        val originAccountNormalized: String = AnalyticsUtil.normalizeHyphenText(originAccountType)
            .replace(Constant.DOT, Constant.EMPTY_STRING)

        return CreditCardSuccessFactory(
            systemDataFactory = systemDataFactory,
            amountPen = amountPen,
            amountUsd = amountUsd,
            cardType = cardType,
            company = companyNormalized,
            currency = currency,
            originAccountType = originAccountNormalized,
            originCurrency = originCurrency,
            payType = payType,
            processRoute = processRoute,
            questionTwo = questionTwo,
        )
    }

    private fun createCreditCardSummaryAnalyticModel(
        holderOfParcelableCreation: HolderOfParcelableCreation,
        holderOfStringCreation: HolderOfStringCreation,
    ): CreditCardSummaryAnalyticModel {

        val creditCardModel: CreditCardModel? = holderOfParcelableCreation.getBy(CreditCardConfirmationActivity.CREDIT_CARD_MODEL)
        val amountPen: String = holderOfStringCreation.findBy(AnalyticsConstant.AMOUNT_PEN)
        val amountUsd: String = holderOfStringCreation.findBy(AnalyticsConstant.AMOUNT_USD)
        val currency: String = holderOfStringCreation.findBy(AnalyticsConstant.CURRENCY)
        val payType: String = holderOfStringCreation.findBy(AnalyticsConstant.TYPE_PAY)
        val processRoute: String = holderOfStringCreation.findBy(AnalyticsConstant.ROUTE_PROCESS)

        val analyticFactory = createCreditCardSummaryFactory(
            creditCardModel = creditCardModel,
            amountPen = amountPen,
            amountUsd = amountUsd,
            currency = currency,
            payType = payType,
            processRoute = processRoute,
        )

        return CreditCardSummaryAnalyticModel(analyticsDataGateway, analyticFactory)
    }

    private fun createCreditCardSummaryFactory(
        creditCardModel: CreditCardModel?,
        amountPen: String,
        amountUsd: String,
        currency: String,
        payType: String,
        processRoute: String,
    ): CreditCardSummaryFactory {

        val brand: String = creditCardModel?.creditCardBrand.orEmpty()
        val classifier: String = creditCardModel?.classifier.orEmpty()
        val cardType: String = getCardTypeAnalytics(brand, classifier)

        return CreditCardSummaryFactory(
            systemDataFactory = systemDataFactory,
            amountPen = amountPen,
            amountUsd = amountUsd,
            cardType = cardType,
            currency = currency,
            payType = payType,
            processRoute = processRoute,
        )
    }

    private fun getCardTypeAnalytics(brand: String, classifier: String): String {
        if (brand.isEmpty() || classifier.isEmpty()) return Constant.HYPHEN_STRING
        val cardType: String = brand + Constant.HYPHEN_STRING + classifier
        return AnalyticsUtil.normalizeHyphenText(cardType)
    }
}
