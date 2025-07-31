package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.carddatabottomsheet

import kotlin.time.Duration

interface TimerBuddyTipService {

    fun addTimerBuddyTip(timerInfo: TimerInfo , duration: Duration? = null)
}
