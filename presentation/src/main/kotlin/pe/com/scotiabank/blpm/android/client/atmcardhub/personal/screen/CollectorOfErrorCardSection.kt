package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import android.content.res.Resources
import android.view.Gravity
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.AdapterFactoryOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.card.CollectorOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiEntityOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfLinearLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.AdapterFactoryOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import java.lang.ref.WeakReference

class CollectorOfErrorCardSection(
    private val weakResources: WeakReference<Resources?>,
    private val reloadType: ReloadType,
    private val receiver: InstanceReceiver,
    private val factory: FactoryOfOneColumnTextEntity,
    private val paddingEntity: UiEntityOfPadding,
): CollectorOfCard {

    private val paddingEntityOfIncluded: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            com.scotiabank.canvascore.R.dimen.canvascore_margin_24,
            com.scotiabank.canvascore.R.dimen.canvascore_margin_24,
            com.scotiabank.canvascore.R.dimen.canvascore_margin_5,
            com.scotiabank.canvascore.R.dimen.canvascore_margin_5
        )
    }

    private val paddingEntityOfTextButton: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_5
        )
    }

    private val paddingOfText: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_10
        )
    }

    override fun collect(): List<UiEntityOfCard<Any>> {

        val recyclerEntity = createRecyclerEntity()

        val cardEntity = UiEntityOfCard<Any>(
            paddingEntity = paddingEntity,
            recyclerEntity = recyclerEntity,
            strokeWidthRes = com.scotiabank.canvascore.R.dimen.canvascore_margin_0
        )

        return listOf(cardEntity)
    }

    private fun createRecyclerEntity(): UiEntityOfRecycler {

        val oneColumnTextEntity: UiEntityOfOneColumnText = createOneColumnTextEntity()
        val oneColumnTextCompound = UiCompound(
            uiEntities = listOf(oneColumnTextEntity),
            factoryOfPortableAdapter = AdapterFactoryOfOneColumnText(),
        )

        val textButtonEntity: UiEntityOfTextButton<ReloadType> = createTextButtonEntity()
        val textButtonCompound = UiCompound(
            uiEntities = listOf(textButtonEntity),
            factoryOfPortableAdapter = AdapterFactoryOfTextButton(),
        )

        val compounds: List<UiCompound<*>> = listOf(oneColumnTextCompound, textButtonCompound)

        val recyclerEntity = UiEntityOfRecycler(
            paddingEntity = paddingEntityOfIncluded,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = FactoryOfLinearLayoutManager(),
        )
        compounds.associateByTo(destination = recyclerEntity.compoundsById, keySelector = ::byId)

        return recyclerEntity
    }

    private fun createOneColumnTextEntity(): UiEntityOfOneColumnText = factory.create(
        paddingEntity = paddingOfText,
        appearance = com.scotiabank.canvascore.R.style.canvascore_style_body2,
        text = weakResources.get()?.getString(R.string.hub_partial_error_text).orEmpty(),
        gravity = Gravity.CENTER_HORIZONTAL,
    )

    private fun createTextButtonEntity(): UiEntityOfTextButton<ReloadType> = UiEntityOfTextButton(
        paddingEntity = paddingEntityOfTextButton,
        isEnabled = true,
        text = weakResources.get()?.getString(R.string.hub_partial_error_button).orEmpty(),
        receiver = receiver,
        drawableStartId = R.drawable.ic_refresh_blue,
        gravity = Gravity.CENTER_HORIZONTAL,
        data = reloadType
    )
}
