package pe.com.scotiabank.blpm.android.client.base.carrier

import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity

class CarrierOfActivityDestination private constructor(
    val screenDestination: Class<out AppCompatActivity>,
    val anyBooleansByIdName: Map<String, Boolean?>,
    val anyIntsByIdName: Map<String, Int?>,
    val anyLongsByIdName: Map<String, Long?>,
    val anyStringsByIdName: Map<String, String?>,
    val anyParcelablesByIdName: Map<String, Parcelable?>,
    val flags: Set<Int>,
) {

    fun buildUpon(): Builder {

        val anyBooleansByIdName: MutableMap<String, Boolean?> = mutableMapOf()
        anyBooleansByIdName.putAll(this.anyBooleansByIdName)

        val anyIntsByIdName: MutableMap<String, Int?> = mutableMapOf()
        anyIntsByIdName.putAll(this.anyIntsByIdName)

        val anyLongsByIdName: MutableMap<String, Long?> = mutableMapOf()
        anyLongsByIdName.putAll(this.anyLongsByIdName)

        val anyStringsByIdName: MutableMap<String, String?> = mutableMapOf()
        anyStringsByIdName.putAll(this.anyStringsByIdName)

        val anyParcelablesByIdName: MutableMap<String, Parcelable?> = mutableMapOf()
        anyParcelablesByIdName.putAll(this.anyParcelablesByIdName)

        val flags: MutableSet<Int> = mutableSetOf()
        flags.addAll(this.flags)

        return Builder(
            screenDestination = screenDestination,
            anyBooleansByIdName = anyBooleansByIdName,
            anyIntsByIdName = anyIntsByIdName,
            anyLongsByIdName = anyLongsByIdName,
            anyStringsByIdName = anyStringsByIdName,
            anyParcelablesByIdName = anyParcelablesByIdName,
        )
    }

    class Builder @JvmOverloads constructor(
        private val screenDestination: Class<out AppCompatActivity>,
        private val anyBooleansByIdName: MutableMap<String, Boolean?> = mutableMapOf(),
        private val anyIntsByIdName: MutableMap<String, Int?> = mutableMapOf(),
        private val anyLongsByIdName: MutableMap<String, Long?> = mutableMapOf(),
        private val anyStringsByIdName: MutableMap<String, String?> = mutableMapOf(),
        private val anyParcelablesByIdName: MutableMap<String, Parcelable?> = mutableMapOf(),
        private val flags: MutableSet<Int> = mutableSetOf(),
    ) {

        fun putBooleanBy(idName: String, value: Boolean): Builder = apply {
            anyBooleansByIdName[idName] = value
        }

        fun putIntBy(idName: String, value: Int): Builder = apply {
            anyIntsByIdName[idName] = value
        }

        fun putLongBy(idName: String, value: Long): Builder = apply {
            anyLongsByIdName[idName] = value
        }

        fun putStringBy(idName: String, value: String): Builder = apply {
            anyStringsByIdName[idName] = value
        }

        fun putParcelableBy(idName: String, value: Parcelable): Builder = apply {
            anyParcelablesByIdName[idName] = value
        }

        fun addFlag(flag: Int) = apply {
            flags.add(flag)
        }

        fun build() = CarrierOfActivityDestination(
            screenDestination = screenDestination,
            anyBooleansByIdName = anyBooleansByIdName,
            anyIntsByIdName = anyIntsByIdName,
            anyLongsByIdName = anyLongsByIdName,
            anyStringsByIdName = anyStringsByIdName,
            anyParcelablesByIdName = anyParcelablesByIdName,
            flags = flags,
        )

        infix fun String.to(value: Any) {
            when (value) {
                is Boolean -> putBooleanBy(this, value)
                is Int -> putIntBy(this, value)
                is Long -> putLongBy(this, value)
                is String -> putStringBy(this, value)
                is Parcelable -> putParcelableBy(this, value)
            }
        }
    }
}
