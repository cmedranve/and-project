package pe.com.scotiabank.blpm.android.client.newdashboard.add

import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.base.date.DateFormatter
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CurrencyFormatter
import pe.com.scotiabank.blpm.android.data.model.RecentTransactionModel
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple.checkbox.AdapterFactoryOfCheckBox
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple.ControllerOfMultipleSelection
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import java.lang.ref.WeakReference

class ComposerOfCheckableOperation(
    weakResources: WeakReference<Resources?>,
    dividerPositions: List<Int>,
    paddingEntity: UiEntityOfPadding,
    currencyAmountFormatter: CurrencyFormatter,
    dateFormatter: DateFormatter,
    receiver: InstanceReceiver,
): RecentOperationService {

    override val controller: ControllerOfMultipleSelection<RecentTransactionModel> = ControllerOfMultipleSelection(
        instanceReceiver = receiver,
    )
    private val converter: ConverterForCheckableOperation = ConverterForCheckableOperation(
        weakResources = weakResources,
        dividerPositions = dividerPositions,
        paddingEntity = paddingEntity,
        currencyAmountFormatter = currencyAmountFormatter,
        dateFormatter = dateFormatter,
        controller = controller,
    )

    private val itemEntities: MutableList<UiEntityOfCheckableButton<RecentTransactionModel>> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfCheckableButton<RecentTransactionModel>> {

        val adapterFactory: AdapterFactoryOfCheckBox<RecentTransactionModel> = AdapterFactoryOfCheckBox()
        return UiCompound(itemEntities, adapterFactory, visibilitySupplier)
    }

    override fun add(operation: RecentTransactionModel) {
        val entity: UiEntityOfCheckableButton<RecentTransactionModel> = converter.toUiEntityOfCheckBox(
            operation = operation,
        )
        itemEntities.add(entity)
    }
}
