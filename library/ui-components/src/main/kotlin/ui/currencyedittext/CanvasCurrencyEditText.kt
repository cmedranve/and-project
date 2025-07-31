package pe.com.scotiabank.blpm.android.ui.currencyedittext

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
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
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentManager
import com.scotiabank.canvascore.fonts.FontManager
import com.scotiabank.canvascore.inputs.EditTextHelper
import com.scotiabank.canvascore.inputs.FormInputView
import com.scotiabank.canvascore.utils.CanvasConfiguration
import com.scotiabank.canvascore.views.CanvasTextView
import com.scotiabank.canvascore.views.ToolTip
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.CanvasCurrencyEditTextBinding

class CanvasCurrencyEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FormInputView(context, attrs, defStyleAttr) {

    val isUnderlineVisible: Boolean
        get() = binding.vInputUnderline.isVisible

    private var textEnteredInputEventCallBack: TextChangedInputEventCallBack? = null
    private var textClearedInputEventCallBack: TextChangedInputEventCallBack? = null
    private var textClearButtonEventCallback: TextChangedInputEventCallBack? = null
    private var isTextFieldEnabled = false
    private var isClearEnabled = false
    private var isReadOnly = false
    private var fontWeight = 0
    private var titleText: String? = null
    private var subtitleText: String? = null
    private var hintText: String? = null
    private var inputType = 0
    private var rightDrawable: Drawable? = null
    private var rightDrawableContentDescription: String? = null
    private var onFocusChangeListener: (View, Boolean) -> Unit = { _, _ -> }

