package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.summary.FrequentOperationSummary
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.FormatterUtil
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckable
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple.ControllerOfMultipleSelection

class SelectionHelper {

    fun byCurrency(
        entity: UiEntityOfCheckable<FrequentOperationModel>,
    ): Currency = entity.data?.currency?.let(Currency::identifyBy) ?: Currency.NONE

    fun toAmount(
        entity: UiEntityOfCheckable<FrequentOperationModel>,
    ): Double = entity.data
        ?.amount
        ?: 0.0

    fun toSumOf(entry: Map.Entry<Currency, List<Double>>): Double = entry.value.sum()

    fun formatAmount(entry: Map.Entry<Currency, Double>): CharSequence {
        val currency: Currency = entry.key
        val amount: Double = entry.value
        return currency.symbol + Constant.SPACE_WHITE + FormatterUtil.format(amount)
    }

    fun attemptToFrequentOperation(
        entity: UiEntityOfCheckable<FrequentOperationModel>,
    ): FrequentOperationModel? = entity.data
}

class SummaryHelper {

    fun isEmpty(summary: FrequentOperationSummary): Boolean = summary.total == 0

    fun findFirstTypeNotEmpty(
        summaries: List<FrequentOperationSummary>,
    ): FrequentOperationType? = summaries
        .firstOrNull(::isNotEmpty)
        ?.type

    private fun isNotEmpty(summary: FrequentOperationSummary): Boolean = summary.total > 0
}

class OperationMatcher {

    fun isMatching(
        underEvaluation: FrequentOperationModel?,
        target: FrequentOperationModel,
    ): Boolean = target.id == underEvaluation?.id
}

class PaymentEnablingWithCreditCard {

    fun isEnabled(
        controller: ControllerOfMultipleSelection<FrequentOperationModel>,
    ): Boolean {
        val itemSelection: Collection<UiEntityOfCheckable<FrequentOperationModel>> = controller.itemSelection
        for (item in itemSelection) {
            val frequentOperation: FrequentOperationModel = item.data ?: continue
            if (frequentOperation.isPayableWithCreditCard.not()) return false
        }
        return true
    }
}