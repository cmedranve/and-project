package pe.com.scotiabank.blpm.android.ui.list.items.inputs

import android.text.InputFilter
import android.text.Spanned
import pe.com.scotiabank.blpm.android.ui.util.Constant

class DayNumberInputFilter : InputFilter {

    private val regex: Regex = createRegex()

    private fun createRegex(): Regex {
        val pattern = "([1-9]?)([0-9]?)"
        return pattern.toRegex()
    }

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int,
    ): CharSequence? {

        val replacement: String = source?.substring(start, end).orEmpty()
        val newVal: String = dest?.substring(0, dstart) + replacement + dest?.substring(dend, dest.length)
        if (newVal.matches(regex)) return null

        return if (source.isNullOrEmpty()) dest?.substring(dstart, dend) else Constant.EMPTY
    }
}
