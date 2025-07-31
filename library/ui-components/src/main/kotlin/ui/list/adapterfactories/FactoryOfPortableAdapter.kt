package pe.com.scotiabank.blpm.android.ui.list.adapterfactories

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity

abstract class FactoryOfPortableAdapter<E: IdentifiableUiEntity<E>, B: ViewBinding> {

    abstract val layoutRes: Int
    abstract val bindingCallback: BindingCallbackOfItemView<B>
    abstract val bindingHandler: InstanceHandler<UiEntityCarrier<E, B>>
    open val stateRecyclingHandler: InstanceHandler<UiEntityCarrier<E, B>>? = null
    open val stateRestorationPolicy: RecyclerView.Adapter.StateRestorationPolicy
        get() = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    open val diffCallback: DiffUtil.ItemCallback<E> = DiffCallback()

    fun create(
        compoundId: Long,
        scrollingAgent: ScrollingAgent,
        viewPool: RecyclerView.RecycledViewPool,
    ) : PortableAdapter<E, B> {

        val adapter: PortableAdapter<E, B> = PortableAdapter(
            id = compoundId,
            layoutRes = layoutRes,
            bindingCallback = bindingCallback,
            bindingHandler = bindingHandler,
            scrollingAgent = scrollingAgent,
            stateRecyclingHandler = stateRecyclingHandler,
            stateRestorationPolicy = stateRestorationPolicy,
            viewPool = viewPool,
            diffCallback = diffCallback,
        )
        adapter.setHasStableIds(true)
        return adapter
    }
}
