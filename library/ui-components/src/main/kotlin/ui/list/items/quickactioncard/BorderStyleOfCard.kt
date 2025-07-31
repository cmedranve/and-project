package pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard

import com.scotiabank.canvascore.cards.CanvasCardView

enum class BorderStyleOfCard {

    FLOAT {
        override fun setUp(cardView: CanvasCardView) = cardView.setUpCardFloat()
    },
    DASHED {
        override fun setUp(cardView: CanvasCardView) = cardView.setUpCardFlatDashed()
    };

    abstract fun setUp(cardView: CanvasCardView)
}
