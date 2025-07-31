package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.carddatabottomsheet

interface AlertBannerService {

    fun addAlertBanner(alertBannerInfo: AlertBannerInfo)

    fun findAlertBannerInfoBy(id: Long): AlertBannerInfo?
}
