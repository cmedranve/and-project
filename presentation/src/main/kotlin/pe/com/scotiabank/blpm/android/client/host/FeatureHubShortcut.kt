package pe.com.scotiabank.blpm.android.client.host

class FeatureHubShortcut(val isDisplayable: Boolean) {

    companion object {

        @JvmStatic
        val ID: String
            get() = "feature_hub_shortcut_id"

        @JvmStatic
        val DISPLAYABLE_FLAG: String
            get() = "feature_hub_displayable_flag"
    }
}