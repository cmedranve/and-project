package pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.backtype

interface NavigationButtonBackController {

    fun editNavigationButtonBackEnabling(
        id: Long,
        isEnabled: Boolean,
    )

    fun editNavigationButtonBackText(
        id: Long,
        text: CharSequence,
    )

    fun removeNavigationButtonBack(id: Long)
}
