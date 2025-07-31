package pe.com.scotiabank.blpm.android.ui.util

enum class KeyboardIntention {

    HIDE,
    SHOW;

    companion object {

        @JvmStatic
        fun filterInHideKeyboard(intention: KeyboardIntention): Boolean = HIDE == intention

        @JvmStatic
        fun filterInShowKeyboard(intention: KeyboardIntention): Boolean = SHOW == intention
    }
}
