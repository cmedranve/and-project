package pe.com.scotiabank.blpm.android.ui.list.items.inputs.currencyedittext

interface CollectorOfCurrencyEditText<D: Any> {

    fun collect(): List<UiEntityOfCurrencyEditText<D>>
}
