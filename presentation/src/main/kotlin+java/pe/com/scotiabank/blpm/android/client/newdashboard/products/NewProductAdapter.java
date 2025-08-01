package pe.com.scotiabank.blpm.android.client.newdashboard.products;

import static pe.com.scotiabank.blpm.android.client.util.Constant.PUSH_NOTIFICATION;
import static pe.com.scotiabank.blpm.android.data.util.Constant.FEATURE_BIOMETRIC;
import static pe.com.scotiabank.blpm.android.data.util.Constant.TYPE_DATA_CONTACT_UPDATE;
import static pe.com.scotiabank.blpm.android.data.util.Constant.TYPE_DATA_UPDATE;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.scotiabank.canvascore.views.StatusBadge.Companion.StatusBadgeType;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import pe.com.scotiabank.blpm.android.client.R;
import pe.com.scotiabank.blpm.android.client.app.AppModel;
import pe.com.scotiabank.blpm.android.client.databinding.ItemBannerEnrollmentBinding;
import pe.com.scotiabank.blpm.android.client.databinding.ItemBannerEnrollmentBiometricBinding;
import pe.com.scotiabank.blpm.android.client.databinding.ItemBannerUpdateDataBinding;
import pe.com.scotiabank.blpm.android.client.databinding.ItemCardHubBinding;
import pe.com.scotiabank.blpm.android.client.databinding.ItemMarketplaceBinding;
import pe.com.scotiabank.blpm.android.client.features.dashboard.products.OnProductAdapter;
import pe.com.scotiabank.blpm.android.client.model.FeatureEnrollmentModel;
import pe.com.scotiabank.blpm.android.ui.util.adapter.BindableAdapter;
import pe.com.scotiabank.blpm.android.client.databinding.ItemNewAlertBinding;
import pe.com.scotiabank.blpm.android.client.databinding.ItemNewEditionBinding;
import pe.com.scotiabank.blpm.android.client.databinding.ItemNewHideAmountBinding;
import pe.com.scotiabank.blpm.android.client.databinding.ItemNewProductBinding;
import pe.com.scotiabank.blpm.android.client.model.AlertModel;
import pe.com.scotiabank.blpm.android.client.util.Constant;
import pe.com.scotiabank.blpm.android.client.util.accesibility.AccessibilityUtil;

public class NewProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BindableAdapter<Object> {

    private static final int ALERT = 1;
    private static final int PRODUCT = 2;
    private static final int EDITION = 3;
    private static final int HIDE_AMOUNT = 4;
    private static final int FEATURE_ENROLLMENT = 5;
    private static final int FEATURE_ENROLLMENT_BIOMETRIC = 6;
    private static final int FEATURE_UPDATE_DATA = 7;
    private static final int MARKETPLACE = 8;
    private static final int CARD_HUB = 9;
    private static final int OTHER = -1;
    private static boolean IS_THE_AMOUNT_HIDDEN = true;

    @NonNull
    private final AppModel appModel;

    private int lastPosition = -1;

    private ProductNavigator productNavigator;
    private final OnProductAdapter callback;
    private List<Object> objectList;

    public NewProductAdapter(
            ProductNavigator productNavigator,
            OnProductAdapter callback,
            @NonNull AppModel appModel
    ) {
        this.appModel = appModel;
        this.productNavigator = productNavigator;
        this.callback = callback;
        this.objectList = new ArrayList<>();
    }

    @Override
    public void setData(List<Object> data) {
        objectList = data;
        notifyDataSetChanged();
    }

    public void updateAlert() {
        if (objectList.get(0) instanceof AlertModel) {
            ((AlertModel) objectList.get(0)).setNick(appModel.getProfile().getClient().getUserName());
            notifyItemChanged(0);
        }

        if (objectList.get(0) instanceof FeatureEnrollmentModel) {
            ((FeatureEnrollmentModel) objectList.get(0)).setValue(false);
            notifyItemChanged(0);
        }
    }

