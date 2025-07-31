package pe.com.scotiabank.blpm.android.client.base.module

import dagger.Module
import dagger.Provides
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.ApplicationScope
import pe.com.scotiabank.blpm.android.data.net.NotificationApiService
import pe.com.scotiabank.blpm.android.data.net.RestApiService
import pe.com.scotiabank.blpm.android.data.net.RestAppraisalApiService
import pe.com.scotiabank.blpm.android.data.net.RestBusinessDashboardApiService
import pe.com.scotiabank.blpm.android.data.net.RestConsentApiService
import pe.com.scotiabank.blpm.android.data.net.RestCreditCardApiService
import pe.com.scotiabank.blpm.android.data.net.RestCreditCardPrepaymentApiService
import pe.com.scotiabank.blpm.android.data.net.RestDebitCardApiService
import pe.com.scotiabank.blpm.android.data.net.RestEraserApiService
import pe.com.scotiabank.blpm.android.data.net.RestGateBffApiService
import pe.com.scotiabank.blpm.android.data.net.RestGooglePayApiService
import pe.com.scotiabank.blpm.android.data.net.RestInstallmentsApiService
import pe.com.scotiabank.blpm.android.data.net.RestJoyBusinessApiService
import pe.com.scotiabank.blpm.android.data.net.RestLoop2PayApiService
import pe.com.scotiabank.blpm.android.data.net.RestMessageApiService
import pe.com.scotiabank.blpm.android.data.net.RestOtpApiService
import pe.com.scotiabank.blpm.android.data.net.RestP2pApiService
import pe.com.scotiabank.blpm.android.data.net.RestPersonalDashboardApiService
import pe.com.scotiabank.blpm.android.data.net.RestProductApiService
import pe.com.scotiabank.blpm.android.data.net.RestPushNotificationsApiService
import pe.com.scotiabank.blpm.android.data.net.RestQRApiService
import pe.com.scotiabank.blpm.android.data.net.RestRechargeApiService
import pe.com.scotiabank.blpm.android.data.net.RestSuperAccountApiService
import pe.com.scotiabank.blpm.android.data.net.RestWithdrawGoalApiService
import retrofit2.Retrofit

@Module
class ApiServiceModule {

    @Provides
    @ApplicationScope
    fun provideRestJoyApiService(appModel: AppModel): RestApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestMessageApiService(appModel: AppModel): RestMessageApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestMessageApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestNotificationApiService(appModel: AppModel): NotificationApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(NotificationApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestPushNotificationsApiService(appModel: AppModel): RestPushNotificationsApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestPushNotificationsApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestOtpApiService(appModel: AppModel): RestOtpApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestOtpApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestP2pApiService(appModel: AppModel): RestP2pApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestP2pApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestLoop2PayApiService(appModel: AppModel): RestLoop2PayApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestLoop2PayApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestQRApiService(appModel: AppModel): RestQRApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestQRApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestInstallmentsApiService(appModel: AppModel): RestInstallmentsApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestInstallmentsApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestEraserApiService(appModel: AppModel): RestEraserApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestEraserApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestConsentApiService(appModel: AppModel): RestConsentApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestConsentApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestJoyBusinessApiService(appModel: AppModel): RestJoyBusinessApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestJoyBusinessApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestBusinessDashboardApiService(appModel: AppModel): RestBusinessDashboardApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestBusinessDashboardApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestPersonalDashboardApiService(appModel: AppModel): RestPersonalDashboardApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestPersonalDashboardApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestProductApiService(appModel: AppModel): RestProductApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestProductApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestWithdrawGoalApiService(appModel: AppModel): RestWithdrawGoalApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestWithdrawGoalApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestSuperAccountApiService(appModel: AppModel): RestSuperAccountApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestSuperAccountApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestGateBffApiService(appModel: AppModel): RestGateBffApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestGateBffApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestDebitCardApiService(appModel: AppModel): RestDebitCardApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestDebitCardApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideCreditCardApiService(appModel: AppModel): RestCreditCardApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestCreditCardApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestBusinessAppraisalApiService(appModel: AppModel): RestAppraisalApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestAppraisalApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestGooglePayApiService(appModel: AppModel): RestGooglePayApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestGooglePayApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestRechargeApiService(appModel: AppModel): RestRechargeApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestRechargeApiService::class.java)
    }

    @Provides
    @ApplicationScope
    fun provideRestCreditCardPrepaymentApiService(appModel: AppModel): RestCreditCardPrepaymentApiService {
        val sessionRetrofit: Retrofit = appModel.sessionRetrofit
        return sessionRetrofit.create(RestCreditCardPrepaymentApiService::class.java)
    }
}
