package pe.com.scotiabank.blpm.android.client.newdashboard.products;

import androidx.lifecycle.MutableLiveData;

import pe.com.scotiabank.blpm.android.client.base.BaseViewModel;

public class NewProductEditionItemViewModel extends BaseViewModel<ProductNavigator> {
    public final MutableLiveData<Boolean> showEditionOption;
    public final MutableLiveData<Boolean> showAllMyAccountOption;
    public final MutableLiveData<Boolean> showHideAmountOption;

    public final MutableLiveData<Boolean> checked;

    private final NewProductEditionModel newProductEditionModel;

    NewProductEditionItemViewModel(NewProductEditionModel newProductEditionModel) {
        this.newProductEditionModel = newProductEditionModel;

        showEditionOption = new MutableLiveData<>();
        showEditionOption.setValue(newProductEditionModel.isEdit());

        showAllMyAccountOption = new MutableLiveData<>();
        showAllMyAccountOption.setValue(newProductEditionModel.isAllMyAccount());

        showHideAmountOption = new MutableLiveData<>();
        showHideAmountOption.setValue(newProductEditionModel.isHideAmount());

        checked = new MutableLiveData<>(true);
        checked.setValue(newProductEditionModel.isChecked());
    }

    public void onEditClick() {
        if (getNavigator() != null) {
            getNavigator().editProducts();
        }
    }

    public void onAllMyAccountsClick() {
        if (getNavigator() != null) {
            getNavigator().showAllMyProducts();
        }
    }

    public void onHideAmountClick() {
        boolean showAmount = !newProductEditionModel.isChecked();
        checked.setValue(showAmount);
        newProductEditionModel.setChecked(checked.getValue());

        if (getNavigator() != null) {
            getNavigator().showAmount(showAmount);
        }
    }
}