package pe.com.scotiabank.blpm.android.client.base.module

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import pe.com.scotiabank.blpm.android.client.app.JoyApplication
import pe.com.scotiabank.blpm.android.client.base.AnalyticsModule
import pe.com.scotiabank.blpm.android.client.base.ApplicationScope
import pe.com.scotiabank.blpm.android.client.base.RepositoryModule
import pe.com.scotiabank.blpm.android.client.base.builder.ActivityBuilder
import pe.com.scotiabank.blpm.android.client.base.builder.FragmentBuilder
import pe.com.scotiabank.blpm.android.client.base.crasherrorreporting.CrashAndErrorReportingModule

@ApplicationScope
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        NetworkingModule::class,
        ClientModule::class,
        ApiServiceModule::class,
        AnalyticsModule::class,
        RepositoryModule::class,
        CrashAndErrorReportingModule::class,
        ActivityBuilder::class,
        FragmentBuilder::class,
    ]
)
interface ApplicationComponent : AndroidInjector<JoyApplication> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance application: JoyApplication): ApplicationComponent
    }
}
