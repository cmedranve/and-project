package pe.com.scotiabank.blpm.android.client.newdashboard.products;

import android.os.Bundle;

import pe.com.scotiabank.blpm.android.client.model.FeatureEnrollmentModel;

public interface ProductNavigator {

    void loadProductDetail(NewProductModel productModel);

    void showProductDetail(
            Bundle bundle,
            String accountName,
            String accountType,
            String availableAmount,
            String currency,
            String productExpirationDateDescription
    );

    void editProducts();

    void showAllMyProducts();

    void notifyCreditCardAmount(int position, NewProductModel productModel);

    void notifyProductError(long productId);

    void setCloseAlert(String id);

    void showAmount(boolean showAmount);

    void updateAmounts(boolean showAmount);

    void goToFeatureEnrollment(FeatureEnrollmentModel featureEnrollmentModel);

    void closeEnrollmentBanner();

    void enrollBiometricTx(FeatureEnrollmentModel featureEnrollmentModel, boolean isChecked);

    void closeEnrollmentBannerAnalytics(FeatureEnrollmentModel item);

    void goToUpdateData(FeatureEnrollmentModel model);

}