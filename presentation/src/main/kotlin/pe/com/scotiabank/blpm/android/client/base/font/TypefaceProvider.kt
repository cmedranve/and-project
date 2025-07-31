package pe.com.scotiabank.blpm.android.client.base.font

import android.graphics.Typeface

interface TypefaceProvider {

    val lightTypeface: Typeface
    val boldTypeface: Typeface
    val headlineTypeface: Typeface
    val regularTypeface: Typeface
    val boldItalicTypeface: Typeface
    val italicTypeface: Typeface
    val legalTypeface: Typeface
    val lightItalicTypeface: Typeface
}
