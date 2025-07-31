package pe.com.scotiabank.blpm.android.ui.list.composite

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound

interface Composite {

    val compounds: List<UiCompound<*>>

    suspend fun recomposeItselfIfNeeded(): List<UiCompound<*>>?

    interface Factory {

        fun create(
            receiver: InstanceReceiver,
            visibilitySupplier: Supplier<Boolean>,
        ): Composite
    }
}
