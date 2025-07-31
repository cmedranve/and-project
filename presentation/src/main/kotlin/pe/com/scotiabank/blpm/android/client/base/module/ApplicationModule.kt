package pe.com.scotiabank.blpm.android.client.base.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.icu.text.NumberFormat
import androidx.preference.PreferenceManager

import com.fasterxml.jackson.databind.ObjectMapper

import dagger.Module
import dagger.Provides
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.app.JoyApplication
import pe.com.scotiabank.blpm.android.client.app.PushOtpFlowChecker
import pe.com.scotiabank.blpm.android.client.base.ApplicationScope
import pe.com.scotiabank.blpm.android.client.base.UIThread
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProviderAgent
import pe.com.scotiabank.blpm.android.client.base.parser.ObjectMapperProvider
import pe.com.scotiabank.blpm.android.client.products.detailproducts.StatusBadgeProductDetail
import pe.com.scotiabank.blpm.android.data.domain.executor.JobExecutor
import pe.com.scotiabank.blpm.android.data.domain.executor.PostExecutionThread
import pe.com.scotiabank.blpm.android.data.domain.executor.ThreadExecutor
import java.util.Locale
import javax.inject.Named

@Module
open class ApplicationModule {

    @Provides
    @ApplicationScope
    fun provideApplication(app: JoyApplication): Application = app

    @Provides
    @ApplicationScope
    fun provideApplicationContext(app: Application): Context = app

    @Provides
    @ApplicationScope
    fun provideDispatcherProvider(): DispatcherProvider = DispatcherProviderAgent()

    @Provides
    @ApplicationScope
    fun provideThreadExecutor(jobExecutor: JobExecutor): ThreadExecutor = jobExecutor

    @Provides
    @ApplicationScope
    fun providePostExecutionThread(uiThread: UIThread): PostExecutionThread = uiThread

    @Provides
    @ApplicationScope
    fun provideAppModel(app: JoyApplication): AppModel = app.appModel

    @Provides
    @ApplicationScope
    fun provideDefaultLocale(): Locale = Locale("es", "PE")

    @Named("generalNumberFormat")
    @Provides
    @ApplicationScope
    fun provideGeneralNumberFormat(
        defaultLocale: Locale
    ): NumberFormat = NumberFormat.getNumberInstance(defaultLocale)

    @Named("integerNumberFormat")
    @Provides
    @ApplicationScope
    fun provideIntegerNumberFormat(
        defaultLocale: Locale
    ): NumberFormat = NumberFormat.getIntegerInstance(defaultLocale)

    @Provides
    @ApplicationScope
    fun providesSharedPreferences(app: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }

    @Provides
    @ApplicationScope
    fun provideObjectMapper(): ObjectMapper = ObjectMapperProvider.buildObjectMapper()

    @Provides
    @ApplicationScope
    fun providesStatusBadgeProductDetail(appContext: Context) = StatusBadgeProductDetail(appContext)

    @Provides
    @ApplicationScope
    fun providePushOtpFlowChecker(appModel: AppModel) = PushOtpFlowChecker(appModel)
}
