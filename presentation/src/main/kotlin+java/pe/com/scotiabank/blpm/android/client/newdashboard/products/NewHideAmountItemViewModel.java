package pe.com.scotiabank.blpm.android.client.newdashboard.products;

import androidx.lifecycle.MutableLiveData;

import pe.com.scotiabank.blpm.android.client.base.BaseViewModel;

public class NewHideAmountItemViewModel extends BaseViewModel<ProductNavigator> {
    public final MutableLiveData<Boolean> checked;
    private final NewHiddenProductModel newHiddenProductModel;

    NewHideAmountItemViewModel(NewHiddenProductModel newHiddenProductModel) {
        this.newHiddenProductModel = newHiddenProductModel;

        checked = new MutableLiveData<>();
        checked.setValue(newHiddenProductModel.isChecked());
    }

    @SuppressWarnings("ConstantConditions")
    public void onHideAmountClick() {
        boolean showAmount = !newHiddenProductModel.isChecked();
        checked.setValue(showAmount);
        newHiddenProductModel.setChecked(checked.getValue());

        if (getNavigator() != null) {
            getNavigator().showAmount(showAmount);
        }
    }
}