package pe.com.scotiabank.blpm.android.client.assistance.model

class ContextualAssistance(var screens: List<Screen>) {

    /**
     * Returns Assitance info for a specific screen
     * @param screenTag defined tag in the screen
     * @return a list of {@AssistanceItem.class}, empty if no assistance found
     */
    fun getScreenByTag(screenTag: String): List<AssistanceItem> {
        for (screenItem in screens) {
            if (screenTag.equals(screenItem.screenTag, ignoreCase = true)) {
                return screenItem.items
            }
        }
        return emptyList()
    }
}
