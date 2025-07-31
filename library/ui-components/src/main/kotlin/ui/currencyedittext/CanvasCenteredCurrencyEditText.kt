package pe.com.scotiabank.blpm.android.ui.currencyedittext

import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.scotiabank.canvascore.fonts.FontManager
import com.scotiabank.canvascore.inputs.EditTextHelper
import com.scotiabank.canvascore.inputs.FormInputView
import com.scotiabank.canvascore.utils.CanvasConfiguration
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.CanvasCenteredCurrencyEditTextBinding

class CanvasCenteredCurrencyEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FormInputView(context, attrs, defStyleAttr) {

    val isUnderlineVisible: Boolean
        get() = binding.vInputUnderline.isVisible

    private var textEnteredInputEventCallBack: TextChangedInputEventCallBack? = null
    private var textClearedInputEventCallBack: TextChangedInputEventCallBack? = null
    private var isTextFieldEnabled = false
    private var fontWeight = 0
    private var hintText: String? = null
    private var inputType = 0
    private var onFocusChangeListener: (View, Boolean) -> Unit = { _, _ -> }

    private lateinit var binding: CanvasCenteredCurrencyEditTextBinding

    private var theme: CanvasConfiguration.Companion.Theme = CanvasConfiguration.Companion.Theme.DEFAULT

    interface TextChangedInputEventCallBack {
        fun onTextChanged()
    }

