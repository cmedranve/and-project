package pe.com.scotiabank.blpm.android.ui.list.items.badge

import com.google.android.material.badge.BadgeDrawable

object UiBinderOfBadge {

    @JvmStatic
    fun bindOrClear(entity: UiEntityOfBadge, badgeDrawable: BadgeDrawable) {
        if (entity.isVisible) bind(entity, badgeDrawable) else clear(badgeDrawable)
        badgeDrawable.isVisible = entity.isVisible
    }

    @JvmStatic
    private fun bind(entity: UiEntityOfBadge, badgeDrawable: BadgeDrawable) {
        entity.label?.let(badgeDrawable::setText) ?: badgeDrawable.clearText()
        entity.number?.let(badgeDrawable::setNumber) ?: badgeDrawable.clearNumber()
        badgeDrawable.maxNumber = entity.maxNumber
    }

    @JvmStatic
    private fun clear(badgeDrawable: BadgeDrawable) {
        badgeDrawable.clearText()
        badgeDrawable.clearNumber()
    }
}
