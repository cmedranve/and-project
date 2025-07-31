package pe.com.scotiabank.blpm.android.client.host.shared

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.icu.text.NumberFormat
import androidx.core.util.Function
import com.fasterxml.jackson.databind.ObjectMapper
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.sdk.crasherrorreporting.deserializer.ConverterFactoryDecorator
import okhttp3.OkHttpClient
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsUserGateway
import pe.com.scotiabank.blpm.android.analytics.factories.SystemDataFactory
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.network.ExternalEnvironment
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CurrencyFormatter
import pe.com.scotiabank.blpm.android.client.base.point.PointFormatter
import pe.com.scotiabank.blpm.android.client.base.products.picking.FormatterOfProductName
import pe.com.scotiabank.blpm.android.client.base.twocolumntext.FactoryOfTwoColumnEntity
import pe.com.scotiabank.blpm.android.client.base.twocolumntext.FactoryOfUpToTwoColumnEntity
import pe.com.scotiabank.blpm.android.client.host.user.UserDao
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.enrollment.FirebaseNotificationTokenProvider
import pe.com.scotiabank.blpm.android.client.scotiapay.UserDataUpdater
import pe.com.scotiabank.blpm.android.client.util.FormatterUtil
import pe.com.scotiabank.blpm.android.data.net.cookie.CookieProvider
import pe.com.scotiabank.blpm.android.data.net.interceptor.HolderOfSensorData
import java.lang.ref.WeakReference
import java.util.Locale

class Hub(
    val dispatcherProvider: DispatcherProvider,
    val mutableLiveHolder: MutableLiveHolder,
    val userInterface: InstanceReceiver,
    val holderOfWeakActivity: HolderOfWeakActivity,
    val okHttpClient: OkHttpClient,
    val environment: ExternalEnvironment,
    val converterFactoryDecorator: ConverterFactoryDecorator,
    val objectMapper: ObjectMapper,
    val defaultLocale: Locale,
    val generalNumberFormat: NumberFormat,
    val integerNumberFormat: NumberFormat,
    val appModel: AppModel,
    val appContext: Context,
    val weakResources: WeakReference<Resources?>,
    val weakAppContext: WeakReference<Context?>,
    val userDao: UserDao,
    val cookieProvider: CookieProvider,
    val defaultSharedPreferences: SharedPreferences,
    val analyticsDataGateway: AnalyticsDataGateway,
    val analyticsUserGateway: AnalyticsUserGateway,
    val systemDataFactory: SystemDataFactory,
) {

    val holderOfSensorData: HolderOfSensorData by lazy {
        MutableHolderOfSensorData()
    }

    val errorTextOnFieldRequired: CharSequence by lazy {
        weakResources.get()?.getString(R.string.this_field_is_required).orEmpty()
    }

    val factoryOfOneColumnTextEntity: FactoryOfOneColumnTextEntity by lazy {
        FactoryOfOneColumnTextEntity()
    }

    val factoryOfTwoColumnTextEntity: FactoryOfTwoColumnEntity by lazy {
        FactoryOfTwoColumnEntity()
    }

    val factoryOfUpToTwoColumnEntity: FactoryOfUpToTwoColumnEntity by lazy {
        FactoryOfUpToTwoColumnEntity()
    }

    val formatterOfProductName: FormatterOfProductName by lazy {
        FormatterOfProductName()
    }

    val currencyFormatterWithoutDecimals: CurrencyFormatter by lazy {
        CurrencyFormatter(
            formatting = Function(FormatterUtil::formatWithoutDecimals),
        )
    }

    val currencyFormatter: CurrencyFormatter by lazy {
        CurrencyFormatter(
            formatting = Function(FormatterUtil::format),
        )
    }

    val exchangeRateFormatter: CurrencyFormatter by lazy {
        CurrencyFormatter(
            formatting = Function(FormatterUtil::formatExchangeRate),
        )
    }

    val pointFormatter: PointFormatter by lazy {
        PointFormatter(integerNumberFormat, weakResources)
    }

    val userDataUpdater: UserDataUpdater by lazy {
        UserDataUpdater(
            appModel = appModel,
            userDao = userDao,
        )
    }

    val notificationTokenProvider: FirebaseNotificationTokenProvider by lazy {
        FirebaseNotificationTokenProvider()
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
        private val okHttpClient: OkHttpClient,
        private val environment: ExternalEnvironment,
        private val converterFactoryDecorator: ConverterFactoryDecorator,
        private val objectMapper: ObjectMapper,
        private val defaultLocale: Locale,
        private val generalNumberFormat: NumberFormat,
        private val integerNumberFormat: NumberFormat,
        private val appModel: AppModel,
        private val appContext: Context,
        private val weakResources: WeakReference<Resources?>,
        private val weakAppContext: WeakReference<Context?>,
        private val userDao: UserDao,
        private val cookieProvider: CookieProvider,
        private val defaultSharedPreferences: SharedPreferences,
        private val analyticsDataGateway: AnalyticsDataGateway,
        private val analyticsUserGateway: AnalyticsUserGateway,
        private val systemDataFactory: SystemDataFactory,
    ) {

        fun create(
            mutableLiveHolder: MutableLiveHolder,
            userInterface: UserInterface,
        ) = Hub(
            dispatcherProvider = dispatcherProvider,
            mutableLiveHolder = mutableLiveHolder,
            userInterface = userInterface,
            holderOfWeakActivity = userInterface,
            okHttpClient = okHttpClient,
            environment = environment,
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
            cookieProvider = cookieProvider,
            defaultSharedPreferences = defaultSharedPreferences,
            analyticsDataGateway = analyticsDataGateway,
            analyticsUserGateway = analyticsUserGateway,
            systemDataFactory = systemDataFactory,
        )
    }
}