    init {
        initAttributes(context, attrs)
        init(context)
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val fontAttrs = context.theme.obtainStyledAttributes(
            attrs,
            com.scotiabank.canvascore.R.styleable.CanvasCoreFontStyle, 0, 0
        )
        val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.CanvasCurrencyEditText)
        try {
            isTextFieldEnabled = styledAttributes.getBoolean(
                R.styleable.CanvasCurrencyEditText_xcanvascore_isEnabled,
                true
            )
            fontWeight = fontAttrs.getInteger(
                com.scotiabank.canvascore.R.styleable.CanvasCoreFontStyle_canvascore_fontType,
                FontManager.TYPE_REGULAR
            )
            hintText =
                styledAttributes.getString(R.styleable.CanvasCurrencyEditText_xcanvascore_hintText)
            inputType = styledAttributes.getInt(
                R.styleable.CanvasCurrencyEditText_xcanvascore_inputType,
                InputType.TYPE_CLASS_NUMBER
            )
        } finally {
            styledAttributes.recycle()
            fontAttrs.recycle()
        }
    }

    override fun inflateLayout() {
        binding = CanvasCenteredCurrencyEditTextBinding.inflate(
            LayoutInflater.from(context),
            this
        )
        minimumHeight = (resources.getDimension(com.scotiabank.canvascore.R.dimen.canvascore_edittext_min_height)
                / resources.displayMetrics.density).toInt()
        bindEditText(binding.etInput)
        setTheme()
    }

    private fun setTheme() {
        theme = CanvasConfiguration.getInstance(context).getTheme()

        if (theme == CanvasConfiguration.Companion.Theme.ITRADE) {
            setItradeTheme()
        }
    }

    private fun setItradeTheme() {
        binding.tvCurrency.apply {
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(com.scotiabank.canvascore.R.dimen.canvascore_font_18)
            )
        }
        binding.etInput.apply {
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(com.scotiabank.canvascore.R.dimen.canvascore_font_18)
            )
            setHintTextColor(ContextCompat.getColor(context, com.scotiabank.canvascore.R.color.canvascore_itrade_edittext_hint))
        }
    }

    /**
     * sets input type for CanvasCenteredCurrencyEditText
     * @param type Integer value from InputType class
     */
    override fun setInputType(type: Int) {
        binding.etInput.inputType = type
    }

    private fun init(context: Context) {
        initCurrencyField(context)
        initInputField(context)
        setInputType(inputType)
    }

    private fun initCurrencyField(context: Context) {
        binding.tvCurrency.typeface = FontManager.fromFontType(context, fontWeight)
    }

    private fun initInputField(context: Context) {
        binding.etInput.typeface = FontManager.fromFontType(context, fontWeight)
        if (!hintText.isNullOrEmpty()) {
            binding.etInput.hint = hintText
        }
    }

    fun setUnderlineVisible(isVisible: Boolean) {
        binding.vInputUnderline.isVisible = isVisible
    }

    override fun onTextEntered() {
        setTextColor(com.scotiabank.canvascore.R.color.canvascore_edittext_label)

        textEnteredInputEventCallBack?.onTextChanged()
    }

    /** @suppress */
    override fun onTextCleared() {
        textClearedInputEventCallBack?.onTextChanged()
    }

    /** @suppress */
    override fun onFocusGained() {
        onFocusChangeListener.invoke(this, hasFocus)
        setInputUnderlineHeight()
        setTextColor(com.scotiabank.canvascore.R.color.canvascore_edittext_label)
        if (!binding.etInput.text.isNullOrEmpty()) {
            binding.etInput.text?.length?.let(binding.etInput::setSelection)
        }
    }

    private fun setInputUnderlineHeight() {
        @DimenRes val heightRes: Int = pickUnderlineHeight()
        val heightInPx: Int = resources.getDimensionPixelSize(heightRes)
        binding.vInputUnderline.updateLayoutParams<ConstraintLayout.LayoutParams> {
            height = heightInPx
        }
    }

    @DimenRes
    private fun pickUnderlineHeight(): Int {
        if (hasFocus) return com.scotiabank.canvascore.R.dimen.canvascore_height_2
        return com.scotiabank.canvascore.R.dimen.canvascore_height_1
    }

    /** @suppress */
    override fun onFocusLost() {
        onFocusChangeListener.invoke(this, hasFocus)
        setInputUnderlineHeight()
    }

    override fun setDefaultTint() {
        super.setDefaultTint()
        val isItradeTheme: Boolean = CanvasConfiguration(context).isItradeTheme()
        if (isItradeTheme) {
            setItradeTintToInputUnderline()
        } else {
            setNewCanvasTintToInputUnderline()
        }
    }

    override fun setPostEntryTint() {
        super.setPostEntryTint()
        val isItradeTheme: Boolean = CanvasConfiguration(context).isItradeTheme()
        if (isItradeTheme) {
            setItradeTintToInputUnderline()
        } else {
            setNewCanvasTintToInputUnderline()
        }
    }

    private fun setNewCanvasTintToInputUnderline() {
        @ColorRes val colorRes: Int = pickNewCanvasColor()
        setColourStateList(binding.vInputUnderline, colorRes)
    }

    @ColorRes
    private fun pickNewCanvasColor(): Int {
        if (hasFocus) return com.scotiabank.canvascore.R.color.canvascore_edittext_underline_activated
        return com.scotiabank.canvascore.R.color.canvascore_edittext_underline
    }

    private fun setItradeTintToInputUnderline() {
        @ColorRes val colorRes: Int = pickItradeColor()
        setColourStateList(binding.vInputUnderline, colorRes)
    }

    @ColorRes
    private fun pickItradeColor(): Int {
        if (hasFocus) return com.scotiabank.canvascore.R.color.canvascore_itrade_edittext_underline_activated
        return com.scotiabank.canvascore.R.color.canvascore_itrade_edittext_underline
    }

    /**
     * Sets Callback on focus changed.
     *
     * @param listener onFocusChangeListener
     */
    fun setOnFocusChangeListener(listener: (View, Boolean) -> Unit) {
        onFocusChangeListener = listener
    }

    /**
     * Sets Callback on text entered.
     *
     * @param textEnteredInputEventCallBack TextChangedInputEventCallBack
     */
    fun setTextEnteredInputEventCallBack(textEnteredInputEventCallBack: TextChangedInputEventCallBack?) {
        this.textEnteredInputEventCallBack = textEnteredInputEventCallBack
    }

    /**
     * Sets Callback on text cleared.
     *
     * @param textClearedInputEventCallBack TextChangedInputEventCallBack
     */
    fun setTextClearedInputEventCallBack(textClearedInputEventCallBack: TextChangedInputEventCallBack?) {
        this.textClearedInputEventCallBack = textClearedInputEventCallBack
    }

    /**
     * Changes text color of the title
     *
     * @param textColor Resource Color
     */
    private fun setTextColor(@ColorRes textColor: Int) {
        binding.tvCurrency.setTextColor(ContextCompat.getColor(context, textColor))
        binding.etInput.setTextColor(ContextCompat.getColor(context, textColor))
    }

    /** @suppress */
    override fun setEnabled(enabled: Boolean) {
        binding.etInput.isEnabled = enabled
        setTextColor(com.scotiabank.canvascore.R.color.canvascore_edittext_label)

        if (!enabled) {
            @ColorInt val disabledColor: Int = ContextCompat.getColor(context,
                if (theme == CanvasConfiguration.Companion.Theme.ITRADE) {
                    com.scotiabank.canvascore.R.color.canvascore_itrade_label_disabled
                } else {
                    com.scotiabank.canvascore.R.color.canvascore_edittext_underline_disabled
                }
            )
            val colorStateList = ColorStateList.valueOf(disabledColor)
            binding.tvCurrency.backgroundTintList = colorStateList
            binding.vInputUnderline.backgroundTintList = colorStateList
            binding.etInput.backgroundTintList = colorStateList
        }

        super.setEnabled(enabled)
    }

    /**
     * Return CurrencyField object of CanvasCenteredCurrencyEditText
     */
    fun getCurrencyField(): TextView = binding.tvCurrency

    /**
     * Return InputField object of CanvasCenteredCurrencyEditText
     */
    fun getInputField(): EditTextHelper = binding.etInput

    /** @suppress */
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        //no-op
    }

    /** @suppress */
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        clearError()
    }

    /** @suppress */
    override fun afterTextChanged(s: Editable) {
        if (s.isNotEmpty() && hasFocus) {
            onTextEntered()
        } else {
            onTextCleared()
        }
    }
}
