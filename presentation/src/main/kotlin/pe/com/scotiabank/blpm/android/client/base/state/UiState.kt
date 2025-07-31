package pe.com.scotiabank.blpm.android.client.base.state

enum class UiState {

    BLANK,
    DISABLED,
    LOADING,
    ERROR,
    EMPTY,
    SUCCESS;

    companion object {

        @JvmStatic
        fun from(quantity: Int): UiState = if (quantity == 0) EMPTY else SUCCESS
    }
}
