package pe.com.scotiabank.blpm.android.client.newdashboard.products;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway;
import pe.com.scotiabank.blpm.android.analytics.factories.biometric.product.BiometricProductFactory;
import pe.com.scotiabank.blpm.android.analytics.firebase.FirebaseAnalyticsDataGateway;
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant;
import pe.com.scotiabank.blpm.android.client.R;
import pe.com.scotiabank.blpm.android.client.base.BaseBindingActivity;
import pe.com.scotiabank.blpm.android.client.base.BindingInflaterOfActivity;
import pe.com.scotiabank.blpm.android.client.databinding.ActivityAllProductsBinding;
import pe.com.scotiabank.blpm.android.client.features.dashboard.products.ProductsFragment;
import pe.com.scotiabank.blpm.android.client.features.dashboard.products.ProductsViewModel;
import pe.com.scotiabank.blpm.android.client.model.FeatureEnrollmentModel;
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardViewModelFactory;
import pe.com.scotiabank.blpm.android.client.newwebview.NewWebViewActivity;
import pe.com.scotiabank.blpm.android.client.pfm.goal.display.GoalDisplayActivity;
import pe.com.scotiabank.blpm.android.client.products.dashboard.edit.EditDashboardActivity;
import pe.com.scotiabank.blpm.android.client.products.detailproducts.NewProductDetailActivity;
import pe.com.scotiabank.blpm.android.client.util.Constant;
import pe.com.scotiabank.blpm.android.client.util.DashboardConstant;
import pe.com.scotiabank.blpm.android.client.util.GatesUtil;
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil;
import pe.com.scotiabank.blpm.android.client.util.Util;
import pe.com.scotiabank.blpm.android.client.util.webview.WebViewConstant;
import pe.com.scotiabank.joy.android.data.BuildConfig;

import static pe.com.scotiabank.blpm.android.client.util.Constant.DOT;
import static pe.com.scotiabank.blpm.android.client.util.Constant.EMPTY_STRING;
import static pe.com.scotiabank.blpm.android.client.util.Constant.HYPHEN_STRING;
import static pe.com.scotiabank.blpm.android.client.util.Constant.LCD;
import static pe.com.scotiabank.blpm.android.client.util.Constant.SPACE_WHITE;

public class AllProductsActivity extends BaseBindingActivity<ActivityAllProductsBinding> implements ProductNavigator {

    private static final int TITLE = R.string.dashboard_all_my_accounts;

    public static final String PRODUCTS_KEY = "products_key";

    @Inject
    DashboardViewModelFactory dashboardViewModelFactory;

    @Inject
    BiometricProductFactory biometricProductFactory;

    AnalyticsDataGateway analyticsDataGateway;

    private List<NewProductModel> productModels;

    @NonNull
    private final ActivityResultLauncher<Intent> launcherOfEditDashboard = registerLauncherForActivityResult(
        this::handleResultFromEditDashboard
    );

    @NonNull
    private ProductsViewModel getViewModel() {
        return new ViewModelProvider(this, dashboardViewModelFactory).get(ProductsViewModel.class);
    }

    @NonNull
    @Override
    protected BindingInflaterOfActivity<ActivityAllProductsBinding> getBindingInflater() {
        return ActivityAllProductsBinding::inflate;
    }

    @Override
    protected void additionalInitializer() {
        binding.rvMyProducts.setAdapter(new NewProductAdapter(this, null, appModel));
        binding.rvMyProducts.setHasFixedSize(true);

        analyticsDataGateway = new FirebaseAnalyticsDataGateway(FirebaseAnalytics.getInstance(this), this);
        RecyclerView.ItemAnimator itemAnimator = binding.rvMyProducts.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }

        productModels = getIntent().getParcelableArrayListExtra(PRODUCTS_KEY);

