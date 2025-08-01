package pe.com.scotiabank.blpm.android.client.newdashboard.mylist

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.scotiabank.canvascore.bottomsheet.types.BodyListBottomSheetType
import com.scotiabank.canvascore.utils.dismissWithRunnable
import com.scotiabank.canvascore.views.CanvasSnackbar
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.BaseAppearErrorMessage
import pe.com.scotiabank.blpm.android.client.base.BaseBindingFragment
import pe.com.scotiabank.blpm.android.client.base.BindingInflaterOfFragment
import pe.com.scotiabank.blpm.android.ui.list.ComposerOfAppBarAndMain
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.ComposerOfBottomSheetList
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.StaticDataOfBottomSheetList
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfFragmentDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.putAllFrom
import pe.com.scotiabank.blpm.android.client.base.products.picking.IntentionPicker
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.CarrierOfProductPicked
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.ProductPicking
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.ProductPickingViewModel
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.ProductPickingViewModelFactory
import pe.com.scotiabank.blpm.android.ui.databinding.ActivityPortableBinding
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.CarrierOfOperationToEdit
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.BottomSheetIdentifier
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.DataOfDeletionDialog
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled.DataOfMaxSelectionDialog
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.util.EventWrapper
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.util.recyclerview.FactoryOfRecyclerView
import pe.com.scotiabank.blpm.android.ui.list.Composer
import pe.com.scotiabank.blpm.android.ui.list.SpaceMeter
import javax.inject.Inject

class NewMyListFragment : BaseBindingFragment<ActivityPortableBinding>(){

    @Inject
    lateinit var viewModelFactory: MyListViewModelFactory
    private val viewModel: MyListViewModel by viewModels(
        ownerProducer = ::requireActivity,
        factoryProducer = ::viewModelFactory,
    )

