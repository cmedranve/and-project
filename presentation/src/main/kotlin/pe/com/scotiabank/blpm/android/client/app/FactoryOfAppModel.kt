package pe.com.scotiabank.blpm.android.client.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.fasterxml.jackson.databind.ObjectMapper
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.factories.AppRoutingFactory
import pe.com.scotiabank.blpm.android.client.base.approuting.AppRoutingDelegate
import pe.com.scotiabank.blpm.android.client.base.checksecurity.FactoryOfCheckSecurityModel
import pe.com.scotiabank.blpm.android.client.base.contentprovider.DirectoryDeleter
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.coroutine.ProviderAgentForCoroutine
import pe.com.scotiabank.blpm.android.client.base.coroutine.ProviderForCoroutine
import pe.com.scotiabank.blpm.android.client.base.crasherrorreporting.FactoryOfThrowableReporter
import pe.com.scotiabank.blpm.android.client.base.font.TypefaceProviderAgent
import pe.com.scotiabank.blpm.android.client.base.network.EnvironmentHolder
import pe.com.scotiabank.blpm.android.client.base.network.FactoryOfCookieJar
import pe.com.scotiabank.blpm.android.client.base.session.FactoryOfSessionModel
import pe.com.scotiabank.blpm.android.client.host.shared.DataStore
import pe.com.scotiabank.blpm.android.client.host.user.UserDao
import pe.com.scotiabank.blpm.android.client.medallia.InitializerOfAppMedallia
import pe.com.scotiabank.blpm.android.client.nosession.keygenerator.KeysGenerator
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.enrollment.EnrollmentModel
import pe.com.scotiabank.blpm.android.data.net.cookie.CookieProvider
import java.lang.ref.WeakReference
import javax.inject.Inject

class FactoryOfAppModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val environmentHolder: EnvironmentHolder,
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val factoryOfCheckSecurityModel: FactoryOfCheckSecurityModel,
    private val analyticsFactoryForAppRouting: AppRoutingFactory,
    private val factoryOfThrowableReporter: FactoryOfThrowableReporter,
    private val factoryOfSessionModel: FactoryOfSessionModel,
    private val objectMapper: ObjectMapper,
    private val defaultSharedPreferences: SharedPreferences,
) {

    fun create(
        weakApp: WeakReference<out Application?>,
        providerForCoroutine: ProviderForCoroutine = createProviderForCoroutine(),
    ): AppModel {
        val storeOfAppPackageInfo = createStoreOfAppPackageInfo(weakApp)
        return AppModel(
            weakApp = weakApp,
            environmentHolder = environmentHolder,
            throwableReporter = factoryOfThrowableReporter.create(),
            handlerOfCheckSecurity = createHandlerOfCheckSecurity(weakApp, providerForCoroutine),
            browserOpener = createBrowserOpener(weakApp),
            typefaceProvider = createTypefaceProvider(weakApp),
            cookieCleaner = CookieCleaner(weakApp, storeOfAppPackageInfo, providerForCoroutine),
            pushOtpCookieCleaner = createPushOtpCookieCleaner(weakApp, storeOfAppPackageInfo, providerForCoroutine),
            legacyStaticKeyCleaner = createLegacyStaticKeyCleaner(),
            commercialNotificationCleaner = CommercialNotificationCleaner(weakApp, providerForCoroutine),
            initializerOfAppMedallia = createInitializerOfAppMedallia(),
            storeOfAppPackageInfo = storeOfAppPackageInfo,
            storeOfAssistanceUi = createStoreOfAssistanceUi(),
            builderOfTimerFacade = createBuilderOfTimerFacade(),
            builderOfAppRoutingDelegate = createBuilderOfAppRoutingDelegate(),
            directoryDeleterFacade = createDirectoryDeleterFacade(weakApp, providerForCoroutine),
            sessionModel = factoryOfSessionModel.create(),
            delegateOfFbLogger = createHolderOfFacebookLogger(weakApp)
        )
    }

    private fun createProviderForCoroutine() = ProviderAgentForCoroutine(dispatcherProvider)

    private fun createHandlerOfCheckSecurity(
        weakAppContext: WeakReference<out Context?>,
        providerForCoroutine: ProviderForCoroutine
    ) = HandlerOfCheckSecurity(
        weakAppContext = weakAppContext,
        providerForCoroutine = providerForCoroutine,
        checkSecurityModel = factoryOfCheckSecurityModel.create(weakAppContext)
    )

    private fun createBrowserOpener(
        weakAppContext: WeakReference<out Context?>
    ) = BrowserOpener(weakAppContext)

    private fun createTypefaceProvider(
        weakAppContext: WeakReference<out Context?>
    ) = TypefaceProviderAgent(weakAppContext)

    private fun createInitializerOfAppMedallia() = InitializerOfAppMedallia()

    private fun createStoreOfAppPackageInfo(
        weakAppContext: WeakReference<out Context?>
    ) = MutableStoreOfAppPackageInfo(weakAppContext)

    private fun createStoreOfAssistanceUi() = MutableStoreOfAssistanceUi()

    private fun createBuilderOfTimerFacade() = MutableTimerFacade.Builder()

    private fun createBuilderOfAppRoutingDelegate(): AppRoutingDelegate.Builder {
        return AppRoutingDelegate.Builder(analyticsDataGateway, analyticsFactoryForAppRouting)
    }

    private fun createDirectoryDeleterFacade(
        weakAppContext: WeakReference<out Context?>,
        providerForCoroutine: ProviderForCoroutine
    ) = DirectoryDeleter(weakAppContext, providerForCoroutine)

    private fun createHolderOfFacebookLogger(
        weakApp: WeakReference<out Application?>
    ) = DelegateOfFbLogger(weakApp)

    private fun createPushOtpCookieCleaner(
        weakApp: WeakReference<out Application?>,
        storeOfAppPackageInfo: MutableStoreOfAppPackageInfo,
        providerForCoroutine: ProviderForCoroutine,
    ): PushOtpCookieCleaner {
        val cookieProvider: CookieProvider = FactoryOfCookieJar.createCookieRepository()
        val userDao: UserDao = DataStore(objectMapper, defaultSharedPreferences)
        return PushOtpCookieCleaner(
            weakApp,
            storeOfAppPackageInfo,
            cookieProvider,
            userDao,
            providerForCoroutine,
        )
    }

    private fun createLegacyStaticKeyCleaner() = LegacyStaticKeyCleaner(
        keysGenerator = KeysGenerator(),
        alias = EnrollmentModel.ALIAS
    )
}
