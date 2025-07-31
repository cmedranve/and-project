package pe.com.scotiabank.blpm.android.client.base.permission

enum class PermissionResult(
    val value: Boolean,
    val analyticValue: String,
) {

    GRANTED(
        value = true,
        analyticValue = "permitir",
    ),

    NOT_GRANTED(
        value = false,
        analyticValue = "rechazar",
    );

    companion object {

        @JvmStatic
        fun identifyBy(value: Boolean): PermissionResult = if (value) GRANTED else NOT_GRANTED
    }
}