        if (productModels != null) {
            getViewModel().setNavigator(this);
            setUpObservers();
            getViewModel().setProducts(productModels, new WeakReference<>(this));
            getViewModel().onAllProductsAnalyticViewEvent(productModels);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackEvent();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void setUpObservers() {
        getViewModel().getErrorMessageV2().observe(this, this::observeErrorMessage);
        getViewModel().getLoadingV2().observe(this, this::observeShowHideLoading);
        getViewModel().getProducts().observe(this, this::observeProducts);
    }

    private void observeProducts(List<Object> products) {
        if (products == null) return;

        RecyclerView.Adapter<?> adapter = binding.rvMyProducts.getAdapter();
        if (!(adapter instanceof NewProductAdapter)) return;
        NewProductAdapter newProductAdapter = (NewProductAdapter) adapter;
        newProductAdapter.setData(products);
    }

    @Override
    protected String getToolbarTitle() {
        return getString(TITLE);
    }

    @Override
    public void loadProductDetail(NewProductModel productModel) {
        String templateType = TemplatesUtil.getOperation(appModel.getNavigationTemplate(), TemplatesUtil.DASHBOARD_KEY, TemplatesUtil.PRODUCTS_KEY).getType();
        if (TemplatesUtil.isDisabled(templateType)) {
            showDisabledAlert(templateType);
            return;
        }
        if (Constant.GL.equalsIgnoreCase(productModel.getProductType())) {
            getViewModel().onClickAllProductsAnalyticEvent(
                    AnalyticsConstant.MY_GOALS,
                    HYPHEN_STRING,
                    HYPHEN_STRING,
                    HYPHEN_STRING,
                    HYPHEN_STRING,
                    HYPHEN_STRING
            );
            startActivity(GoalDisplayActivity.getCallingIntent(this));
        } else if (Constant.FM.equalsIgnoreCase(productModel.getProductType())) {
            final String url = GatesUtil.getGateUrl(getAppModel(), BuildConfig.MUTUAL_FOUND.replace(Constant.PRODUCT_ID, String.valueOf(productModel.getId())));
            launchWebView(url);
        } else if (LCD.equalsIgnoreCase(productModel.getProductType())) {
            final String autoDisburseUrl = GatesUtil.getAutoDisburseUrlBusiness(getResources());
            final String url = GatesUtil.getGateUrl(appModel, autoDisburseUrl);
            launchWebView(url);
        } else {
            getViewModel().getGatesOutsideFromDeepLink(productModel, new WeakReference<>(this));
        }

    }

    private void launchWebView(String url) {
        Intent intent = new Intent(this, NewWebViewActivity.class);
        intent.putExtra(WebViewConstant.URL_KEY, url);
        startActivity(intent);
    }

    @Override
    public void showProductDetail(
            Bundle bundle, String accountName,
            String accountType,
            String availableAmount,
            String currency,
            String productExpirationDateDescription
    ) {
        String productType = Util.removeAccents(accountName).replace(SPACE_WHITE, HYPHEN_STRING)
                .replace(DOT, EMPTY_STRING).toLowerCase();

        getViewModel().onClickAllProductsAnalyticEvent(
                AnalyticsConstant.ACCOUNT,
                availableAmount,
                Util.removeAccents(currency).toLowerCase(),
                productType,
                accountType.replace(SPACE_WHITE, HYPHEN_STRING).toLowerCase(),
                productExpirationDateDescription
        );
        Intent intent = new Intent(this, NewProductDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void editProducts() {
        String templateType = TemplatesUtil.getOperation(appModel.getNavigationTemplate(), TemplatesUtil.DASHBOARD_KEY, TemplatesUtil.PRODUCTS_EDIT_KEY).getType();
        if (TemplatesUtil.isDisabled(templateType)) {
            showDisabledAlert(templateType);
        } else {
            getViewModel().onClickAllProductsAnalyticEvent(
                    AnalyticsConstant.EDIT,
                    HYPHEN_STRING,
                    HYPHEN_STRING,
                    HYPHEN_STRING,
                    HYPHEN_STRING,
                    HYPHEN_STRING
            );
            Intent intent = new Intent(this, EditDashboardActivity.class);
            launcherOfEditDashboard.launch(intent);
        }
    }

    private void handleResultFromEditDashboard(ActivityResult result) {
        int resultCode = result.getResultCode();
        boolean isOk = Activity.RESULT_OK == resultCode;
        if (isOk) onOkResultFromEditDashboard(result);
    }

    private void onOkResultFromEditDashboard(ActivityResult result) {
        Intent data = result.getData();
        setResult(ProductsFragment.ALL_PRODUCTS_EDITED, data);
        finish();
    }

    @Override
    public void showAllMyProducts() {
        //Not required
    }

    @Override
    public void setCloseAlert(String id) {
        //Not required
    }

    @Override
    public void showAmount(boolean showAmount) {
        //Not required
    }

    @Override
    public void updateAmounts(boolean showAmount) {
        //Not required
    }

    @Override
    public void goToFeatureEnrollment(FeatureEnrollmentModel featureEnrollmentModel) {
        //Not required
    }

    @Override
    public void closeEnrollmentBanner() {
        //Not required
    }

    @Override
    public void enrollBiometricTx(FeatureEnrollmentModel featureEnrollmentModel, boolean isChecked) {
        //  Not Required
    }

    @Override
    public void closeEnrollmentBannerAnalytics(FeatureEnrollmentModel item) {
        //  Not Required
    }

    @Override
    public void goToUpdateData(FeatureEnrollmentModel model) {
        //  Not Required
    }

    @Override
    public void notifyCreditCardAmount(int position, NewProductModel productModel) {
        getViewModel().getOriginalProducts().set(position, productModel);
        binding.rvMyProducts.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void notifyProductError(long productId) {
        for (int position = 0; position < getViewModel().getOriginalProducts().size(); position++) {
            NewProductModel productModel = getViewModel().getOriginalProducts().get(position);

            if (productModel.getId() == productId) {
                productModel.setAmount(DashboardConstant.DASHBOARD_CREDIT_CARD_AMOUNT_FAILED);
                productModel.setFirstTime(false);
                productModel.setAmountLoaded(true);
                getViewModel().getOriginalProducts().set(position, productModel);
                binding.rvMyProducts.getAdapter().notifyItemChanged(position);
                break;
            }
        }
    }

    private void handleBackEvent() {
        getViewModel().onClickAllProductsAnalyticEvent(
                AnalyticsConstant.BACK,
                HYPHEN_STRING,
                HYPHEN_STRING,
                HYPHEN_STRING,
                HYPHEN_STRING,
                HYPHEN_STRING
        );
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(Constant.OPTION_DATA, (ArrayList<? extends Parcelable>) productModels);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}