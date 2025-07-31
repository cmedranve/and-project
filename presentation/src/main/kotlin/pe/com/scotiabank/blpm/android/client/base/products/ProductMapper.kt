package pe.com.scotiabank.blpm.android.client.base.products

import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.data.entity.products.common.BaseProductEntity

class ProductMapper {

    fun toProducts(entities: List<BaseProductEntity?>): List<ProductModel> = entities
        .mapNotNull(::toProductIfNotNull)

    private fun toProductIfNotNull(
        entity: BaseProductEntity?,
    ): ProductModel? = entity?.let(::toProduct)

    fun toProduct(entity: BaseProductEntity): ProductModel = ProductModel().apply {
        id = entity.id
        customerProductNumber = entity.customerProductNumber
        currencyId = entity.currency
        availableAmount = entity.availableAmount
        productName = entity.productName
        subProductName = entity.subProductName
        productType = entity.productType
        priority = entity.priority
        subProductType = entity.subProductType
        isAsOrigin = entity.isAsOrigin
        isAsDestination = entity.isAsDestination
        productAlias = entity.additionalData?.alias
        isAsPrincipalAccount = entity.additionalData?.isPrincipalFlag ?: false
    }
}
