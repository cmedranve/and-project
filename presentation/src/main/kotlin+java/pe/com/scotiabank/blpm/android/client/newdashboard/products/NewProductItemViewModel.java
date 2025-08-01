package pe.com.scotiabank.blpm.android.client.newdashboard.products;

import androidx.lifecycle.MutableLiveData;
import pe.com.scotiabank.blpm.android.client.base.BaseViewModel;
import pe.com.scotiabank.blpm.android.client.util.Constant;
import pe.com.scotiabank.blpm.android.client.util.DashboardConstant;

public class NewProductItemViewModel extends BaseViewModel<ProductNavigator> {
    private NewProductModel productModel;
    public final MutableLiveData<String> name;
    public final MutableLiveData<String> amount;
    public final MutableLiveData<Integer> amountColor;
    public final MutableLiveData<Boolean> principal;
    public final MutableLiveData<Boolean> showAmount;
    public final MutableLiveData<Boolean> showLazyLoading;
    public final MutableLiveData<Boolean> showFailedAmount;
    public final MutableLiveData<Boolean> showChevron;
    public final MutableLiveData<Boolean> flagWarning;

    NewProductItemViewModel(NewProductModel productModel) {
        this.productModel = productModel;

        name = new MutableLiveData<>();
        name.setValue(productModel.getName());

        amount = new MutableLiveData<>();
        amount.setValue(productModel.isAmountHidden() ? productModel.getMasked() : productModel.getAmount());

        amountColor = new MutableLiveData<>();
        amountColor.setValue(productModel.isAmountHidden() ? productModel.getDefaultColor() : productModel.getAmountColor());

        principal = new MutableLiveData<>();
        principal.setValue(productModel.isPrincipal());

        showChevron = new MutableLiveData<>();
        showChevron.setValue(productModel.isClickable());

        showAmount = new MutableLiveData<>();

        showLazyLoading = new MutableLiveData<>();
        showFailedAmount = new MutableLiveData<>();

        flagWarning = new MutableLiveData<>();
        flagWarning.setValue(productModel.isFlagWarning());

        if (Constant.TC.equalsIgnoreCase(productModel.getProductType()) && !productModel.isAmountHidden()) {
            boolean loadingAmount = productModel.isFirstTime() || !productModel.isAmountLoaded();
            showAmount.setValue(!loadingAmount && !DashboardConstant.DASHBOARD_CREDIT_CARD_AMOUNT_FAILED.equalsIgnoreCase(productModel.getAmount()));
            showLazyLoading.setValue(loadingAmount);
            showFailedAmount.setValue(DashboardConstant.DASHBOARD_CREDIT_CARD_AMOUNT_FAILED.equalsIgnoreCase(productModel.getAmount()));
            return;
        }
        showAmount.setValue(true);
        showLazyLoading.setValue(false);
        showFailedAmount.setValue(false);
    }

    public void onProductDetailClick() {
        if (getNavigator() != null && productModel.isClickable()) {
            getNavigator().loadProductDetail(productModel);
        }
    }
}