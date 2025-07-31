package pe.com.scotiabank.blpm.android.ui.list.adapterfactories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.scotiabank.enhancements.handling.*
import pe.com.scotiabank.blpm.android.ui.list.items.Identifiable
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.EventHandler
import java.lang.ref.WeakReference

class PortableAdapter<E: IdentifiableUiEntity<E>, B: ViewBinding> internal constructor(
    override val id: Long,
    @LayoutRes private val layoutRes: Int,
    private val bindingCallback: BindingCallbackOfItemView<B>,
    private val bindingHandler: InstanceHandler<UiEntityCarrier<E, B>>,
    internal val scrollingAgent: ScrollingAgent,
    private val stateRecyclingHandler: InstanceHandler<UiEntityCarrier<E, B>>?,
    stateRestorationPolicy: StateRestorationPolicy,
    private val viewPool: RecyclerView.RecycledViewPool,
    diffCallback: DiffUtil.ItemCallback<E>,
) : ListAdapter<E, ViewHolder<E, B>>(diffCallback), Identifiable {

    init {
        this.stateRestorationPolicy = stateRestorationPolicy
    }

    override fun getItemId(position: Int): Long = currentList[position].id

    override fun getItemViewType(position: Int): Int = layoutRes

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<E, B> {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val root: View = inflater.inflate(layoutRes, parent, false)
        val binding: B = bindingCallback.bind(root)
        return ViewHolder(binding, scrollingAgent, viewPool)
    }

    override fun onViewRecycled(holder: ViewHolder<E, B>) {
        holder.onViewRecycled()
        super.onViewRecycled(holder)
    }

    override fun onBindViewHolder(holder: ViewHolder<E, B>, position: Int) {
        holder.stateRecyclingHandler = stateRecyclingHandler
        val item: E = getItem(position)
        holder.bind(item, bindingHandler)
        item.resetChangingState()

        val isLastItem: Boolean = isLastItem(position)
        if (isLastItem.not()) return

        holder.doAfterBinding()
    }

    private fun isLastItem(position: Int): Boolean {
        val lastIndex: Int = itemCount - 1
        return lastIndex == position
    }
}

internal class DiffCallback<E: IdentifiableUiEntity<E>> : DiffUtil.ItemCallback<E>() {

    override fun areItemsTheSame(oldItem: E, newItem: E): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: E,
        newItem: E
    ): Boolean = oldItem.isHoldingTheSameContentAs(newItem)
}

class ViewHolder<E: IdentifiableUiEntity<E>, B: ViewBinding>(
    @Suppress("CanBeParameter") private val binding: B,
    private val scrollingAgent: ScrollingAgent,
    viewPool: RecyclerView.RecycledViewPool,
) : RecyclerView.ViewHolder(binding.root) {

    private val carrier: UiEntityCarrier<E, B> = UiEntityCarrier(binding, scrollingAgent.handler, viewPool)
    internal var stateRecyclingHandler: InstanceHandler<UiEntityCarrier<E, B>>? = null

    internal fun onViewRecycled() {
        stateRecyclingHandler?.handle(carrier)
    }

    internal fun bind(item: E, bindingHandler: InstanceHandler<UiEntityCarrier<E, B>>) {
        carrier.uiEntity = item
        bindingHandler.handle(carrier)
    }

    internal fun doAfterBinding() {
        scrollingAgent.weakBinding = carrier.weakBinding
        carrier.uiEntity?.id?.let(scrollingAgent::onLastItemReached)
    }
}

class UiEntityCarrier<E: IdentifiableUiEntity<E>, B: ViewBinding> internal constructor(
    binding: B,
    val scrollingEventHandler: EventHandler?,
    val viewPool: RecyclerView.RecycledViewPool,
) {

    val weakBinding: WeakReference<B> = WeakReference(binding)

    var uiEntity: E? = null
        internal set
}