    @Override
    public int getItemCount() {
        return this.objectList == null ? 0 : this.objectList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object object = objectList.get(position);
        if (object instanceof FeatureEnrollmentModel) {
            return getTypeEnrollment(object);
        } else if (object instanceof AlertModel) {
            return ALERT;
        } else if (object instanceof NewProductModel) {
            return PRODUCT;
        } else if (object instanceof NewProductEditionModel) {
            return EDITION;
        } else if (object instanceof NewHiddenProductModel) {
            return HIDE_AMOUNT;
        } else if (object instanceof CardHubModel) {
            return CARD_HUB;
        } else if (object instanceof MarketplaceModel) {
            return MARKETPLACE;
        } else {
            return OTHER;
        }
    }

    private int getTypeEnrollment(Object object) {
        String typeEnrollment = ((FeatureEnrollmentModel) object).getType();
        return switch (typeEnrollment) {
            case FEATURE_BIOMETRIC, PUSH_NOTIFICATION -> FEATURE_ENROLLMENT_BIOMETRIC;
            case TYPE_DATA_CONTACT_UPDATE, TYPE_DATA_UPDATE -> FEATURE_UPDATE_DATA;
            default -> FEATURE_ENROLLMENT;
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case FEATURE_ENROLLMENT:
                ItemBannerEnrollmentBinding bannerEnrollmentBinding = ItemBannerEnrollmentBinding.inflate(
                        inflater, parent, false);
                return new FeatureEnrollmentHolder(productNavigator, bannerEnrollmentBinding);
            case FEATURE_ENROLLMENT_BIOMETRIC:
                ItemBannerEnrollmentBiometricBinding bannerEnrollmentBiometricBinding = ItemBannerEnrollmentBiometricBinding.inflate(
                        inflater, parent, false);
                return new FeatureEnrollmentBiometricHolder(appModel, productNavigator, bannerEnrollmentBiometricBinding);
            case FEATURE_UPDATE_DATA:
                ItemBannerUpdateDataBinding bannerUpdateDataBinding = ItemBannerUpdateDataBinding.inflate(
                        inflater, parent, false);
                return new FeatureUpdateDataHolder(productNavigator, bannerUpdateDataBinding);
            case ALERT:
                ItemNewAlertBinding itemNewAlertBinding = ItemNewAlertBinding.inflate(
                        inflater, parent, false);
                WeakReference<? extends ProductNavigator> weakNavigator = new WeakReference<>(productNavigator);
                return new AlertViewHolder(appModel, weakNavigator, itemNewAlertBinding);
            case PRODUCT:
                ItemNewProductBinding itemNewProductBinding = DataBindingUtil.inflate(
                        inflater, R.layout.item_new_product, parent, false);
                return new ProductViewHolder(productNavigator, itemNewProductBinding);
            case CARD_HUB:
                ItemCardHubBinding itemCardHubBinding = ItemCardHubBinding.inflate(
                        inflater, parent, false);
                return new CardHubViewHolder(productNavigator, itemCardHubBinding);
            case MARKETPLACE:
                ItemMarketplaceBinding itemMarketplaceBinding = ItemMarketplaceBinding.inflate(
                        inflater, parent, false);
                return new MarketplaceViewHolder(callback, itemMarketplaceBinding);
            case EDITION:
                ItemNewEditionBinding itemNewEditionBinding = DataBindingUtil.inflate(
                        inflater, R.layout.item_new_edition, parent, false);
                return new EditionViewHolder(productNavigator, itemNewEditionBinding);
            case HIDE_AMOUNT://HIDE_AMOUNT
                ItemNewHideAmountBinding itemNewHideAmountBinding = DataBindingUtil.inflate(
                        inflater, R.layout.item_new_hide_amount, parent, false);
                return new HideAmountViewHolder(productNavigator, itemNewHideAmountBinding);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case FEATURE_ENROLLMENT:
                FeatureEnrollmentHolder featureEnrollmentHolder = (FeatureEnrollmentHolder) holder;
                featureEnrollmentHolder.onBind((FeatureEnrollmentModel) objectList.get(position));
                break;
            case FEATURE_ENROLLMENT_BIOMETRIC:
                FeatureEnrollmentBiometricHolder featureEnrollmentBiometricHolder = (FeatureEnrollmentBiometricHolder) holder;
                featureEnrollmentBiometricHolder.onBind((FeatureEnrollmentModel) objectList.get(position));
                break;
            case FEATURE_UPDATE_DATA:
                FeatureUpdateDataHolder featureUpdateDataHolder = (FeatureUpdateDataHolder) holder;
                featureUpdateDataHolder.onBind((FeatureEnrollmentModel) objectList.get(position));
                break;
            case ALERT:
                AlertViewHolder alertItemViewModel = (AlertViewHolder) holder;
                alertItemViewModel.onBind((AlertModel) objectList.get(position));
                break;
            case PRODUCT:
                ProductViewHolder productViewHolder = (ProductViewHolder) holder;
                NewProductModel productModel = (NewProductModel) objectList.get(position);
                productModel.setAmountHidden(appModel.getProfile().getPreferenceModel().isShowBalance());

                if (Constant.TC.equalsIgnoreCase(productModel.getProductType()) && (productModel.isFirstTime() || !productModel.isAmountLoaded())) {
                    productModel.setFirstTime(false);
                }

                productViewHolder.onBind(productModel);

                setAnimation(holder.itemView, position);

                break;
            case CARD_HUB:
                CardHubViewHolder cardHubViewHolder = (CardHubViewHolder) holder;
                cardHubViewHolder.onBind();
                break;
            case MARKETPLACE:
                MarketplaceViewHolder marketplaceViewHolder = (MarketplaceViewHolder) holder;
                marketplaceViewHolder.onBind();
                break;
            case EDITION:
                EditionViewHolder editionViewHolder = (EditionViewHolder) holder;
                editionViewHolder.onBind((NewProductEditionModel) objectList.get(position));

                setAnimation(holder.itemView, position);
                break;
            case HIDE_AMOUNT://HIDE_AMOUNT
                HideAmountViewHolder hideAmountViewHolder = (HideAmountViewHolder) holder;
                hideAmountViewHolder.onBind((NewHiddenProductModel) objectList.get(position));

                setAnimation(holder.itemView, position);
                break;
            default:
                break;
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.up_from_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private ProductNavigator productNavigator;
        private ItemNewProductBinding itemNewProductBinding;

        ProductViewHolder(ProductNavigator productNavigator, ItemNewProductBinding itemNewProductBinding) {
            super(itemNewProductBinding.getRoot());
            this.productNavigator = productNavigator;
            this.itemNewProductBinding = itemNewProductBinding;
        }

        void onBind(NewProductModel productModel) {
            NewProductItemViewModel itemViewModel = new NewProductItemViewModel(productModel);
            itemViewModel.setNavigator(productNavigator);
            itemNewProductBinding.setViewModel(itemViewModel);
            accessibilityForProduct(productModel, itemNewProductBinding);
            bindStatusBadgeExpirationDate(itemNewProductBinding, productModel);
        }

        private void bindStatusBadgeExpirationDate(ItemNewProductBinding itemNewProductBinding, NewProductModel productModel) {
            String statusProductType = productModel.getStatusProductType();
            if (statusProductType == null) {
                hideStatusBadgeExpirationDate(itemNewProductBinding);
                return;
            }

            StatusBadgeType statusBadgeType = getStatusBadgeType(statusProductType);
            if (statusBadgeType == null) {
                hideStatusBadgeExpirationDate(itemNewProductBinding);
                return;
            }

            String expirationDateDescription = productModel.getExpirationDateDescription();
            if (expirationDateDescription == null || expirationDateDescription.isEmpty()) {
                hideStatusBadgeExpirationDate(itemNewProductBinding);
                return;
            }

            showStatusBadgeExpirationDate(itemNewProductBinding, statusBadgeType, expirationDateDescription);
        }

        private StatusBadgeType getStatusBadgeType(String statusProductType) {
            if (StatusProductType.OVERDUE.name().equalsIgnoreCase(statusProductType)) {
                return StatusBadgeType.TYPE_ERROR;
            }
            if (StatusProductType.PREVENT.name().equalsIgnoreCase(statusProductType)) {
                return StatusBadgeType.TYPE_DEFAULT;
            }
            return null;
        }

        private void hideStatusBadgeExpirationDate(ItemNewProductBinding itemNewProductBinding) {
            itemNewProductBinding.statusBadgeExpirationDate.setVisibility(View.GONE);
            itemNewProductBinding.tvName.setEllipsize(null);
            itemNewProductBinding.tvName.setSingleLine(false);
            itemNewProductBinding.tvAmount.setEllipsize(null);
            itemNewProductBinding.tvAmount.setSingleLine(false);
        }

        private void showStatusBadgeExpirationDate(
                ItemNewProductBinding itemNewProductBinding,
                StatusBadgeType statusBadgeType,
                String expirationDateDescription
        ) {
            itemNewProductBinding.statusBadgeExpirationDate.setText(expirationDateDescription);
            itemNewProductBinding.statusBadgeExpirationDate.setType(statusBadgeType);
            itemNewProductBinding.statusBadgeExpirationDate.setVisibility(View.VISIBLE);
            itemNewProductBinding.tvName.setSingleLine();
            itemNewProductBinding.tvName.setEllipsize(TextUtils.TruncateAt.END);
            itemNewProductBinding.tvAmount.setSingleLine();
            itemNewProductBinding.tvAmount.setEllipsize(TextUtils.TruncateAt.END);
        }

        private void accessibilityForProduct(NewProductModel productModel, ItemNewProductBinding itemNewProductBinding) {
            String contentDescription = Constant.EMPTY_STRING;
            if (productModel.isFlagWarning()) {
                contentDescription = itemView.getContext().getString(R.string.accessibility_important_notice);
            }
            contentDescription = contentDescription + productModel.getName();
            if (IS_THE_AMOUNT_HIDDEN) {
                contentDescription = contentDescription
                        + Constant.SPACE_WHITE
                        + itemView.getContext().getString(R.string.accessibility_hidden_balance);
            } else {
                contentDescription = contentDescription
                        + Constant.SPACE_WHITE + productModel.getCurrencyPlusAmount();
            }
            if (productModel.isPrincipal()) {
                contentDescription = contentDescription
                        + itemView.getContext().getString(R.string.accessibility_main_account);
            }
            itemNewProductBinding.cvProduct.setContentDescription(contentDescription);
            AccessibilityUtil.actionAndDelegate(itemNewProductBinding.cvProduct,
                    Button.class,
                    itemView.getContext().getString(R.string.accessibility_see_detail));
        }
    }

    class CardHubViewHolder extends RecyclerView.ViewHolder {
        private ItemCardHubBinding itemCardHubBinding;
        private ProductNavigator productNavigator;

        CardHubViewHolder(ProductNavigator productNavigator, ItemCardHubBinding itemCardHubBinding) {
            super(itemCardHubBinding.getRoot());
            this.productNavigator = productNavigator;
            this.itemCardHubBinding = itemCardHubBinding;
        }

        void onBind() {
            setupAccessibility(itemCardHubBinding);
        }

        private void setupAccessibility(ItemCardHubBinding itemCardHubBinding) {
            itemCardHubBinding.cvHub.setOnClickListener(view -> callback.onCardHubClicked());
        }
    }

    static class EditionViewHolder extends RecyclerView.ViewHolder {
        private ItemNewEditionBinding itemNewEditionBinding;
        private ProductNavigator productNavigator;

        EditionViewHolder(ProductNavigator productNavigator, ItemNewEditionBinding itemNewEditionBinding) {
            super(itemNewEditionBinding.getRoot());
            this.productNavigator = productNavigator;
            this.itemNewEditionBinding = itemNewEditionBinding;
        }

        void onBind(NewProductEditionModel newProductEditionModel) {
            NewProductEditionItemViewModel itemViewModel = new NewProductEditionItemViewModel(
                newProductEditionModel
            );
            itemViewModel.setNavigator(productNavigator);
            setupAmountButton(itemViewModel.checked.getValue());
            setupVisibility(
                    itemViewModel.showAllMyAccountOption,
                    itemViewModel.showHideAmountOption,
                    itemViewModel.showEditionOption
            );

            itemNewEditionBinding.btnChangeAmountVisibility.setOnClickListener(v -> {
                    itemNewEditionBinding.btnChangeAmountVisibility.setEnabled(false);
                    itemViewModel.onHideAmountClick();
                }
            );

            itemNewEditionBinding.clEdit.setOnClickListener(v -> itemViewModel.onEditClick());

            itemNewEditionBinding.clAllMyAccounts.setOnClickListener(v ->
                itemViewModel.onAllMyAccountsClick()
            );

            itemNewEditionBinding.setViewModel(itemViewModel);
            setupAccessibility(itemNewEditionBinding);
        }

        private void setupVisibility(
                MutableLiveData<Boolean> showAllMyAccountOption,
                MutableLiveData<Boolean> showHideAmountOption,
                MutableLiveData<Boolean> showEditOption
        ) {
            var btnAllMyAccounts = itemNewEditionBinding.clAllMyAccounts;
            var btnHideAmount = itemNewEditionBinding.btnChangeAmountVisibility;
            var btnEditAccounts = itemNewEditionBinding.clEdit;
            var viewHide = itemNewEditionBinding.viewSpaceItems;
            btnAllMyAccounts.setVisibility(showAllMyAccountOption.getValue()? View.VISIBLE : View.INVISIBLE);
            btnHideAmount.setVisibility(showHideAmountOption.getValue()? View.VISIBLE : View.GONE);
            viewHide.setVisibility(showHideAmountOption.getValue()? View.VISIBLE : View.GONE);
            btnEditAccounts.setVisibility(showEditOption.getValue()?View.VISIBLE : View.INVISIBLE);
        }

        private void setupAmountButton(Boolean value) {
            var btnChangeAmount = itemNewEditionBinding.btnChangeAmountVisibility;
            btnChangeAmount.setEnabled(true);
            if (value) {
                btnChangeAmount.setText(R.string.show_amount);
                btnChangeAmount.setCompoundDrawablesWithIntrinsicBounds(
                        com.scotiabank.canvaspe.R.drawable.ic_show,
                        ResourcesCompat.ID_NULL,
                        ResourcesCompat.ID_NULL,
                        ResourcesCompat.ID_NULL
                );
            } else {
                btnChangeAmount.setText(R.string.hide_amount);
                btnChangeAmount.setCompoundDrawablesWithIntrinsicBounds(
                        com.scotiabank.canvaspe.R.drawable.ic_hide,
                        ResourcesCompat.ID_NULL,
                        ResourcesCompat.ID_NULL,
                        ResourcesCompat.ID_NULL
                );
            }
        }

        private void setupAccessibility(ItemNewEditionBinding itemNewEditionBinding) {
            AccessibilityUtil.setViewType(itemNewEditionBinding.clEdit, Button.class);
            AccessibilityUtil.setViewType(itemNewEditionBinding.clAllMyAccounts, Button.class);
            AccessibilityUtil.setViewType(itemNewEditionBinding.btnChangeAmountVisibility, Button.class);
        }
    }

    static class HideAmountViewHolder extends RecyclerView.ViewHolder {
        private ItemNewHideAmountBinding itemNewHideAmountBinding;
        private ProductNavigator productNavigator;

        HideAmountViewHolder(ProductNavigator productNavigator, ItemNewHideAmountBinding itemNewHideAmountBinding) {
            super(itemNewHideAmountBinding.getRoot());
            this.productNavigator = productNavigator;
            this.itemNewHideAmountBinding = itemNewHideAmountBinding;
        }

        void onBind(NewHiddenProductModel newHiddenProductModel) {
            NewHideAmountItemViewModel itemViewModel = new NewHideAmountItemViewModel(newHiddenProductModel);
            itemViewModel.setNavigator(productNavigator);
            IS_THE_AMOUNT_HIDDEN = itemViewModel.checked.getValue();
            itemNewHideAmountBinding.setViewModel(itemViewModel);
        }
    }
}