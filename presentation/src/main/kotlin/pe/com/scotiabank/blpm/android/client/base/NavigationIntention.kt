package pe.com.scotiabank.blpm.android.client.base

enum class NavigationIntention {

    BACK,
    CLOSE,
    GO_HOME;

    companion object {

        @JvmStatic
        fun filterInBack(intention: NavigationIntention): Boolean = BACK == intention

        @JvmStatic
        fun filterInClose(intention: NavigationIntention): Boolean = CLOSE == intention

        @JvmStatic
        fun filterInGoHome(intention: NavigationIntention): Boolean = GO_HOME == intention
    }
}
