package pe.com.scotiabank.blpm.android.ui.util

class StateOfKeyboardVisibility(val isOldVisible: Boolean, val isNewVisible: Boolean) {

    val isBecomingVisible: Boolean
        get() = isOldVisible.not() && isNewVisible
    val isBecomingHidden: Boolean
        get() = isOldVisible && isNewVisible.not()
}
