package pe.com.scotiabank.blpm.android.client.app

interface TimerFacade {

    fun startPlinLockTimer()

    fun resetPlinLockTimer()

    fun startGlobalTimer()

    fun startInactivityTimer()

    fun cancelTimers()
}
