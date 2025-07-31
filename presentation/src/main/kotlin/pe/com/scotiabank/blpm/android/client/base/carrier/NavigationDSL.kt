package pe.com.scotiabank.blpm.android.client.base.carrier

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

inline fun destinationCarrierOf(
    screenDestination: Class<out AppCompatActivity>,
    actions: CarrierOfActivityDestination.Builder.() -> Unit = {}
): CarrierOfActivityDestination {
    val builder = CarrierOfActivityDestination.Builder(screenDestination)
    actions(builder)
    return builder.build()
}

inline fun destinationCarrierOf(
    screenDestination: Class<out Fragment>,
    operation: FragmentOperation = FragmentOperation.ADD,
    tagForBackStack: String = screenDestination.simpleName,
    actions: CarrierOfFragmentDestination.Builder.() -> Unit = {}
): CarrierOfFragmentDestination {
    val builder = CarrierOfFragmentDestination.Builder(screenDestination, operation, tagForBackStack)
    actions(builder)
    return builder.build()
}
