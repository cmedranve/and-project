package pe.com.scotiabank.blpm.android.client.base

enum class SearchIntention {

    SHOW,
    HIDE;

    companion object {

        @JvmStatic
        fun filterInShow(intention: SearchIntention): Boolean = SHOW == intention

        @JvmStatic
        fun filterInHide(intention: SearchIntention): Boolean = HIDE == intention
    }
}
