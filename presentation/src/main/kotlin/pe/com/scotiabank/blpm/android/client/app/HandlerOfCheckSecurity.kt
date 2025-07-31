package pe.com.scotiabank.blpm.android.client.app

import android.content.Context
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import kotlinx.coroutines.launch
import pe.com.scotiabank.blpm.android.client.base.checksecurity.*
import pe.com.scotiabank.blpm.android.client.base.coroutine.ProviderForCoroutine
import pe.com.scotiabank.blpm.android.client.shield.ShieldGateway
import pe.com.scotiabank.blpm.android.client.shield.wipeMemoryByFinishingProcess
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import java.lang.ref.WeakReference

class HandlerOfCheckSecurity(
    private val weakAppContext: WeakReference<out Context?>,
    providerForCoroutine: ProviderForCoroutine,
    private val checkSecurityModel: CheckSecurityModel,
) : ProviderForCoroutine by providerForCoroutine, RootChecking by checkSecurityModel {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            ImmediateBlockingEntity::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleImmediateBlocking)
        )
        .add(
            BlockingEntity::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleBlocking)
        )
        .build()
    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    fun checkIntegrity() = appScope.launch {
        val result: Any = checkSecurityModel.checkIntegrity()
        selfReceiver.receive(result)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleImmediateBlocking(entity: ImmediateBlockingEntity) {
        wipeMemoryByFinishingProcess()
    }

    private fun handleBlocking(entity: BlockingEntity) {
        val appContext: Context = weakAppContext.get() ?: return
        ShieldGateway.navigateToShieldByFinishingAllTasks(appContext, entity)
    }
}
