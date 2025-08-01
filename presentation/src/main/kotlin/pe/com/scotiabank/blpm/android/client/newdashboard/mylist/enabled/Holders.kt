package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple.ControllerOfMultipleSelection

interface HolderOfCheckBoxController {

    val controller: ControllerOfMultipleSelection<FrequentOperationModel>
}

interface HolderOfImmediateAvailability {

    var isImmediateAvailable: Boolean
}