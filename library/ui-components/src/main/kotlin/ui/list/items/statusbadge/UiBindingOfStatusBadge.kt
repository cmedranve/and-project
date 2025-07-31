package pe.com.scotiabank.blpm.android.ui.list.items.statusbadge

import com.scotiabank.canvascore.views.StatusBadge.Companion.StatusBadgeType

internal inline fun attemptBindStatusBadge(
    entity: UiEntityOfStatusBadge?,
    setterCallback: (statusBadgeType: StatusBadgeType, statusText: String) -> Unit
) {
    if (entity != null && entity.text.isNotBlank()) {
        setterCallback.invoke(entity.type, entity.text)
    }
}
