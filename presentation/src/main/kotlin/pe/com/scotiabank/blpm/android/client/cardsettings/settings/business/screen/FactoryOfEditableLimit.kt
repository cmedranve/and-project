package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import android.content.res.Resources
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen.CardSettings
import pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen.Limit
import pe.com.scotiabank.blpm.android.client.base.number.DoubleParser
import java.lang.ref.WeakReference

class FactoryOfEditableLimit(
    private val doubleParser: DoubleParser,
    private val weakResources: WeakReference<Resources?>,
    private val holderOfCardSettings: MutableHolderOfCardSettings,
) {

    private val cardSettings: CardSettings?
        get() = holderOfCardSettings.settingsReceived

    private val limits: List<Limit>
        get() = cardSettings?.limitList ?: emptyList()

    private val isEnabled: Boolean
        get() = cardSettings?.isTempLock == false

    fun attemptToEditableLimit(
        info: CardLimitInfo,
    ): EditableLimit? {

        val limit: Limit = limits.getOrNull(info.positionFromNetworkCall) ?: return null

        val editableLimit = EditableLimit(
            id = randomLong(),
            info = info,
            amountFromNetworkCall = doubleParser.parse(limit.amountConfig),
            maxAmount = doubleParser.parse(limit.amountMax),
            weakResources = weakResources,
        )
        editableLimit.setIsEnabled(isEnabled)

        return editableLimit
    }
}