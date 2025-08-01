package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.payment

import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CurrencyFormatter
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.FrequentOperationService
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.StoreOfCheckableOperation
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.HolderOfCheckBoxController
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.OperationMatcher
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple.checkbox.AdapterFactoryOfCheckBox
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple.ControllerOfMultipleSelection
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import java.lang.ref.WeakReference
import java.util.function.BiFunction

class ComposerOfCheckablePayment(
    weakResources: WeakReference<Resources?>,
    dividerPositions: List<Int>,
    paddingEntity: UiEntityOfPadding,
    formatter: CurrencyFormatter,
    receiver: InstanceReceiver,
    matcher: OperationMatcher,
): HolderOfCheckBoxController, FrequentOperationService {

    private val store = StoreOfCheckableOperation(
        receiver = receiver,
        matcher = matcher,
        converter = BiFunction(::toUiEntityOfCheckBox),
    )

    override val controller: ControllerOfMultipleSelection<FrequentOperationModel> by store::controller

    private val converterForEnabled: ConverterForEnabledPayment = ConverterForEnabledPayment(
        weakResources = weakResources,
        dividerPositions = dividerPositions,
        paddingEntity = paddingEntity,
        formatter = formatter,
        receiver = receiver,
        controller = store.controller,
    )
    private val converterForDisabled: ConverterForDisabledPayment = ConverterForDisabledPayment(
        dividerPositions = dividerPositions,
        paddingEntity = paddingEntity,
        receiver = receiver,
        controller = store.controller,
    )

    override val quantity: Int by store::quantity

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfCheckableButton<FrequentOperationModel>> {

        val adapterFactory: AdapterFactoryOfCheckBox<FrequentOperationModel> = AdapterFactoryOfCheckBox()
        return UiCompound(store.itemEntities, adapterFactory, visibilitySupplier)
    }

    override fun add(frequentOperation: FrequentOperationModel) {
        store.add(frequentOperation)
    }

    private fun toUiEntityOfCheckBox(
        frequentOperation: FrequentOperationModel,
        id: Long,
    ): UiEntityOfCheckableButton<FrequentOperationModel> {

        if (frequentOperation.isEnabled) {
            return converterForEnabled.toUiEntityOfCheckBox(frequentOperation, id)
        }

        return converterForDisabled.toUiEntityOfCheckBox(frequentOperation, id)
    }

    override fun edit(frequentOperation: FrequentOperationModel) {
        store.edit(frequentOperation)
    }

    override fun remove(frequentOperation: FrequentOperationModel) {
        store.remove(frequentOperation)
    }

    override fun clear() {
        store.clear()
    }
}