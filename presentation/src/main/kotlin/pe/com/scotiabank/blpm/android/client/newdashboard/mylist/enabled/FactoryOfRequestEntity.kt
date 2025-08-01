package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.radiobutton.CollectorOfInstallmentChipsComponent
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.payment.institutions.PaymentModelDataMapper
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.data.entity.FrequentItemEntity
import pe.com.scotiabank.blpm.android.data.entity.FrequentItemWrapperEntity

class FactoryOfRequestEntity {

    fun createEntity(
        originProduct: ProductModel,
        numberOfInstallments: Int,
        operations: Collection<FrequentOperationModel>,
    ): FrequentItemWrapperEntity {

        val requestEntity: FrequentItemWrapperEntity = PaymentModelDataMapper.transformFrequentOperationModels(originProduct.id, operations)

        val isLessThanMinimum: Boolean = numberOfInstallments < CollectorOfInstallmentChipsComponent.MIN_NUMBER_OF_INSTALLMENTS_ALLOWED
        if (isLessThanMinimum) return requestEntity

        val dataEntities: List<FrequentItemEntity> = requestEntity.items

        dataEntities.forEach { dataEntity ->
            dataEntity.installmentsNumber = numberOfInstallments.toString()
        }

        return requestEntity
    }
}