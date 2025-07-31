package pe.com.scotiabank.blpm.android.client.base.checksecurity

import android.content.Context
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.base.checksecurity.CheckSecurityFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsUtil
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.shield.ShieldText
import java.lang.ref.WeakReference

class CheckSecurityModel(
    private val weakAppContext: WeakReference<out Context?>,
    dispatcherProvider: DispatcherProvider,
    private val checker: Checker,
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticsFactory: CheckSecurityFactory,
): DispatcherProvider by dispatcherProvider, RootChecking {

    suspend fun checkIntegrity(): Any = withContext(defaultDispatcher) {

        return@withContext when {
            checker.isHooked() -> onHookingDetected()
            checker.isTampered() -> onTamperingDetected()
            checker.isEmulator() -> onEmulatorDetected()
            else -> TrustworthyIntegrity
        }
    }

    private fun onHookingDetected(): ImmediateBlockingEntity {
        val title: String = weakAppContext.get()
            ?.let(ShieldText::getTitleOnHooking)
            .orEmpty()

        val factory = FactoryOfAnalyticsEvent(analyticsFactory::createHookingEvent)
        sendEvent(factory, title)

        return ImmediateBlockingEntity
    }

    private fun onTamperingDetected(): BlockingEntity {
        val title: String = weakAppContext.get()
            ?.let(ShieldText::getTitleOnTampering)
            .orEmpty()
        val description: String = weakAppContext.get()
            ?.let(ShieldText::getDescriptionOnTampering)
            .orEmpty()

        val factory = FactoryOfAnalyticsEvent(analyticsFactory::createTamperingEvent)
        sendEvent(factory, title)

        return BlockingEntity(title, description)
    }

    private fun onEmulatorDetected(): BlockingEntity {
        val title: String = weakAppContext.get()
            ?.let(ShieldText::getTitleOnEmulator)
            .orEmpty()
        val description: String = weakAppContext.get()
            ?.let(ShieldText::getDescriptionOnEmulator)
            .orEmpty()

        val factory = FactoryOfAnalyticsEvent(analyticsFactory::createEmulatorEvent)
        sendEvent(factory, title)

        return BlockingEntity(title, description)
    }

    override fun checkRooting(): SealedNonBlockingEntity {
        return if (checker.isRootedDevice()) onRootedDeviceDetected() else EmptyNonBlockingEntity
    }

    private fun onRootedDeviceDetected(): NonBlockingEntity {
        val title: String = weakAppContext.get()
            ?.let(ShieldText::getTitleOnRootedDevice)
            .orEmpty()
        val description: String = weakAppContext.get()
            ?.let(ShieldText::getDescriptionOnRootedDevice)
            .orEmpty()
        val buttonText: String = weakAppContext.get()
            ?.let(ShieldText::getButtonTextOnRootedDevice)
            .orEmpty()

        val factory = FactoryOfAnalyticsEvent(analyticsFactory::createRootingEvent)
        sendEvent(factory, title)

        return NonBlockingEntity(title, description, buttonText)
    }

    private fun sendEvent(factory: FactoryOfAnalyticsEvent, message: String) {
        val eventLabel: String = AnalyticsUtil.normalizeText(message)
        val event: AnalyticsEvent = factory.createAnalyticsEvent(eventLabel)
        analyticsDataGateway.sendEventV2(event)
    }
}
