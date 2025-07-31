package pe.com.scotiabank.blpm.android.client.host.osversioncheck

import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.analytics.EmptyAnalyticConsumer
import pe.com.scotiabank.blpm.android.client.base.calltoaction.BottomCompositeForCallToAction
import pe.com.scotiabank.blpm.android.client.base.calltoaction.CallToAction
import pe.com.scotiabank.blpm.android.client.base.calltoaction.CoordinatorForCallToAction
import pe.com.scotiabank.blpm.android.client.base.calltoaction.TopCompositeForCallToAction
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import java.lang.ref.WeakReference

class VersionCheckCoordinatorFactory(
    private val hub: Hub,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    fun create(): CoordinatorForCallToAction {

        val uiStateHolder = DelegateUiStateHolder()
        val textProvider = TextProvider(hub.weakResources)
        val callToActions: List<CallToAction> = listOf(CallToAction.UNDERSTOOD_PRIMARY)

        return CoordinatorForCallToAction(
            factoryOfToolbarComposite = AppBarComposite.Factory(hub.dispatcherProvider),
            factoryOfMainTopComposite = createFactoryOfMainTopComposite(uiStateHolder, textProvider),
            factoryOfMainBottomComposite = createFactoryOfMainBottomComposite(callToActions),
            availabilityRegistry = createAvailabilityRegistry(callToActions),
            analyticConsumer = EmptyAnalyticConsumer,
            embeddedDataName = String.EMPTY,
            analyticAdditionalData = Unit,
            isToolbarEnabled = false,
            toolbarIconRes = ResourcesCompat.ID_NULL,
            titleText = String.EMPTY,
            weakParent = weakParent,
            scope = parentScope.newChildScope(),
            dispatcherProvider = hub.dispatcherProvider,
            mutableLiveHolder = hub.mutableLiveHolder,
            userInterface = hub.userInterface,
            uiStateHolder = uiStateHolder,
        )
    }

    private fun createFactoryOfMainTopComposite(
        uiStateHolder: DelegateUiStateHolder,
        textProvider: TextProvider,
    ) = TopCompositeForCallToAction.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        weakResources = hub.weakResources,
        factory = hub.factoryOfOneColumnTextEntity,
        imageRes = R.drawable.os_check_version,
        titleRes = R.string.os_updater_title,
        description = textProvider.descriptionForOsUpdate,
        uiStateHolder = uiStateHolder,
    )

    private fun createFactoryOfMainBottomComposite(
        callToActions: List<CallToAction>,
    ) = BottomCompositeForCallToAction.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        weakResources = hub.weakResources,
        callToActions = callToActions,
    )

    private fun createAvailabilityRegistry(callToActions: List<CallToAction>): AvailabilityRegistry {
        val ids: Collection<Long> = callToActions.map(::fromCallToActionToId)
        return AvailabilityRegistry(ids)
    }

    private fun fromCallToActionToId(callToAction: CallToAction): Long = callToAction.id
}