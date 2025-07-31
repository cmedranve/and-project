package pe.com.scotiabank.blpm.android.client.host.session.subflow

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.client.base.carrier.HolderOfStringCreation

class SubFlowLauncher(
    val shortcutId: String,
    val shortcutName: String,
    val analyticValue: String,
    val factorySupplier: Supplier<SubFlowFactory>,
) {

    companion object {

        @JvmStatic
        val SHORTCUT_ID: String
            get() = "shortcut_id"

        @JvmStatic
        val SHORTCUT_NAME: String
            get() = "shortcut_name"

        @JvmStatic
        val ANALYTIC_VALUE: String
            get() = "analytic_value"

        @JvmStatic
        fun createFrom(holder: HolderOfStringCreation): SubFlowLauncher {

            val shortcutId: String = holder.findBy(SHORTCUT_ID)
            val shortcutName: String = holder.findBy(SHORTCUT_NAME)
            val analyticValue: String = holder.findBy(ANALYTIC_VALUE)
            val supplier: Supplier<SubFlowFactory> = identifyFactorySupplierBy(shortcutId)

            return SubFlowLauncher(
                shortcutId = shortcutId,
                shortcutName = shortcutName,
                analyticValue = analyticValue,
                factorySupplier = supplier,
            )
        }

        @JvmStatic
        private fun identifyFactorySupplierBy(
            shortcutId: String,
        ): Supplier<SubFlowFactory> = when (shortcutId.trim().lowercase()) {

            BillPaymentSubFlowFactory.SHORTCUT_ID -> Supplier(::BillPaymentSubFlowFactory)

            else -> Supplier(::EmptySubFlowFactory)
        }

    }
}