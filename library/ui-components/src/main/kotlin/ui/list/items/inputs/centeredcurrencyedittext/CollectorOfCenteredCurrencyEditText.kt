package pe.com.scotiabank.blpm.android.ui.list.items.inputs.centeredcurrencyedittext

interface CollectorOfCenteredCurrencyEditText<D: Any> {

    fun collect(): List<UiEntityOfCenteredCurrencyEditText<D>>
}
