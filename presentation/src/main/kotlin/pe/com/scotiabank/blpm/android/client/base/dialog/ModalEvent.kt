package pe.com.scotiabank.blpm.android.client.base.dialog

enum class ModalEvent {
    PRIMARY_CLICKED,
    SECONDARY_CLICKED;

    companion object {

        @JvmStatic
        fun filterInPrimaryClicked(
            event: ModalEvent,
        ): Boolean = PRIMARY_CLICKED == event

        @JvmStatic
        fun filterInSecondaryClicked(
            event: ModalEvent
        ): Boolean = SECONDARY_CLICKED == event
    }
}
