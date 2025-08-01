package pe.com.scotiabank.blpm.android.client.newdashboard.add

import pe.com.scotiabank.blpm.android.data.model.RecentTransactionModel
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple.ControllerOfMultipleSelection

interface RecentOperationService {

    val controller: ControllerOfMultipleSelection<RecentTransactionModel>

    fun add(operation: RecentTransactionModel)
}