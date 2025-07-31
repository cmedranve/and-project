package pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.radiobutton

import android.content.Context
import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.InstallmentCollector
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfChip
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfDynamicChipsComponent
import java.lang.ref.WeakReference

class CollectorOfInstallmentChipsComponent(
    private val weakAppContext: WeakReference<Context?>,
): InstallmentCollector {

    override fun collect(
        filteredProducts: List<ProductModel>,
        paddingOfIncludedItem: UiEntityOfPadding,
        controllerOfChip: SelectionControllerOfChipsComponent<Int>
    ): List<UiEntityOfDynamicChipsComponent<Int>> {

        val isCreditCardFound: Boolean = filteredProducts.any(::isCreditCard)
        if (!isCreditCardFound) return emptyList()

        val chipEntityOfNonInstallment: UiEntityOfChip<Int> = weakAppContext.get()
            ?.resources
            ?.let(::createChipEntityOfNonInstallment)
            ?: return emptyList()

        val chipEntitiesByText = attemptCreateChipEntitiesByText(chipEntityOfNonInstallment)
            ?: return emptyList()

        val installmentEntity = UiEntityOfDynamicChipsComponent(
            paddingOfIncludedItem,
            controllerOfChip,
            chipEntitiesByText,
            true
        )
        controllerOfChip.setComponentEntity(installmentEntity)
        controllerOfChip.setDefaultChip(chipEntityOfNonInstallment)

        return listOf(installmentEntity)
    }

    private fun isCreditCard(product: ProductModel): Boolean {
        return Constant.TC.equals(product.productType, ignoreCase = true)
    }

    private fun attemptCreateChipEntitiesByText(
        chipEntityOfNonInstallment: UiEntityOfChip<Int>,
    ): LinkedHashMap<String, UiEntityOfChip<Int>>?  {

        val chipEntitiesByText: LinkedHashMap<String, UiEntityOfChip<Int>> = LinkedHashMap()
        chipEntitiesByText[chipEntityOfNonInstallment.text] = chipEntityOfNonInstallment

        for (number in MIN_NUMBER_OF_INSTALLMENTS_ALLOWED..MAX_NUMBER_OF_INSTALLMENTS) {
            val chipEntity = attemptCreateChipEntityOfInstallment(number)
                ?: return null
            chipEntitiesByText[chipEntity.text] = chipEntity
        }

        return chipEntitiesByText
    }

    private fun createChipEntityOfNonInstallment(res: Resources): UiEntityOfChip<Int> {
        val text: String = res.getString(R.string.no_installments)
        return UiEntityOfChip(text, MIN_NUMBER_OF_INSTALLMENTS)
    }

    private fun attemptCreateChipEntityOfInstallment(number: Int): UiEntityOfChip<Int>? {
        val chipText: String = weakAppContext.get()
            ?.resources
            ?.getQuantityString(R.plurals.number_of_installments, number, number)
            ?: return null
        return UiEntityOfChip(chipText, number)
    }

    companion object {

        const val MIN_NUMBER_OF_INSTALLMENTS_ALLOWED = 2
        const val MIN_NUMBER_OF_INSTALLMENTS = 1
        const val MAX_NUMBER_OF_INSTALLMENTS = 36
    }
}
