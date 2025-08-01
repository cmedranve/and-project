package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel

interface FrequentOperationService {

    val quantity: Int

    fun add(frequentOperation: FrequentOperationModel)

    fun edit(frequentOperation: FrequentOperationModel)

    fun remove(frequentOperation: FrequentOperationModel)

    fun clear()
}
