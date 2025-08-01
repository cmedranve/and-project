package pe.com.scotiabank.blpm.android.client.newdashboard.add

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.client.base.BaseBindingFragment
import pe.com.scotiabank.blpm.android.client.base.BindingInflaterOfFragment
import pe.com.scotiabank.blpm.android.ui.list.ComposerOfAppBarAndMain
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.putAllFrom
import pe.com.scotiabank.blpm.android.ui.databinding.ActivityPortableBinding
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.MyListViewModel
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.MyListViewModelFactory
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.list.Composer
import pe.com.scotiabank.blpm.android.ui.list.SpaceMeter
import javax.inject.Inject

class AddOperationFragment : BaseBindingFragment<ActivityPortableBinding>() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: AddOperationViewModel by viewModels(
        ownerProducer = { this },
        factoryProducer = ::viewModelFactory,
    )

    @Inject
    lateinit var myListViewModelFactory: MyListViewModelFactory
    private val myListViewModel: MyListViewModel by viewModels(
        ownerProducer = ::requireActivity,
        factoryProducer = ::myListViewModelFactory,
    )

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            NavigationIntention::class,
            InstancePredicate(NavigationIntention::filterInBack),
            InstanceHandler(::handleBack)
        )
        .add(
            CarrierOfOperationListAdded::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleOperationListAdded)
        )
        .add(
            CarrierOfActivityDestination::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::goToActivityDestination)
        )
        .build()

    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val spaceMeter: SpaceMeter by lazy {
        SpaceMeter()
    }

    override fun getBindingInflater(): BindingInflaterOfFragment<ActivityPortableBinding> {
        return BindingInflaterOfFragment(ActivityPortableBinding::inflate)
    }

    override fun getLayout(): Int = R.layout.activity_portable

    override fun initializeInjector() {
        // Not required
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerOnBackPressedCallback()
        spaceMeter.register(binding)
        setUpObservers()
        viewModel.setUpUi(selfReceiver)
    }

    private fun registerOnBackPressedCallback() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                viewModel.handleOnBackClicked()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setUpObservers() {
        viewModel.getLoadingV2().observe(viewLifecycleOwner, Observer(::observeShowHideLoading))
        viewModel.getErrorMessageV2().observe(viewLifecycleOwner, Observer(::observeErrorMessage))
        ComposerOfAppBarAndMain.compose(viewLifecycleOwner, binding, viewModel)
        Composer.compose(viewLifecycleOwner, binding.rvAnchoredBottomItems, viewModel.liveAnchoredBottomCompounds)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleBack(intention: NavigationIntention) {
        popSelfFromBackStack()
    }

    private fun popSelfFromBackStack() {
        val fragmentContainerView: FragmentContainerView = activity
            ?.findViewById(R.id.fragment_container_view)
            ?: return
        parentFragmentManager.popBackStack()
        fragmentContainerView.visibility = View.GONE
    }

    private fun handleOperationListAdded(carrier: CarrierOfOperationListAdded) {
        myListViewModel.receiveEvent(carrier)
        popSelfFromBackStack()
    }

    private fun goToActivityDestination(carrier: CarrierOfActivityDestination) {
        val intent = Intent(requireActivity(), carrier.screenDestination)
            .putAllFrom(carrier)
        requireActivity().startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.recyclingState = binding.rvMainItems.layoutManager?.onSaveInstanceState()
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        spaceMeter.unregister()
        super.onDestroyView()
    }
}