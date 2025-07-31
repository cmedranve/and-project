package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import android.content.Context
import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.errorstate.CompositeOfErrorState
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.base.onecolumn.TextCollectorForDisabledOrErrorState
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardOwnerType
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.ui.list.composite.Composite
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.card.ComposerOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.ComposerOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.SingleCollectorOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.loading.ComposerOfLoading
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.ComposerOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.ComposerOfOneColumnText
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class MainTopComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    uiStateHolder: UiStateHolder,
    private val composerOfSkeleton: ComposerOfSkeleton,
    val compositeOfErrorState: CompositeOfErrorState,
    private val groupCompositesByOwnerType: Map<Long, GroupComposite>,
    private val cardComposerOfTravelCard: ComposerOfCard,
) : Composite,
    DispatcherProvider by dispatcherProvider,
    UiStateHolder by uiStateHolder,
    AtmCardGroupService
{

    private val compoundsByKey: MutableMap<Int, List<UiCompound<*>>?> = ConcurrentHashMap()

    override val compounds: List<UiCompound<*>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompound<*>> {

        val mutableCompounds: MutableList<UiCompound<*>> = mutableListOf()

        val skeletonCompound = composerOfSkeleton.composeUiData(
            visibilitySupplier = Supplier(::isLoadingVisible),
        )
        mutableCompounds.add(skeletonCompound)

        val compoundsOfErrorState: List<UiCompound<*>> = compositeOfErrorState.compose()
        mutableCompounds.addAll(compoundsOfErrorState)

        groupCompositesByOwnerType
            .values
            .forEach { groupComposite -> mutableCompounds.addAll(elements = groupComposite.composeItself()) }

        val cardCompoundOfTravelCard = cardComposerOfTravelCard.composeUiData(
            visibilitySupplier = Supplier(::isSuccessVisible),
        )
        mutableCompounds.add(cardCompoundOfTravelCard)

        return mutableCompounds
    }

    override fun addAtmCardGroup(group: AtmCardGroup) {
        val groupComposite: GroupComposite = groupCompositesByOwnerType[group.ownerType.id] ?: return
        groupComposite.addAtmCardGroup(group)
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val uiStateHolder: UiStateHolder,
        private val appModel: AppModel,
        private val weakResources: WeakReference<Resources?>,
        private val weakAppContext: WeakReference<Context?>,
        private val factory: FactoryOfOneColumnTextEntity,
        private val ownerTypes: List<AtmCardOwnerType>,
    ) {

        private val horizontalPaddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
                right = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
            )
        }

        private val paddingEntity: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
            )
        }

        private val imagePaddingEntityForErrorState: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        private val textPaddingEntityForErrorState: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        private val paddingEntityForErrorLoading: UiEntityOfPadding by lazy {
            UiEntityOfPadding(
                top = com.scotiabank.canvascore.R.dimen.canvascore_margin_36,
                bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_36,
                left = horizontalPaddingEntity.left,
                right = horizontalPaddingEntity.right,
            )
        }

        fun create(receiver: InstanceReceiver): MainTopComposite = MainTopComposite(
            dispatcherProvider = dispatcherProvider,
            uiStateHolder = uiStateHolder,
            composerOfSkeleton = createComposerOfSkeleton(),
            compositeOfErrorState = createCompositeForErrorState(receiver),
            groupCompositesByOwnerType = createGroupCompositesByOwnerType(receiver),
            cardComposerOfTravelCard = createCardComposerOfTravelCard(receiver),
        )

        private fun createComposerOfSkeleton(): ComposerOfSkeleton {
            val collector = SkeletonCollectorForLoading(paddingEntity)
            return ComposerOfSkeleton(collector)
        }

        private fun createCompositeForErrorState(
            receiver: InstanceReceiver,
        ): CompositeOfErrorState {

            val imageCollector = SingleCollectorOfOneColumnImage(R.drawable.ic_error_list)

            val textCollector = TextCollectorForDisabledOrErrorState(
                paddingEntity = textPaddingEntityForErrorState,
                weakResources = weakResources,
                titleRes = R.string.eraser_error_list_title,
                descriptionRes = R.string.eraser_error_list_description,
            )

            val loadingComposer = ComposerOfLoading(paddingEntityForErrorLoading)
            loadingComposer.add(id = randomLong())

            return CompositeOfErrorState(
                imagePaddingEntity = imagePaddingEntityForErrorState,
                imageComposer = ComposerOfOneColumnImage(imageCollector, receiver),
                textComposer = ComposerOfOneColumnText(textCollector),
                loadingComposer = loadingComposer,
                visibilitySupplier = Supplier(uiStateHolder::isErrorVisible),
            )
        }

        private fun createGroupCompositesByOwnerType(
            receiver: InstanceReceiver,
        ): Map<Long, GroupComposite> = ownerTypes.associateBy(
            keySelector = ::byId,
            valueTransform = { ownerType -> createGroupComposite(receiver) },
        )

        private fun byId(ownerType: AtmCardOwnerType): Long = ownerType.id

        private fun createGroupComposite(receiver: InstanceReceiver): GroupComposite {
            val composerOfLabel = createComposerOfLabel()
            val cardComposerOfAtmCard: CardComposerOfAtmCard = createCardComposerOfAtmCard(receiver)
            return GroupComposite(uiStateHolder, composerOfLabel, cardComposerOfAtmCard)
        }

        private fun createComposerOfLabel(): ComposerOfLabel {
            val converter = ConverterOfLabel(weakResources, factory, horizontalPaddingEntity)
            return ComposerOfLabel(converter)
        }

        private fun createCardComposerOfAtmCard(receiver: InstanceReceiver): CardComposerOfAtmCard {
            val textConverterForActive = TextConverterOfAtmCard(
                weakResources = weakResources,
                factory = factory,
                cardNameAppearance = com.scotiabank.canvascore.R.style.canvascore_style_subtitle2,
                cardNumberAppearance = com.scotiabank.canvascore.R.style.canvascore_style_character_count,
                bottomPaddingOfCardNumber = com.scotiabank.canvascore.R.dimen.canvascore_margin_22,
            )
            val textConverterForLocked = TextConverterOfAtmCard(
                weakResources = weakResources,
                factory = factory,
                cardNameAppearance = com.scotiabank.canvascore.R.style.canvascore_style_text_button_disabled,
                cardNumberAppearance = com.scotiabank.canvascore.R.style.canvascore_style_caption_alternate,
                bottomPaddingOfCardNumber = com.scotiabank.canvascore.R.dimen.canvascore_margin_8,
            )
            val converter = CardConverterOfAtmCard(
                typefaceProvider = appModel,
                weakResources = weakResources,
                weakAppContext = weakAppContext,
                paddingEntity = paddingEntity,
                textConverterForActive = textConverterForActive,
                textConverterForLocked = textConverterForLocked,
                converterOfCardInfo = ConverterOfCardInfo(receiver),
                receiver = receiver,
            )
            return CardComposerOfAtmCard(converter)
        }

        private fun createCardComposerOfTravelCard(receiver: InstanceReceiver): ComposerOfCard {
            val converter = CollectorOfTravelCard(
                weakResources = weakResources,
                factory = factory,
                paddingEntity = paddingEntity,
                receiver = receiver,
            )
            return ComposerOfCard(converter)
        }
    }

    companion object {

        private val SINGLE_KEY: Int
            get() = 0
    }
}
