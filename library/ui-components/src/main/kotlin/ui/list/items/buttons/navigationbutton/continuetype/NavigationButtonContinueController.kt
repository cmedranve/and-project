package pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.continuetype

interface NavigationButtonContinueController {

    fun editNavigationButtonContinueEnabling(
        id: Long,
        isEnabled: Boolean,
    )

    fun editNavigationButtonContinueText(
        id: Long,
        text: CharSequence,
    )

    fun removeNavigationButtonContinue(id: Long)
}
