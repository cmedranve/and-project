package pe.com.scotiabank.blpm.android.ui.bottommodal

import android.graphics.drawable.Drawable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import pe.com.scotiabank.blpm.android.ui.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import pe.com.scotiabank.blpm.android.ui.databinding.BottomModalPlinBinding

class BottomModalCallToAction : BottomSheetDialogFragment() {

    companion object {
        private const val TITLE = "title"
        private const val MESSAGE = "message"
        private const val ICON = "icon"
        private const val ACTION_MESSAGE = "action_message"
        private const val DISMISS_MESSAGE = "dismiss_message"

        fun newInstance(args: Bundle?): BottomModalCallToAction = BottomModalCallToAction().apply {
            arguments = args
        }
    }

    var actionListener: (() -> Unit)? = null
    var dismissListener: (() -> Unit)? = null
    var onCreateRunnable: (() -> Unit)? = null
    private lateinit var binding: BottomModalPlinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CallToAcitonModalStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_modal_plin, container, false)
        binding.lifecycleOwner = this
        configDialog()
        return binding.root
    }

    private fun configDialog() = with(binding) {
        val arguments = arguments ?: return@with
        setResource(arguments, DISMISS_MESSAGE, btnDismiss)
        setResource(arguments, TITLE, tvTitle)
        setResource(arguments, ACTION_MESSAGE, btnAction)
        setResource(arguments, MESSAGE, tvMessage)
        checkIcon(arguments)
        setAction()
        setDismiss()
        onCreateRunnable?.invoke()
    }

    private fun checkIcon(arguments: Bundle) {
        if (!arguments.containsKey(ICON)) return
        setIcon(arguments.getInt(ICON))
    }

    private fun setResource(args: Bundle, key: String, tv: TextView) {
        if (!args.containsKey(key)) return
        when {
            args.getString(key) != null -> tv.text = args.getString(key)
            args.getInt(key) != 0 -> tv.setText(args.getInt(key))
        }
    }

    fun setIcon(@DrawableRes drawable: Int) = with(binding.ivIcon) {
        setImageDrawable(ContextCompat.getDrawable(context, drawable))
    }

    fun setIcon(drawable: Drawable) {
        binding.ivIcon.setImageDrawable(drawable)
    }

    fun setAction() = with(binding.btnAction) {
        setOnClickListener { buttonAction(actionListener) }
    }

    fun setDismiss() {
        binding.btnDismiss.setOnClickListener { buttonAction(dismissListener) }
    }

    private fun buttonAction(onClickListener: (() -> Unit)?) {
        dismiss()
        onClickListener?.invoke()
    }

    class Builder {
        private val args: Bundle = Bundle()
        private var actionListener: (() -> Unit)? = null
        private var dismissListener: (() -> Unit)? = null
        private var onCreateRunnable: (() -> Unit)? = null

        fun setTitle(title: String?): Builder {
            args.putString(TITLE, title)
            return this
        }

        fun setTitle(@StringRes title: Int): Builder {
            args.putInt(TITLE, title)
            return this
        }

        fun setMessage(message: String?): Builder {
            args.putString(MESSAGE, message)
            return this
        }

        fun setMessage(@StringRes message: Int): Builder {
            args.putInt(MESSAGE, message)
            return this
        }

        fun setIcon(@DrawableRes drawable: Int): Builder {
            args.putInt(ICON, drawable)
            return this
        }

        fun setAction(listener: () -> Unit): Builder {
            actionListener = listener
            return this
        }

        fun setDismiss(listener: (() -> Unit)?): Builder {
            dismissListener = listener
            return this
        }

        fun setActionMessage(@StringRes message: Int): Builder {
            args.putInt(ACTION_MESSAGE, message)
            return this
        }

        fun setActionMessage(message: String?): Builder {
            args.putString(ACTION_MESSAGE, message)
            return this
        }

        fun setDismissMessage(@StringRes message: Int): Builder {
            args.putInt(DISMISS_MESSAGE, message)
            return this
        }

        fun setDismissMessage(message: String?): Builder {
            args.putString(DISMISS_MESSAGE, message)
            return this
        }

        fun setOnCreateRunnable(runnable: (() -> Unit)?): Builder {
            onCreateRunnable = runnable
            return this
        }

        fun build(): BottomModalCallToAction {
            return newInstance(args).also { modal ->
                modal.actionListener = actionListener
                modal.dismissListener = dismissListener
                modal.onCreateRunnable = onCreateRunnable
            }
        }

    }
}
