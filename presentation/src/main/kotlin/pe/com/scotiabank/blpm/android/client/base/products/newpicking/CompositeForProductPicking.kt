package pe.com.scotiabank.blpm.android.client.base.products.newpicking

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.editableinstallment.EditableInstallmentService
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.exchangerate.ExchangeRateService
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.installmentchips.InstallmentOption
import pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton.ProductGroupService
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.HolderOfChipController

interface CompositeForProductPicking: Composite, ProductGroupService, ExchangeRateService,
    HolderOfChipController<InstallmentOption>, EditableInstallmentService {

    interface Factory {

        fun create(receiver: InstanceReceiver): CompositeForProductPicking
    }
}
