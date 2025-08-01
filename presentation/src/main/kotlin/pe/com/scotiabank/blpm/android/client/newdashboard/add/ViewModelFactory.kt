package pe.com.scotiabank.blpm.android.client.newdashboard.add

import android.content.Context
import android.content.res.Resources
import androidx.core.util.Function
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.fasterxml.jackson.databind.ObjectMapper
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.date.DateFormatter
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CurrencyFormatter
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType
import pe.com.scotiabank.blpm.android.client.base.registry.VisitRegistry
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.util.FormatterUtil
import pe.com.scotiabank.blpm.android.data.mapper.RecentTransactionsMapper
import pe.com.scotiabank.blpm.android.data.net.MyListApiService
import pe.com.scotiabank.blpm.android.data.net.RestPersonalTransferApiService
import pe.com.scotiabank.blpm.android.data.repository.RecentTransactionsRepository
import pe.com.scotiabank.blpm.android.data.repository.mylist.MyListRepository
import java.lang.ref.WeakReference
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val objectMapper: ObjectMapper,
    private val appModel: AppModel,
    appContext: Context,
    private val repository: RecentTransactionsRepository,
) : ViewModelProvider.Factory {

    private val weakResources: WeakReference<Resources?> = WeakReference(appContext.resources)
    private val dateFormatter = DateFormatter()
    private val currencyAmountFormatter = CurrencyFormatter(
        formatting = Function(FormatterUtil::format),
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(AddOperationViewModel::class.java)) {
            return createViewModel(extras) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun createViewModel(extras: CreationExtras): AddOperationViewModel {

        val api = appModel.sessionRetrofit.create(MyListApiService::class.java)
        val myListRepository = MyListRepository(api, objectMapper)
        val mapper = RecentTransactionsMapper(objectMapper)

        val model = AddOperationModel(
            dispatcherProvider = dispatcherProvider,
            myListRepository = myListRepository,
            repository = repository,
            mapper = mapper,
        )

        return AddOperationViewModel(
            factoryOfToolbarComposite = createFactoryOfAppBarComposite(),
            factoryOfMainTopComposite = createFactoryOfMainTopComposite(),
            factoryOfMainBottomComposite = BottomComposite.Factory(dispatcherProvider),
            factoryOfAnchoredBottomComposite = BottomComposite.Factory(dispatcherProvider),
            weakResources = weakResources,
            visitRegistry = createVisitRegistry(),
            model = model,
            converter = ConverterForRecentOperation(weakResources, dateFormatter),
            frequentOperationType = FrequentOperationType.PAYMENT,
        )
    }

    private fun createFactoryOfAppBarComposite() = AppBarComposite.Factory(
        dispatcherProvider = dispatcherProvider,
    )

    private fun createFactoryOfMainTopComposite() = MainTopComposite.Factory(
        dispatcherProvider = dispatcherProvider,
        weakResources = weakResources,
        currencyAmountFormatter = currencyAmountFormatter,
        dateFormatter = dateFormatter,
    )

    private fun createVisitRegistry(): VisitRegistry {
        val maxNumberAllowedById: Map<Long, Int> = mapOf(
            AddOperationViewModel.OPERATION_LIST_ID_FOR_VISIT_REGISTRY to 1,
        )
        return VisitRegistry(maxNumberAllowedById)
    }
}