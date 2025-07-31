package pe.com.scotiabank.blpm.android.client.host.session.subflow

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.analytics.factories.qrpayment.QRAnalyticsConstant
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.model.qr.QRCardModel
import pe.com.scotiabank.blpm.android.client.model.qr.QRVerifiedEstablishmentModel
import pe.com.scotiabank.blpm.android.client.newqrpayment.enabled.EnabledQrPaymentCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.newqrpayment.shared.QrInfoCarrier
import pe.com.scotiabank.blpm.android.client.qrpayment.QRPaymentMapper
import pe.com.scotiabank.blpm.android.client.qrpayment.seed.QRSeed
import pe.com.scotiabank.blpm.android.client.qrpayment.seed.QRSeedFactory
import pe.com.scotiabank.blpm.android.client.util.qrcamera.QRCameraConstant
import pe.com.scotiabank.blpm.android.data.entity.qr.QRSeedEntity
import pe.com.scotiabank.blpm.android.data.net.NewRestQrApiService
import pe.com.scotiabank.blpm.android.data.repository.qr.NewQrRepository
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class QrPaymentSubFlowFactory: SubFlowFactory {

    override fun create(
        hub: Hub,
        parentScope: CoroutineScope,
        weakParent: WeakReference<out Coordinator?>,
    ): Coordinator {

        val fakeQrSeedEntity: QRSeedEntity = QRSeedFactory.createForFullProfile()
        val fakeQrSeed: QRSeed = QRPaymentMapper.transformSeed(fakeQrSeedEntity)
        hub.appModel.qrSeed = fakeQrSeed

        val qrCardModels: List<QRCardModel> = emptyList()
        val establishment = QRVerifiedEstablishmentModel()

        val qrInfoCarrier = QrInfoCarrier(
            establishmentType = QRCameraConstant.P2N,
            previousSection = QRAnalyticsConstant.MY_ACCOUNTS,
            establishment = establishment,
            qrCardModels = qrCardModels,
        )

        val retrofit: Retrofit = hub.appModel.sessionRetrofit
        val factory = EnabledQrPaymentCoordinatorFactory(
            hub = hub,
            newQrRepository = createNewQrRepository(retrofit, hub.objectMapper),
            parentScope = parentScope,
            qrInfoCarrier = qrInfoCarrier,
            weakParent = weakParent,
        )
        return factory.create()
    }

    private fun createNewQrRepository(
        retrofit: Retrofit,
        objectMapper: ObjectMapper,
    ): NewQrRepository {

        val api: NewRestQrApiService = retrofit.create(NewRestQrApiService::class.java)
        return NewQrRepository(api = api, objectMapper = objectMapper)
    }

    companion object {

        val SHORTCUT_ID: String
            @JvmStatic
            get() = "qr_payment_shortcut_id"

        val SHORTCUT_NAME: String
            @JvmStatic
            get() = "Pagar con QR"
    }
}