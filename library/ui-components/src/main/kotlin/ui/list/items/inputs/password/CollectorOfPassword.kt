package pe.com.scotiabank.blpm.android.ui.list.items.inputs.password

interface CollectorOfPassword<D: Any> {

    fun collect(): List<UiEntityOfPassword<D>>
}
