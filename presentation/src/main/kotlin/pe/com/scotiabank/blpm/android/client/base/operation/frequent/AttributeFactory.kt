package pe.com.scotiabank.blpm.android.client.base.operation.frequent

import pe.com.scotiabank.blpm.android.client.model.ConfirmationModel
import pe.com.scotiabank.blpm.android.data.entity.AttributeEntity

interface AttributeFactory {

    fun create(confirmation: ConfirmationModel): AttributeEntity
}
