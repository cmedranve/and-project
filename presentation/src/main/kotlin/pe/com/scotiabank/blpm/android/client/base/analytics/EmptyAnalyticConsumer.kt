package pe.com.scotiabank.blpm.android.client.base.analytics

import androidx.core.util.Consumer

object EmptyAnalyticConsumer : Consumer<AnalyticEventData<*>> {

    override fun accept(value: AnalyticEventData<*>) {
        // not required
    }
}
