package pe.com.scotiabank.blpm.android.ui.list.items.card

interface CollectorOfCard {

    fun collect(): List<UiEntityOfCard<Any>>
}
