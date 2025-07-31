package pe.com.scotiabank.blpm.android.client.host.session.subflow

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.newpayment.enabled.EnabledPaymentCoordinatorFactory
import java.lang.ref.WeakReference

class BillPaymentSubFlowFactory: SubFlowFactory {

    override fun create(
        hub: Hub,
        parentScope: CoroutineScope,
        weakParent: WeakReference<out Coordinator?>,
    ): Coordinator {

        val appModel: AppModel = hub.appModel
        val factory = EnabledPaymentCoordinatorFactory(
            hub = hub,
            retrofit = appModel.sessionRetrofit,
            parentScope = parentScope,
            weakParent = weakParent,
        )
        return factory.create()
    }

    companion object {

        val SHORTCUT_ID: String
            @JvmStatic
            get() = "bill_payment_shortcut_id"

        val SHORTCUT_NAME: String
            @JvmStatic
            get() = "Pago de servicios"
    }
}