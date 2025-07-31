package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import android.content.res.Resources
import android.view.Gravity
import android.widget.ImageView.ScaleType
import com.scotiabank.canvascore.buttons.PillButton.Companion.PillButtonType
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.pillbutton.AdapterFactoryOfPillButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.pillbutton.UiEntityOfPillButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.AdapterFactoryOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.card.CollectorOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiEntityOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.image.UiEntityOfImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.AdapterFactoryOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.UiEntityOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfLinearLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.AdapterFactoryOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import java.lang.ref.WeakReference

class CollectorOfTravelCard(
    private val weakResources: WeakReference<Resources?>,
    private val factory: FactoryOfOneColumnTextEntity,
    private val paddingEntity: UiEntityOfPadding,
    private val receiver: InstanceReceiver,
) : CollectorOfCard {

    private val paddingEntityOfWholeContent: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = com.scotiabank.canvascore.R.dimen.canvascore_margin_24,
            right = com.scotiabank.canvascore.R.dimen.canvascore_margin_24,
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_10,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_10,
        )
    }

    private val paddingEntityOfItem: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_10,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_10,
        )
    }

    private val imageEntity: UiEntityOfImage by lazy {
        UiEntityOfImage(scaleType = ScaleType.FIT_START)
    }

    override fun collect(): List<UiEntityOfCard<Any>> {
        val recyclerEntity: UiEntityOfRecycler = createRecyclerEntity()
        val entity: UiEntityOfCard<Any> = UiEntityOfCard(
            paddingEntity = paddingEntity,
            recyclerEntity = recyclerEntity,
            useCompatPadding = false,
        )
        return listOf(entity)
    }

    private fun createRecyclerEntity(): UiEntityOfRecycler {

        val oneColumnImageCompound = UiCompound(
            uiEntities = createOneColumnImageEntities(),
            factoryOfPortableAdapter = AdapterFactoryOfOneColumnImage(),
        )

        val oneColumnTextCompound = UiCompound(
            uiEntities = createOneColumnTextEntities(),
            factoryOfPortableAdapter = AdapterFactoryOfOneColumnText(),
        )

        val pillButtonEntity: UiEntityOfPillButton<Any> = createPillButtonEntity()
        val pillButtonCompound = UiCompound(
            uiEntities = listOf(pillButtonEntity),
            factoryOfPortableAdapter = AdapterFactoryOfPillButton(),
        )

        val textButtonEntity: UiEntityOfTextButton<Any> = createTextButtonEntity()
        val textButtonCompound = UiCompound(
            uiEntities = listOf(textButtonEntity),
            factoryOfPortableAdapter = AdapterFactoryOfTextButton(),
        )

        val compounds: List<UiCompound<*>> = listOf(
            oneColumnImageCompound,
            oneColumnTextCompound,
            pillButtonCompound,
            textButtonCompound,
        )

        val recyclerEntity = UiEntityOfRecycler(
            paddingEntity = paddingEntityOfWholeContent,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfLinearLayoutManager(),
        )
        compounds.associateByTo(destination = recyclerEntity.compoundsById, keySelector = ::byId)

        return recyclerEntity
    }

    private fun createOneColumnImageEntities(): List<UiEntityOfOneColumnImage> {
        val entity = UiEntityOfOneColumnImage(
            paddingEntity = paddingEntityOfItem,
            entityOfColumn = imageEntity,
            drawableRes = com.scotiabank.icons.illustrative.R.drawable.ic_travel_outlined_multicoloured_48,
        )
        return listOf(entity)
    }

    private fun createOneColumnTextEntities(): List<UiEntityOfOneColumnText> {
        val entityForLabel: UiEntityOfOneColumnText = factory.create(
            paddingEntity = paddingEntityOfItem,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_subtitle2,
            text = weakResources.get()?.getString(R.string.card_settings_warning_travel).orEmpty(),
        )
        val entityForValue: UiEntityOfOneColumnText = factory.create(
            paddingEntity = paddingEntityOfItem,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_introduction_black,
            text = weakResources.get()?.getString(R.string.card_settings_manage_your_travels).orEmpty(),
        )
        return listOf(entityForLabel, entityForValue)
    }

    private fun createPillButtonEntity(): UiEntityOfPillButton<Any> = UiEntityOfPillButton(
        paddingEntity = paddingEntityOfItem,
        isEnabled = true,
        text = weakResources.get()?.getString(CardSettingAction.REGISTER_TRAVEL.labelRes).orEmpty(),
        receiver = receiver,
        data = CardSettingAction.REGISTER_TRAVEL,
        type = PillButtonType.TYPE_NOVA_BLUE,
    )

    private fun createTextButtonEntity(): UiEntityOfTextButton<Any> = UiEntityOfTextButton(
        paddingEntity = paddingEntityOfItem,
        isEnabled = true,
        text = weakResources.get()?.getString(CardSettingAction.WHY_DO_I_HAVE_TO_REGISTER_TRAVEL.labelRes).orEmpty(),
        receiver = receiver,
        data = CardSettingAction.WHY_DO_I_HAVE_TO_REGISTER_TRAVEL,
        gravity = Gravity.CENTER,
    )
}
