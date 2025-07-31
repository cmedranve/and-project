package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import pe.com.scotiabank.blpm.android.client.newdashboard.products.NewProductModel

interface CardHubService {

    fun add(newProductModel: NewProductModel)

    fun fetchAll(): List<NewProductModel>

    fun clear()

}
