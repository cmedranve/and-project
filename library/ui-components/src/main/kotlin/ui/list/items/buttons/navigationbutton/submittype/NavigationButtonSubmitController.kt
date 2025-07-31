package pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.submittype

interface NavigationButtonSubmitController {

    fun editNavigationButtonSubmitEnabling(
        id: Long,
        isEnabled: Boolean,
    )

    fun editNavigationButtonSubmitText(
        id: Long,
        text: CharSequence,
    )

    fun removeNavigationButtonSubmit(id: Long)
}
