package pe.com.scotiabank.blpm.android.ui.list.items.inputs.password

import android.content.res.Resources
import androidx.annotation.DimenRes
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.scotiabank.canvascore.inputs.PasswordRequirements
import com.scotiabank.canvascore.inputs.modal.AttrsCanvasPasswordRequirementModal

internal object UiBinderOfRequirement {

    private val INDEX_OF_REQUIREMENT_1: Int
        get() = 0

    private val INDEX_OF_REQUIREMENT_2: Int
        get() = 1

    private val INDEX_OF_REQUIREMENT_3: Int
        get() = 2

    @JvmStatic
    fun <D: Any> bind(entity: UiEntityOfPassword<D>, pRequirements: PasswordRequirements) {

        pRequirements.isVisible = entity.requirementEntities.isNotEmpty()
        bindPaddingTop(entity.paddingTopForRequirement, pRequirements)

        val entities: List<UiEntityOfRequirement> = entity.requirementEntities
        val requirementEntity1: UiEntityOfRequirement? = entities.getOrNull(INDEX_OF_REQUIREMENT_1)
        val requirementEntity2: UiEntityOfRequirement? = entities.getOrNull(INDEX_OF_REQUIREMENT_2)
        val requirementEntity3: UiEntityOfRequirement? = entities.getOrNull(INDEX_OF_REQUIREMENT_3)

        bindAttrs(
            title = entity.requirementTitle,
            entity1 = requirementEntity1,
            entity2 = requirementEntity2,
            entity3 = requirementEntity3,
            pRequirements = pRequirements,
        )

        bindAccessibilityAnnouncements(
            entity1 = requirementEntity1,
            entity2 = requirementEntity2,
            entity3 = requirementEntity3,
            pRequirements = pRequirements,
        )

        bindIconsByStatus(
            isAllDefault = entity.isAllDefault,
            entity1 = requirementEntity1,
            entity2 = requirementEntity2,
            entity3 = requirementEntity3,
            pRequirements = pRequirements,
        )
    }

    @JvmStatic
    private fun bindPaddingTop(@DimenRes paddingTopRes: Int, pRequirements: PasswordRequirements) {

        val res: Resources = pRequirements.resources
        val paddingTopInPixels: Int = res.getDimensionPixelOffset(paddingTopRes)

        if (paddingTopInPixels != pRequirements.paddingTop) {
            pRequirements.updatePadding(top = paddingTopInPixels)
        }
    }

    @JvmStatic
    private fun bindAttrs(
        title: String,
        entity1: UiEntityOfRequirement?,
        entity2: UiEntityOfRequirement?,
        entity3: UiEntityOfRequirement?,
        pRequirements: PasswordRequirements,
    ) {
        val attrs = AttrsCanvasPasswordRequirementModal(
            title = title,
            requirementOne = entity1?.text.orEmpty(),
            requirementTwo = entity2?.text.orEmpty(),
            requirementThree = entity3?.text.orEmpty(),
        )
        pRequirements.setupPasswordRequirements(attrs)
    }

    @JvmStatic
    private fun bindAccessibilityAnnouncements(
        entity1: UiEntityOfRequirement?,
        entity2: UiEntityOfRequirement?,
        entity3: UiEntityOfRequirement?,
        pRequirements: PasswordRequirements,
    ) {
        pRequirements.setRequirementOneAccessibilityAnnouncement(
            accessibilityAnnouncement = entity1?.contentDescription.orEmpty(),
        )
        pRequirements.setRequirementTwoAccessibilityAnnouncement(
            accessibilityAnnouncement = entity2?.contentDescription.orEmpty(),
        )
        pRequirements.setRequirementTwoAccessibilityAnnouncement(
            accessibilityAnnouncement = entity3?.contentDescription.orEmpty(),
        )
    }

    @JvmStatic
    private fun bindIconsByStatus(
        isAllDefault: Boolean,
        entity1: UiEntityOfRequirement?,
        entity2: UiEntityOfRequirement?,
        entity3: UiEntityOfRequirement?,
        pRequirements: PasswordRequirements,
    ) {
        if (isAllDefault) {
            pRequirements.resetPasswordRequirements()
            return
        }
        pRequirements.setPasswordRequirementOneSatisfied(
            isSatisfied = entity1?.isSatisfied ?: false,
        )
        pRequirements.setPasswordRequirementTwoSatisfied(
            isSatisfied = entity2?.isSatisfied ?: false,
        )
        pRequirements.setPasswordRequirementThreeSatisfied(
            isSatisfied = entity3?.isSatisfied ?: false,
        )
    }
}
