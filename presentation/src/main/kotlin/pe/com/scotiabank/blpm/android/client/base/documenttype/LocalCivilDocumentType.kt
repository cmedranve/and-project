package pe.com.scotiabank.blpm.android.client.base.documenttype

import androidx.annotation.StringRes
import pe.com.scotiabank.blpm.android.client.nosession.shared.documentsection.DocumentType

import pe.com.scotiabank.blpm.android.client.util.Constant

enum class LocalCivilDocumentType(
    @StringRes val labelResId: Int,
    val typeForNetworkCall:CharSequence,
    val minLength: Int,
    val maxLength: Int,
) {

    DNI(
        labelResId = DocumentType.DNI.labelResId,
        typeForNetworkCall = DocumentType.DNI.typeForNetworkCall,
        minLength = DocumentType.DNI.minLength,
        maxLength = DocumentType.DNI.maxLength,
    ),

    TAXPAXER_IDENTIFICATION_NUMBER(
        labelResId = DocumentType.TAXPAXER_IDENTIFICATION_NUMBER.labelResId,
        typeForNetworkCall = DocumentType.TAXPAXER_IDENTIFICATION_NUMBER.typeForNetworkCall,
        minLength = DocumentType.TAXPAXER_IDENTIFICATION_NUMBER.minLength,
        maxLength = DocumentType.TAXPAXER_IDENTIFICATION_NUMBER.maxLength,
    );

    val errorText: String
        get() = "Ingresa" + Constant.SPACE_WHITE + maxLength + Constant.SPACE_WHITE + "dígitos."

    fun isMatchingMaxLength(text: CharSequence): Boolean = text.length == maxLength
}