    private lateinit var binding: CanvasCurrencyEditTextBinding

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
            titleText =
                styledAttributes.getString(R.styleable.CanvasCurrencyEditText_xcanvascore_titleText)
            subtitleText =
                styledAttributes.getString(R.styleable.CanvasCurrencyEditText_xcanvascore_subtitleText)
            hintText =
                styledAttributes.getString(R.styleable.CanvasCurrencyEditText_xcanvascore_hintText)
            isClearEnabled = styledAttributes.getBoolean(
                R.styleable.CanvasCurrencyEditText_xcanvascore_isClearEnabled,
                false
            )
            isReadOnly = styledAttributes.getBoolean(
                R.styleable.CanvasCurrencyEditText_xcanvascore_isReadOnly,
                false
            )
            inputType = styledAttributes.getInt(
                R.styleable.CanvasCurrencyEditText_xcanvascore_inputType,
                InputType.TYPE_CLASS_NUMBER
            )
            rightDrawable = styledAttributes.getDrawable(
                R.styleable.CanvasCurrencyEditText_xcanvascore_drawableRight
            )
            rightDrawableContentDescription = styledAttributes.getString(
                R.styleable.CanvasCurrencyEditText_xcanvascore_drawableRightContentDescription
            )
        } finally {
            styledAttributes.recycle()
            fontAttrs.recycle()
        }
    }

    override fun inflateLayout() {
        binding = CanvasCurrencyEditTextBinding.inflate(
            LayoutInflater.from(context),
            this
        )
        binding.imgInputClear.setOnClickListener {
            binding.etInput.text = null
            binding.imgInputClear.visibility = View.GONE
            textClearButtonEventCallback?.onTextChanged()
        }
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
        binding.tvTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(com.scotiabank.canvascore.R.dimen.canvascore_font_14)
        )
        binding.tvSubtitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(com.scotiabank.canvascore.R.dimen.canvascore_font_14)
        )
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
        binding.tvReadOnlyText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(com.scotiabank.canvascore.R.dimen.canvascore_font_18)
        )
    }

    /**
     * sets input type for CanvasCurrencyEditText
     * @param type Integer value from InputType class
     */
    override fun setInputType(type: Int) {
        binding.etInput.inputType = type
    }

    private fun init(context: Context) {
        initTitle(context)
        initSubtitle(context)
        initCurrencyField(context)
        initInputField(context)
        initReadOnly()
        initRightDrawable()
        setInputType(inputType)
    }

    private fun initTitle(context: Context) {
        binding.tvTitle.typeface = FontManager.fromFontType(context, FontManager.TYPE_BOLD)
        if (titleText.isNullOrEmpty()) {
            binding.tvTitle.visibility = View.GONE
        } else {
            binding.tvTitle.apply {
                visibility = View.VISIBLE
                text = titleText
            }
        }
    }

    private fun initSubtitle(context: Context) {
        binding.tvSubtitle.typeface = FontManager.fromFontType(context, fontWeight)
        if (subtitleText.isNullOrEmpty()) {
            binding.tvSubtitle.visibility = View.GONE
        } else {
            binding.tvSubtitle.apply {
                visibility = View.VISIBLE
                text = subtitleText
            }
        }
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

    private fun initReadOnly() {
        if (isReadOnly) {
            setReadOnly()
        } else {
            isEnabled = isTextFieldEnabled
        }
    }

    private fun setReadOnly() {
        binding.tvReadOnlyText.visibility = View.VISIBLE
        binding.tvCurrency.visibility = View.GONE
        binding.etInput.visibility = View.GONE
        binding.vInputUnderline.visibility = View.GONE

        @ColorInt val readOnlyColor: Int = ContextCompat.getColor(
            context,
            com.scotiabank.canvascore.R.color.canvascore_edittext_label,
        )
        binding.tvReadOnlyText.setTextColor(readOnlyColor)

        binding.tvTitle.labelFor = com.scotiabank.canvascore.R.id.tv_readOnlyText
    }

    fun setUnderlineVisible(isVisible: Boolean) {
        binding.vInputUnderline.isVisible = isVisible
    }

    private fun initRightDrawable() {
        if (rightDrawable != null) {
            binding.imgRightIcon.setImageDrawable(rightDrawable)
            setRightDrawableVisibility(binding.imgInputClear.visibility == View.GONE)
        }

        if (!rightDrawableContentDescription.isNullOrEmpty()) {
            binding.imgRightIcon.contentDescription = rightDrawableContentDescription
        }
    }

    fun tintUnderlineWithErrorColor(errorText: CharSequence) {
        if (errorText.isBlank()) {
            setPostEntryTint()
            return
        }
        setColourStateList(binding.vInputUnderline, com.scotiabank.canvascore.R.color.canvascore_form_field_error)
    }

    /** @suppress */
    override fun onTextEntered() {
        setTitleColor(com.scotiabank.canvascore.R.color.canvascore_edittext_label)
        if (isClearEnabled) {
            binding.imgInputClear.visibility = View.VISIBLE
        }

        setRightDrawableVisibility(false)
        textEnteredInputEventCallBack?.onTextChanged()
    }

    /** @suppress */
    override fun onTextCleared() {
        binding.imgInputClear.visibility = View.GONE
        setRightDrawableVisibility(true)
        textClearedInputEventCallBack?.onTextChanged()
    }

    /** @suppress */
    override fun onFocusGained() {
        onFocusChangeListener.invoke(this, hasFocus)
        setInputUnderlineHeight()
        setTitleColor(com.scotiabank.canvascore.R.color.canvascore_edittext_label)
        if (!binding.etInput.text.isNullOrEmpty()) {
            binding.etInput.text?.length?.let(binding.etInput::setSelection)
        }
        if (isClearEnabled) {
            binding.imgInputClear.visibility = View.VISIBLE
            setRightDrawableVisibility(false)
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
        binding.imgInputClear.visibility = View.GONE
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
     * Enables or disables clear button
     *
     * @param isClearEnabled
     */
    fun enableClearButton(isClearEnabled: Boolean) {
        this.isClearEnabled = isClearEnabled
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

    fun setTextClearButtonEventCallback(textClearButtonEventCallback: TextChangedInputEventCallBack?) {
        this.textClearButtonEventCallback = textClearButtonEventCallback
    }

    /**
     * Set the Right Drawable source
     *
     * @param rightDrawable drawable icon
     */
    fun setRightDrawableSrc(rightDrawable: Drawable? = null) {
        this.rightDrawable = rightDrawable
        binding.imgRightIcon.setImageDrawable(rightDrawable)
        setRightDrawableVisibility(rightDrawable != null)
    }

    /**
     * Set the Right Drawable source
     *
     * @param rightDrawable drawable icon Id
     */
    fun setRightDrawableSrc(@DrawableRes rightDrawable: Int? = null) {
        var drawableRef: Drawable? = null
        if (rightDrawable != null) {
            drawableRef = ContextCompat.getDrawable(context, rightDrawable)
        }
        setRightDrawableSrc(drawableRef)
    }

    /**
     * Set the content description for the right drawable
     *
     * @param description right drawable description
     */
    fun setRightDrawableContentDescription(description: String?) {
        this.rightDrawableContentDescription = description
        binding.imgRightIcon.contentDescription = description
    }

    /**
     * Set the content description for the right drawable
     *
     * @param description right drawable description Id
     */
    fun setRightDrawableContentDescription(@StringRes description: Int?) {
        var descriptionRef: String? = null
        if (description != null) {
            descriptionRef = context.getString(description)
        }

        setRightDrawableContentDescription(descriptionRef)
    }

    /**
     * Set the click listener for the right drawable
     */
    fun setRightDrawableOnClickListener(callback: () -> Unit) {
        binding.imgRightIcon.setOnClickListener { callback.invoke() }
    }

    /**
     * Set the Right Drawable Icon Visibility
     *
     * @param visible True = View.VISIBLE
     */
    private fun setRightDrawableVisibility(visible: Boolean = false) {
        if (this.rightDrawable != null && visible) {
            binding.imgRightIcon.visibility = View.VISIBLE
        } else {
            binding.imgRightIcon.visibility = View.GONE
        }
    }

    /**
     * Sets title to the component
     *
     * @param titleString CharSequence
     */
    fun setTitle(titleString: String?) {
        titleText = titleString
        binding.tvTitle.apply {
            visibility = View.VISIBLE
            text = titleText
        }
    }

    /**
     * Sets subtitle to the component
     *
     * @param subtitleString CharSequence
     */
    fun setSubtitle(subtitleString: CharSequence?) {
        binding.tvSubtitle.apply {
            visibility = View.VISIBLE
            text = subtitleString
        }
    }

    /**
     * Sets ContentDescription for Clear Button on UserNameInputComponent
     *
     * @param description CharSequence
     */
    fun setClearButtonContentDescription(description: CharSequence?) {
        binding.imgInputClear.contentDescription = description
    }

    /**
     * Changes text color of the title
     *
     * @param titleColor Resource Color
     */
    private fun setTitleColor(@ColorRes titleColor: Int) {
        binding.tvTitle.setTextColor(ContextCompat.getColor(context, titleColor))
        binding.tvSubtitle.setTextColor(ContextCompat.getColor(context, titleColor))
        binding.tvCurrency.setTextColor(ContextCompat.getColor(context, titleColor))
        binding.etInput.setTextColor(ContextCompat.getColor(context, titleColor))
    }

    override fun setError(stringRes: Int) {
        super.setError(stringRes)
        binding.tvTitle.setTextColor(ContextCompat.getColor(context, com.scotiabank.canvascore.R.color.canvascore_edittext_error))
        binding.tvSubtitle.setTextColor(ContextCompat.getColor(context, com.scotiabank.canvascore.R.color.canvascore_edittext_error))
        binding.imgTooltip.setColorFilter(ContextCompat.getColor(context, com.scotiabank.canvascore.R.color.canvascore_edittext_error), PorterDuff.Mode.SRC_IN)
    }

    override fun setError(error: CharSequence) {
        super.setError(error)
        binding.tvTitle.setTextColor(ContextCompat.getColor(context, com.scotiabank.canvascore.R.color.canvascore_edittext_error))
        binding.tvSubtitle.setTextColor(ContextCompat.getColor(context, com.scotiabank.canvascore.R.color.canvascore_edittext_error))
        binding.imgTooltip.setColorFilter(ContextCompat.getColor(context, com.scotiabank.canvascore.R.color.canvascore_edittext_error), PorterDuff.Mode.SRC_IN)
    }

    /**
     * Removes all the error views attached to CanvasCurrencyEditText
     */
    override fun clearErrorBindings() {
        super.clearErrorBindings()
        setTitleColor(com.scotiabank.canvascore.R.color.canvascore_edittext_label)
        binding.tvSubtitle.setTextColor(ContextCompat.getColor(context, com.scotiabank.canvascore.R.color.canvascore_edittext_label))
        binding.imgTooltip.setColorFilter(ContextCompat.getColor(context, com.scotiabank.canvascore.R.color.canvascore_edittext_label), PorterDuff.Mode.SRC_IN)
    }

    /**
     * Clears all the errors of the CanvasCurrencyEditText
     */
    override fun clearError() {
        super.clearError()
        setTitleColor(com.scotiabank.canvascore.R.color.canvascore_edittext_label)
        binding.tvSubtitle.setTextColor(ContextCompat.getColor(context, com.scotiabank.canvascore.R.color.canvascore_edittext_label))
        binding.imgTooltip.setColorFilter(ContextCompat.getColor(context, com.scotiabank.canvascore.R.color.canvascore_edittext_label), PorterDuff.Mode.SRC_IN)
    }

    /** @suppress */
    override fun setContentDescription(contentDescription: CharSequence) {
        super.setContentDescription(
            contentDescription.toString()
                .plus(
                    resources.getString(com.scotiabank.canvascore.R.string.canvascore_space)
                        .plus(titleText)
                )
        )
        setErrorViewContentDescription(contentDescription.toString())
    }

    /** @suppress */
    override fun setEnabled(enabled: Boolean) {
        binding.etInput.isEnabled = enabled
        setTitleColor(com.scotiabank.canvascore.R.color.canvascore_edittext_label)

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

    private fun getDensityPixelFromFloat(value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            resources.displayMetrics
        ).toInt()
    }

    /**
     * Return CurrencyField object of CanvasCurrencyEditText
     */
    fun getCurrencyField(): TextView = binding.tvCurrency

    /**
     * Return InputField object of CanvasCurrencyEditText
     */
    fun getInputField(): EditTextHelper = binding.etInput

    /**
     * Returns title object of CanvasCurrencyEditText
     */
    fun getTitleTextView(): CanvasTextView = binding.tvTitle

    /**
     * Returns sub title object of CanvasCurrencyEditText
     */
    fun getSubTitleTextView(): CanvasTextView = binding.tvSubtitle

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

    override fun setText(charSequence: CharSequence) {
        if (isReadOnly) {
            binding.tvReadOnlyText.apply {
                text = charSequence
                contentDescription = charSequence
            }
        } else {
            super.setText(charSequence)
        }
    }

    /**
     * Sets tooltip to the component.
     *
     * @param accessibilityText: String Accessibility Text to be announced on focus
     * @param headlineText: String headline text of the tooltip
     * @param contentText: String content text of the tooltip
     * @param buttonText: String button text of the tooltip
     */
    fun setToolTip(
        accessibilityText: String, headlineText: String, contentText: String,
        buttonText: String, fm: FragmentManager
    ) {
        binding.imgTooltip.apply {
            visibility = View.VISIBLE
            contentDescription = accessibilityText
            setOnClickListener {
                ToolTip(headlineText, contentText, buttonText).show(fm)
            }
        }
    }

    /**
     * Hides the tooltip icon from the component.
     */
    fun hideToolTip() {
        binding.imgTooltip.apply {
            visibility = View.GONE
        }
    }
}
