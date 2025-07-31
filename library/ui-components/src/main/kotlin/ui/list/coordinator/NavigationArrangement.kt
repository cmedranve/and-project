package pe.com.scotiabank.blpm.android.ui.list.coordinator

import androidx.annotation.TransitionRes
import androidx.core.content.res.ResourcesCompat
import pe.com.scotiabank.blpm.android.ui.R

enum class NavigationArrangement(@TransitionRes val transitionResId: Int) {

    ADD_SCREEN(transitionResId = ResourcesCompat.ID_NULL),
    REMOVE_SCREEN(transitionResId = ResourcesCompat.ID_NULL),
    ADD_POP_UP(transitionResId = ResourcesCompat.ID_NULL),
    REMOVE_POP_UP(transitionResId = ResourcesCompat.ID_NULL),
}
