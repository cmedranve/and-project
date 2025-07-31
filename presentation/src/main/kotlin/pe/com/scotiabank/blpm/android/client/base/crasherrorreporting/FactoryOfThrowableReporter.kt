package pe.com.scotiabank.blpm.android.client.base.crasherrorreporting

import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scotiabank.sdk.crasherrorreporting.ThrowableReporter
import com.scotiabank.sdk.crasherrorreporting.annotations.ConsistentKeys
import com.scotiabank.sdk.crasherrorreporting.converter.ProviderConverter
import com.scotiabank.sdk.crasherrorreporting.converter.defaults.DefaultConverterForDeserializationErrorProvider
import com.scotiabank.sdk.crasherrorreporting.converter.defaults.DefaultConverterForNetworkErrorProvider
import com.scotiabank.sdk.crasherrorreporting.network.NetworkErrorProvider
import com.scotiabank.sdk.crasherrorreporting.reporter.Reporter
import pe.com.scotiabank.blpm.android.data.domain.executor.ThreadExecutor
import javax.inject.Inject

class FactoryOfThrowableReporter @Inject constructor(
    private val threadExecutor: ThreadExecutor,
    private val networkErrorProvider: NetworkErrorProvider,
    private val defaultConverterForDeserializationErrorProvider: DefaultConverterForDeserializationErrorProvider,
) {

    private val reporter: Reporter by lazy {
        createReporter()
    }

    private val converterForNetworkErrorProvider: ProviderConverter by lazy {
        DefaultConverterForNetworkErrorProvider(networkErrorProvider)
    }

    fun create(): ThrowableReporter = ThrowableReporter(
        executor = threadExecutor,
        providerConverters = listOf(defaultConverterForDeserializationErrorProvider, converterForNetworkErrorProvider),
        reporters = listOf(reporter),
    )

    private fun createReporter(): Reporter = object : Reporter {

        private val crashlytics = FirebaseCrashlytics.getInstance()

        override fun report(throwable: Throwable, @ConsistentKeys values: Map<String, String>) {
            crashlytics.setCustomKeys(values.toCustomKeysAndValues())
            crashlytics.recordException(throwable)
        }

        override fun report(@ConsistentKeys values: Map<String, String>) {
            crashlytics.setCustomKeys(values.toCustomKeysAndValues())
        }

        private fun Map<String, String>.toCustomKeysAndValues(): CustomKeysAndValues {
            val builder = CustomKeysAndValues.Builder()
            this.forEach(builder::putString)
            return builder.build()
        }
    }
}
