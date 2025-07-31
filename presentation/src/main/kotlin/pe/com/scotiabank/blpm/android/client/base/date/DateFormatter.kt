package pe.com.scotiabank.blpm.android.client.base.date

import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.DateUtil

class DateFormatter {

    fun format(date: String): String {
        val isYearFormat: Boolean = DateUtil.isDifferentCurrentYear(date)
        val outputFormat: String = if (isYearFormat) Constant.DD_MMM_YYYY else Constant.DD_MMM

        return DateUtil.format(outputFormat, date)
    }

    companion object {

        val DD_OF_MM_OF_YYYY: String
            get() = "dd 'de' MMMM 'del' yyyy"
    }
}