    @Inject
    lateinit var productPickingViewModelFactory: ProductPickingViewModelFactory
    private val productPickingViewModel: ProductPickingViewModel by viewModels(
        ownerProducer = { this },
        factoryProducer = ::productPickingViewModelFactory,
    )

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            CarrierOfFragmentDestination::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::addFragment)
        )
        .add(
            DataOfMaxSelectionDialog::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::showDialogOnMaxSelection)
        )
        .add(
            StaticDataOfBottomSheetList::class,
            InstancePredicate(::filterInSubMenu),
            InstanceHandler(::showSubMenu)
        )
        .add(
            CarrierOfOperationToEdit::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnEdit)
        )
        .add(
            DataOfDeletionDialog::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnDelete)
        )
        .add(
            Boolean::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::onAmountButtonClicked)
        )
        .add(
            StaticDataOfBottomSheetList::class,
            InstancePredicate(::filterInProductPicking),
            InstanceHandler(::showProductPicker)
        )
        .add(
            CarrierOfProductPicked::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::onProductSelected)
        )
        .add(
            CarrierOfActivityDestination::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::goToActivityDestination)
        )
        .add(
            SpannableStringBuilder::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::showMessage)
        )
        .add(
            OptionTemplate::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleDisabledAlert)
        )
        .add(
            IntentionPicker::class,
            InstancePredicate(::filterInIntentionPicker),
            InstanceHandler(::handlerEnableButton)
        )
        .build()

    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val spaceMeter: SpaceMeter by lazy {
        SpaceMeter()
    }

    override fun getBindingInflater(): BindingInflaterOfFragment<ActivityPortableBinding> {
        return BindingInflaterOfFragment(ActivityPortableBinding::inflate)
    }

    override fun getLayout(): Int = pe.com.scotiabank.blpm.android.ui.R.layout.activity_portable

    override fun initializeInjector() {
        // Not required
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spaceMeter.register(binding)
        setUpObservers()
        viewModel.setUpUi(selfReceiver)
        productPickingViewModel.setUp(selfReceiver)
        productPickingViewModel.validateEnableButton()
    }

    private fun setUpObservers() {
        setUpLoadingObservers()
        setUpErrorObservers()
        ComposerOfAppBarAndMain.compose(viewLifecycleOwner, binding, viewModel)
        Composer.compose(viewLifecycleOwner, binding.rvAnchoredBottomItems, viewModel.liveAnchoredBottomCompounds)
    }

    fun showMessage(spannableStringBuilder: SpannableStringBuilder) {
        val canvasSnackBar = CanvasSnackbar.make(binding.root)
        canvasSnackBar.setMessage(spannableStringBuilder)
        val icon: Drawable? = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_checkmark_white)
        if (icon != null) {
            canvasSnackBar.setIcon(icon)
        }
        canvasSnackBar.show()
    }

    private fun setUpLoadingObservers() {
        val loadingObserver: Observer<EventWrapper<Boolean>> = Observer(::observeShowHideLoading)
        viewModel.getLoadingV2().observe(viewLifecycleOwner, loadingObserver)
        productPickingViewModel.getLoadingV2().observe(viewLifecycleOwner, loadingObserver)
    }

    private fun setUpErrorObservers() {
        val errorObserver: Observer<EventWrapper<BaseAppearErrorMessage>> = Observer(::observeErrorMessage)
        viewModel.getErrorMessageV2().observe(viewLifecycleOwner, errorObserver)
        productPickingViewModel.getErrorMessageV2().observe(viewLifecycleOwner, errorObserver)
    }

    @SuppressLint("CommitTransaction")
    private fun addFragment(carrier: CarrierOfFragmentDestination) {
        val fragmentContainerView: FragmentContainerView = activity
            ?.findViewById(R.id.fragment_container_view)
            ?: return

        val args: Bundle = Bundle().putAllFrom(carrier)

        parentFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                androidx.navigation.ui.R.anim.nav_default_enter_anim,
                androidx.navigation.ui.R.anim.nav_default_exit_anim,
                androidx.navigation.ui.R.anim.nav_default_pop_enter_anim,
                androidx.navigation.ui.R.anim.nav_default_pop_exit_anim,
            )
            .add(
                R.id.fragment_container_view,
                carrier.screenDestination,
                args,
            )
            .addToBackStack(carrier.tagForBackStack)
            .commitAllowingStateLoss()

        fragmentContainerView.visibility = View.VISIBLE
    }

    private fun showDialogOnMaxSelection(data: DataOfMaxSelectionDialog) {
        createDialogOnMaxSelection(data).show()
    }

    private fun createDialogOnMaxSelection(
        data: DataOfMaxSelectionDialog,
    ): AlertDialog = AlertDialog.Builder(requireContext())
        .setMessage(data.message)
        .setCancelable(data.isCancelable)
        .setPositiveButton(data.textForPositiveButton) { _: DialogInterface?, _: Int ->
            data.callbackForPositiveButton.accept(viewModel)
        }
        .setOnDismissListener { data.callbackForDismiss.accept(viewModel) }
        .create()

    private fun filterInSubMenu(
        data: StaticDataOfBottomSheetList,
    ): Boolean = BottomSheetIdentifier.SUB_MENU == data.id

    private fun showSubMenu(data: StaticDataOfBottomSheetList) {
        val rvBsItems: RecyclerView = FactoryOfRecyclerView.create(requireContext())
        ComposerOfBottomSheetList.compose(rvBsItems, childFragmentManager, data, viewModel.liveCompoundsOfSheetDialog)
    }

    private fun handleClickOnEdit(carrier: CarrierOfOperationToEdit) {
        val fragment: BodyListBottomSheetType? = childFragmentManager.findFragmentByTag(
            BodyListBottomSheetType::class.simpleName
        ) as? BodyListBottomSheetType

        fragment?.dismissWithRunnable(
            activity = requireActivity(),
            actions = { viewModel.receiveEvent(carrier) },
            delayMillis = 0L,
        )
    }

    private fun handleClickOnDelete(data: DataOfDeletionDialog) {
        val fragment: BodyListBottomSheetType? = childFragmentManager.findFragmentByTag(
            BodyListBottomSheetType::class.simpleName
        ) as? BodyListBottomSheetType

        fragment?.dismissWithRunnable(
            activity = requireActivity(),
            actions = { showDialogToConfirmDeletion(data) },
            delayMillis = 0L,
        )
    }

    private fun showDialogToConfirmDeletion(data: DataOfDeletionDialog) {
        createDialogForDeletion(data).show()
    }

    private fun createDialogForDeletion(
        data: DataOfDeletionDialog,
    ): AlertDialog = AlertDialog.Builder(requireContext())
        .setMessage(data.message)
        .setPositiveButton(data.textForPositiveButton) { _: DialogInterface?, _: Int ->
            data.callbackForPositiveButton.accept(viewModel)
        }
        .setNegativeButton(data.textForNegativeButton) { _: DialogInterface?, _: Int ->
            data.callbackForNegativeButton.accept(viewModel)
        }
        .create()

    private fun onAmountButtonClicked(isPayableWithCreditCard: Boolean) {
        productPickingViewModel.fetchProductList(isPayableWithCreditCard)
    }

    private fun filterInProductPicking(
        data: StaticDataOfBottomSheetList,
    ): Boolean = ProductPicking.IDENTIFIER == data.id

    private fun showProductPicker(data: StaticDataOfBottomSheetList) {
        val rvBsItems: RecyclerView = FactoryOfRecyclerView.create(requireContext())
        ComposerOfBottomSheetList.compose(rvBsItems, childFragmentManager, data, productPickingViewModel.liveCompounds)
        viewModel.receiveEvent(true)
    }

    private fun onProductSelected(carrier: CarrierOfProductPicked) {
        viewModel.receiveEvent(carrier)
    }

    private fun goToActivityDestination(carrier: CarrierOfActivityDestination) {
        val intent = Intent(requireActivity(), carrier.screenDestination)
            .putAllFrom(carrier)
        requireActivity().startActivity(intent)
    }

    private fun handleDisabledAlert(optionTemplate: OptionTemplate) {
        showDisabledAlert(optionTemplate.type)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.recyclingState = binding.rvMainItems.layoutManager?.onSaveInstanceState()
        super.onSaveInstanceState(outState)
    }

    private fun filterInIntentionPicker(
        data: IntentionPicker,
    ): Boolean = IntentionPicker.ENABLE_CANVAS_BUTTON == data

    @Suppress("UNUSED_PARAMETER")
    private fun handlerEnableButton(intention: IntentionPicker) {
        productPickingViewModel.changeAvailabilityRegister(true)
    }

    override fun onDestroyView() {
        spaceMeter.unregister()
        super.onDestroyView()
    }
}