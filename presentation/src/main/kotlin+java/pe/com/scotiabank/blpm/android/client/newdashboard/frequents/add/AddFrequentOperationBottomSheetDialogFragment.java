package pe.com.scotiabank.blpm.android.client.newdashboard.frequents.add;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent;
import pe.com.scotiabank.blpm.android.client.base.receipt.ReceiptAnalyticsData;
import pe.com.scotiabank.blpm.android.client.databinding.FragmentMyListBottomSheetDialogBinding;
import pe.com.scotiabank.blpm.android.client.model.mylist.AddFrequentOperationBottomSheetDialogFragmentModel;
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway;
import pe.com.scotiabank.blpm.android.analytics.factories.receipt.mylist.ReceiptMyListFactory;
import pe.com.scotiabank.blpm.android.client.util.LogUtil;
import pe.com.scotiabank.blpm.android.analytics.factories.payment.PaymentAnalyticsConstant;

public class AddFrequentOperationBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private AddFrequentOperationBottomSheetDialogFragmentModel model;
    private AddFrequentOperationBottomSheetDialogFragmentNavigator listener;
    private ReceiptMyListFactory receiptMyListFactory;
    private AnalyticsDataGateway analyticsDataGateway;
    private ReceiptAnalyticsData receiptAnalyticsData;
    private FragmentMyListBottomSheetDialogBinding binding;

    public static AddFrequentOperationBottomSheetDialogFragment newInstance(AddFrequentOperationBottomSheetDialogFragmentModel model) {
        AddFrequentOperationBottomSheetDialogFragment dialogFragment = new
                AddFrequentOperationBottomSheetDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(AddFrequentOperationBottomSheetDialogFragmentModel.class.getName(), model);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    public AddFrequentOperationBottomSheetDialogFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddFrequentOperationBottomSheetDialogFragmentNavigator) context;
        } catch (ClassCastException e) {
            LogUtil.e("Must implement AddListListener", e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyListBottomSheetDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @NonNull
    private AddFrequentOperationBottomSheetDialogFragmentViewModel getViewModel() {
        return new ViewModelProvider(this).get(AddFrequentOperationBottomSheetDialogFragmentViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeInjector();
    }

    private void initializeInjector() {
        if (getArguments() == null) return;
        model = getArguments().getParcelable(AddFrequentOperationBottomSheetDialogFragmentModel.class.getName());
        additionalInitializer();
    }

    private void additionalInitializer() {
        analyticsDataGateway.setCurrentScreen(ReceiptMyListFactory.SCREEN_NAME_RECEIPT_POPUP_ADD_OPERATION);
        if (model == null) return;

        getViewModel().setDescription(model.getName());
        setUpObservers();
        setUpListeners();

        if (analyticsDataGateway == null || model == null || receiptMyListFactory == null) return;

        AnalyticsEvent event = receiptMyListFactory.showPopupAddFrequentPayment(
                receiptAnalyticsData.getDescription(),
                receiptAnalyticsData.getElementsNumber(),
                receiptAnalyticsData.getInstitution(),
                receiptAnalyticsData.getAmount(),
                receiptAnalyticsData.getCurrency(),
                receiptAnalyticsData.getInstallmentsNumber(),
                receiptAnalyticsData.getAuthenticationChannel(),
                receiptAnalyticsData.getTypePay(),
                receiptAnalyticsData.getTypeOriginAccount(),
                receiptAnalyticsData.getTypeService(),
                receiptAnalyticsData.getStatus()
        );
        analyticsDataGateway.sendEventV2(event);
    }

    private void setUpObservers() {
        getViewModel().getDescription().observe(getViewLifecycleOwner(), this::observeDescription);
        getViewModel().getDismiss().observe(getViewLifecycleOwner(), this::observeDismiss);
        getViewModel().getFrequent().observe(getViewLifecycleOwner(), this::observeFrequent);
        getViewModel().getSendNegativeAnalytics().observe(getViewLifecycleOwner(), this::observeSendNegativeAnalytics);
        getViewModel().getSendPositiveAnalytics().observe(getViewLifecycleOwner(), this::observeSendPositiveAnalytics);
    }

    private void observeDescription(CharSequence description) {
        binding.tvMessage.setText(description);
    }

    private void observeDismiss(boolean isDismissClicked) {
        if (!isDismissClicked) return;

        dismiss();
        getViewModel().setDismiss(false);
    }

    private void observeFrequent(boolean isFrequentClicked) {
        if (!isFrequentClicked) return;

        if (listener != null) {
            listener.onFrequentClick();
        }
        getViewModel().setFrequent(false);
    }

    private void observeSendNegativeAnalytics(boolean isGoingToSendAnalytics) {
        if (!isGoingToSendAnalytics) return;

        if (getContext() == null || analyticsDataGateway == null || model == null || receiptMyListFactory == null) return;

        sendEventOnClick(PaymentAnalyticsConstant.AT_ANOTHER_TIME);
        getViewModel().setSendNegativeAnalytics(false);
    }

    private void observeSendPositiveAnalytics(boolean isGoingToSendAnalytics) {
        if (!isGoingToSendAnalytics) return;

        if (getContext() == null || analyticsDataGateway == null || model == null || receiptMyListFactory == null) return;

        sendEventOnClick(PaymentAnalyticsConstant.YES_ADD);
        getViewModel().setSendPositiveAnalytics(false);
    }

    private void sendEventOnClick(String eventLabel) {
        AnalyticsEvent event = receiptMyListFactory.clickPopupAddFrequentPayment(
                eventLabel,
                receiptAnalyticsData.getDescription(),
                receiptAnalyticsData.getElementsNumber(),
                receiptAnalyticsData.getInstitution(),
                receiptAnalyticsData.getAmount(),
                receiptAnalyticsData.getCurrency(),
                receiptAnalyticsData.getInstallmentsNumber(),
                receiptAnalyticsData.getAuthenticationChannel(),
                receiptAnalyticsData.getTypePay(),
                receiptAnalyticsData.getTypeOriginAccount(),
                receiptAnalyticsData.getTypeService(),
                receiptAnalyticsData.getStatus()
        );
        analyticsDataGateway.sendEventV2(event);
    }

    private void setUpListeners() {
        binding.tvCancel.setOnClickListener(v -> getViewModel().onNegativeClick());
        binding.btnYes.setOnClickListener(v -> getViewModel().onPositiveClick());
    }

    public void setAnalytics(
        AnalyticsDataGateway analyticsDataGateway,
        ReceiptMyListFactory receiptMyListFactory,
        ReceiptAnalyticsData receiptAnalyticsData
    ) {
        this.analyticsDataGateway = analyticsDataGateway;
        this.receiptMyListFactory = receiptMyListFactory;
        this.receiptAnalyticsData = receiptAnalyticsData;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}