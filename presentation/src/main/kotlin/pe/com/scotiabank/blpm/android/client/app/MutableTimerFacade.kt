package pe.com.scotiabank.blpm.android.client.app

import android.os.Handler
import android.os.Looper

class MutableTimerFacade(
    private val expireSession: Runnable,
    private val finishPlinLockTime: Runnable,
    private val plinLockHandler: Handler = Handler(Looper.getMainLooper()),
    private val globalHandler: Handler = Handler(Looper.getMainLooper()),
    private val inactivityHandler: Handler = Handler(Looper.getMainLooper())
) : TimerFacade {

    companion object {

        private const val TIMEOUT_GLOBAL: Long = 900_000L
        private const val TIMEOUT_INACTIVITY: Long = 300_000L
        private const val TIMEOUT_PLIN_LOCK: Long = 120_000L
    }

    override fun startPlinLockTimer() {
        plinLockHandler.removeCallbacks(finishPlinLockTime)
        plinLockHandler.postDelayed(finishPlinLockTime, TIMEOUT_PLIN_LOCK)
    }

    override fun resetPlinLockTimer() {
        plinLockHandler.removeCallbacks(finishPlinLockTime)
    }

    override fun startGlobalTimer() {
        globalHandler.removeCallbacks(expireSession)
        globalHandler.postDelayed(expireSession, TIMEOUT_GLOBAL)
    }

    override fun startInactivityTimer() {
        inactivityHandler.removeCallbacks(expireSession)
        inactivityHandler.postDelayed(expireSession, TIMEOUT_INACTIVITY)
    }

    override fun cancelTimers() {
        plinLockHandler.removeCallbacks(finishPlinLockTime)
        inactivityHandler.removeCallbacks(expireSession)
        globalHandler.removeCallbacks(expireSession)
    }

    class Builder {

        fun build(
            expireSession: Runnable,
            finishPlinLockTime: Runnable,
        ): MutableTimerFacade = MutableTimerFacade(expireSession, finishPlinLockTime)
    }
}
