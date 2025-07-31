package pe.com.scotiabank.blpm.android.client.base.datepicker

import com.google.android.material.datepicker.CalendarConstraints
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong

class DatePickerDataHolder(
    val theme: Int,
    val defaultSelection: List<Long>,
    val calendarConstraints: CalendarConstraints,
    val inputMethod: Int,
    val titleText: CharSequence? = null,
    val positiveButtonText: CharSequence? = null,
    val contentDescriptionForPositiveButton: CharSequence? = null,
    val negativeButtonText: CharSequence? = null,
    val contentDescriptionForNegativeButton: CharSequence? = null,
    val receiver: InstanceReceiver? = null,
    val data: Any? = null,
    val id: Long = randomLong(),
)
