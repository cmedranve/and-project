package pe.com.scotiabank.blpm.android.client.base.datepicker

import com.google.android.material.datepicker.MaterialDatePicker

fun <S: Any> MaterialDatePicker.Builder<S>.setDatePickerDataHolder(
    dataHolder: DatePickerDataHolder,
): MaterialDatePicker.Builder<S> {
    return this.setTheme(dataHolder.theme)
        .setCalendarConstraints(dataHolder.calendarConstraints)
        .setInputMode(dataHolder.inputMethod)
        .setTitleText(dataHolder.titleText)
        .setPositiveButtonText(dataHolder.positiveButtonText)
        .setPositiveButtonContentDescription(dataHolder.contentDescriptionForPositiveButton)
        .setNegativeButtonText(dataHolder.negativeButtonText)
        .setNegativeButtonContentDescription(dataHolder.contentDescriptionForNegativeButton)
}
