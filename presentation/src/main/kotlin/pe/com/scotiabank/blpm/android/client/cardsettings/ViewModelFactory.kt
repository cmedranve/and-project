package pe.com.scotiabank.blpm.android.client.cardsettings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.icu.text.NumberFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.fasterxml.jackson.databind.ObjectMapper
import com.scotiabank.sdk.crasherrorreporting.deserializer.ConverterFactoryDecorator
import okhttp3.OkHttpClient
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsUserGateway
import pe.com.scotiabank.blpm.android.analytics.factories.SystemDataFactory
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.network.EnvironmentHolder
import pe.com.scotiabank.blpm.android.client.base.network.FactoryOfCookieJar
import pe.com.scotiabank.blpm.android.client.base.registry.VisitRegistry
import pe.com.scotiabank.blpm.android.client.host.shared.DataStore
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.host.user.UserDao
import pe.com.scotiabank.blpm.android.data.net.cookie.CookieRepository
import java.lang.ref.WeakReference
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

class ViewModelFactory @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val environmentHolder: EnvironmentHolder,
    private val converterFactoryDecorator: ConverterFactoryDecorator,
    private val objectMapper: ObjectMapper,
    private val defaultLocale: Locale,
    @Named("generalNumberFormat") private val generalNumberFormat: NumberFormat,
    @Named("integerNumberFormat") private val integerNumberFormat: NumberFormat,
    private val appModel: AppModel,
    private val appContext: Context,
    private val defaultSharedPreferences: SharedPreferences,
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticsUserGateway: AnalyticsUserGateway,
    @Named("systemDataFactorySession") private val systemDataFactory: SystemDataFactory,
) : ViewModelProvider.Factory {

    private val weakResources: WeakReference<Resources?> = WeakReference(appContext.resources)
    private val weakAppContext: WeakReference<Context?> = WeakReference(appContext)
    private val userDao: UserDao = DataStore(objectMapper, defaultSharedPreferences)
    private val cookieRepository: CookieRepository = FactoryOfCookieJar.createCookieRepository()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(CardSettingHubHostViewModel::class.java)) {
            return createViewModel(extras) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun createViewModel(
        extras: CreationExtras,
    ): CardSettingHubHostViewModel = CardSettingHubHostViewModel(
        dispatcherProvider = dispatcherProvider,
        hubFactory = createHubFactory(),
        environmentHolder = environmentHolder,
        visitRegistry = createVisitRegistry(),
        appModel = appModel,
    )

    private fun createVisitRegistry(): VisitRegistry {
        val maxNumberAllowedById: Map<Long, Int> = mapOf(
            CardSettingHubHostViewModel.HOST_ID_FOR_VISIT_REGISTRY to 1,
        )
        return VisitRegistry(maxNumberAllowedById)
    }

    private fun createHubFactory(): Hub.Factory = Hub.Factory(
        dispatcherProvider = dispatcherProvider,
        okHttpClient = OkHttpClient(),
        environment = environmentHolder.environment,
        converterFactoryDecorator = converterFactoryDecorator,
        objectMapper = objectMapper,
        defaultLocale = defaultLocale,
        generalNumberFormat = generalNumberFormat,
        integerNumberFormat = integerNumberFormat,
        appModel = appModel,
        appContext = appContext,
        weakResources = weakResources,
        weakAppContext = weakAppContext,
        userDao = userDao,
        cookieProvider = cookieRepository,
        defaultSharedPreferences = defaultSharedPreferences,
        analyticsDataGateway = analyticsDataGateway,
        analyticsUserGateway = analyticsUserGateway,
        systemDataFactory = systemDataFactory,
    )
}
