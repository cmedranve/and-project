package pe.com.scotiabank.blpm.android.client.base.products.newpicking

import androidx.arch.core.util.Function
import pe.com.scotiabank.blpm.android.client.base.products.ProductMapper
import pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton.ProductGroup
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.data.entity.products.common.BaseProductEntity

class Mapper(
    private val productMapper: ProductMapper,
): Function<List<BaseProductEntity?>, ProductGroup> {

    override fun apply(input: List<BaseProductEntity?>): ProductGroup {
        val products: List<ProductModel> = productMapper.toProducts(input)
        val selectedProduct: ProductModel = products.first()
        return ProductGroup(selectedProduct, products)
    }
}
