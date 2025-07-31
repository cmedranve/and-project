package pe.com.scotiabank.blpm.android.client.base.font

import android.content.Context
import android.graphics.Typeface
import com.scotiabank.canvascore.fonts.FontManager
import java.lang.ref.WeakReference

class TypefaceProviderAgent(
    private val weakAppContext: WeakReference<out Context?>
): TypefaceProvider {

    override val lightTypeface: Typeface by lazy {
        weakAppContext.get()?.let(FontManager::newLight) ?: Typeface.DEFAULT
    }

    override val boldTypeface: Typeface by lazy {
        weakAppContext.get()?.let(FontManager::newBold) ?: Typeface.DEFAULT_BOLD
    }

    override val headlineTypeface: Typeface by lazy {
        weakAppContext.get()?.let(FontManager::headline) ?: Typeface.DEFAULT_BOLD
    }

    override val regularTypeface: Typeface by lazy {
        weakAppContext.get()?.let(FontManager::newRegular) ?: Typeface.DEFAULT
    }

    override val boldItalicTypeface: Typeface by lazy {
        weakAppContext.get()?.let(FontManager::newBoldItalic) ?: Typeface.DEFAULT_BOLD
    }

    override val italicTypeface: Typeface by lazy {
        weakAppContext.get()?.let(FontManager::newItalic) ?: Typeface.DEFAULT
    }

    override val legalTypeface: Typeface by lazy {
        weakAppContext.get()?.let(FontManager::legal) ?: Typeface.DEFAULT
    }

    override val lightItalicTypeface: Typeface by lazy {
        weakAppContext.get()?.let(FontManager::lightItalic) ?: Typeface.DEFAULT
    }
}
