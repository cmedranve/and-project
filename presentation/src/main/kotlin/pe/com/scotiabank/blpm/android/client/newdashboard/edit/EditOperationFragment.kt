package pe.com.scotiabank.blpm.android.client.newdashboard.edit

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.client.base.BaseBindingFragment
import pe.com.scotiabank.blpm.android.client.base.BindingInflaterOfFragment
import pe.com.scotiabank.blpm.android.ui.list.ComposerOfAppBarAndMain
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.carrier.feedFrom
import pe.com.scotiabank.blpm.android.ui.databinding.ActivityPortableBinding
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.MyListViewModel
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.MyListViewModelFactory
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.list.Composer
import pe.com.scotiabank.blpm.android.ui.list.SpaceMeter
import pe.com.scotiabank.blpm.android.ui.util.KeyboardIntention
import pe.com.scotiabank.blpm.android.ui.util.hideKeyboard
import javax.inject.Inject

class EditOperationFragment : BaseBindingFragment<ActivityPortableBinding>() {

    private val creationExtras = MutableCreationExtras()
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: EditOperationViewModel by viewModels(
        ownerProducer = { this },
        extrasProducer = ::creationExtras,
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
            KeyboardIntention::class,
            InstancePredicate(KeyboardIntention::filterInHideKeyboard),
            InstanceHandler(::hideKeyboard)
        )
        .add(
            NavigationIntention::class,
            InstancePredicate(NavigationIntention::filterInBack),
            InstanceHandler(::handleBack)
        )
        .add(
            CarrierOfOperationEdited::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleEditSaved)
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
        creationExtras.feedFrom(arguments)
        spaceMeter.register(binding)
        setUpObservers()
        viewModel.setUpUi(selfReceiver)
    }

    private fun registerOnBackPressedCallback() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                viewModel.receiveEvent(NavigationIntention.BACK)
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

    @Suppress("UNUSED_PARAMETER")
    private fun hideKeyboard(intention: KeyboardIntention) {
        hideKeyboard()
        activity?.currentFocus?.clearFocus()
    }

    private fun handleEditSaved(carrier: CarrierOfOperationEdited) {
        myListViewModel.receiveEvent(carrier)
        popSelfFromBackStack()
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