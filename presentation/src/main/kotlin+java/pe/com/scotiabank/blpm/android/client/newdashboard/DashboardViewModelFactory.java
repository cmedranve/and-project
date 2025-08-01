package pe.com.scotiabank.blpm.android.client.newdashboard;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Named;

import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway;
import pe.com.scotiabank.blpm.android.analytics.factories.SystemDataFactory;
import pe.com.scotiabank.blpm.android.analytics.factories.commercialnotification.management.ActivateFactory;
import pe.com.scotiabank.blpm.android.analytics.factories.newdashboard.products.AllMyProductsFactory;
import pe.com.scotiabank.blpm.android.analytics.factories.newdashboard.products.MyProductsFactory;
import pe.com.scotiabank.blpm.android.client.app.AppModel;
import pe.com.scotiabank.blpm.android.client.app.PushOtpFlowChecker;
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider;
import pe.com.scotiabank.blpm.android.client.collections.CollectionsMapper;
import pe.com.scotiabank.blpm.android.client.collections.CollectionsModel;
import pe.com.scotiabank.blpm.android.client.features.dashboard.products.FactoryOfFeatureEnrollment;
import pe.com.scotiabank.blpm.android.client.features.dashboard.products.ProductsViewModel;
import pe.com.scotiabank.blpm.android.client.features.dashboard.products.analytic.NewProductsAnalyticModel;
import pe.com.scotiabank.blpm.android.client.features.dashboard.products.analytics.allmyproducts.AllProductsAnalyticModel;
import pe.com.scotiabank.blpm.android.client.features.dashboard.products.analytics.myproducts.MyProductsAnalyticModel;
import pe.com.scotiabank.blpm.android.client.messaging.activate.analytic.AnalyticModel;
import pe.com.scotiabank.blpm.android.client.newdashboard.gates.DashboardGateViewModel;
import pe.com.scotiabank.blpm.android.client.newdashboard.gates.GatesBffModel;
import pe.com.scotiabank.blpm.android.client.security.NotificationSharedPreferences;
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate;
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil;
import pe.com.scotiabank.blpm.android.data.domain.interactor.AlertUseCase;
import pe.com.scotiabank.blpm.android.data.domain.interactor.GatesUseCase;
import pe.com.scotiabank.blpm.android.data.domain.interactor.GetMessagesUseCase;
import pe.com.scotiabank.blpm.android.data.domain.interactor.NewGetProductsUseCase;
import pe.com.scotiabank.blpm.android.data.domain.interactor.NewHideAmountUseCase;
import pe.com.scotiabank.blpm.android.data.domain.interactor.ProductDetailUseCase;
import pe.com.scotiabank.blpm.android.data.domain.interactor.featureenrollment.FeatureEnrollmentCloseUseCase;
import pe.com.scotiabank.blpm.android.data.domain.interactor.featureenrollment.FeatureEnrollmentMessageUseCase;
import pe.com.scotiabank.blpm.android.data.domain.interactor.loop2pay.Loop2PayAffiliateBankUseCase;
import pe.com.scotiabank.blpm.android.data.domain.interactor.messaging.NotificationVinculationUseCase;
import pe.com.scotiabank.blpm.android.data.domain.interactor.superaccount.LotteryUseCase;
import pe.com.scotiabank.blpm.android.data.net.RestCollectionsApiService;
import pe.com.scotiabank.blpm.android.data.repository.collections.CollectionsRepository;

public class DashboardViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    private final AppModel appModel;
    @NonNull
    private final WeakReference<Context> weakAppContext;

    private final NewGetProductsUseCase productUseCase;
    private final ProductDetailUseCase productDetailUseCase;

    private final GatesUseCase gatesUseCase;
    private final GatesBffModel gatesBffModel;

    private final AlertUseCase alertUseCase;

    private final NewHideAmountUseCase hideAmountUseCase;
    private final GetMessagesUseCase getMessagesUseCase;
    private final Loop2PayAffiliateBankUseCase loop2PayAffiliateBankUseCase;
    private final FeatureEnrollmentMessageUseCase featureEnrollmentMessageUseCase;
    private final FeatureEnrollmentCloseUseCase featureEnrollmentCloseUseCase;
    private final NotificationVinculationUseCase enableNotificationsUseCase;
    private final NotificationSharedPreferences notificationSharedPreferences;
    private final NewProductsAnalyticModel newProductsAnalyticModel;
    private final LotteryUseCase lotteryUseCase;
    private final AnalyticsDataGateway analyticsDataGateway;
    private final SystemDataFactory systemDataFactory;
    private final DispatcherProvider dispatcherProvider;
    private final ObjectMapper objectMapper;

    @Inject
    public DashboardViewModelFactory(
            @NonNull AppModel appModel,
            @NonNull Context appContext,
            NewGetProductsUseCase productUseCase,
            ProductDetailUseCase productDetailUseCase,
            GatesUseCase gatesUseCase,
            GatesBffModel gatesBffModel,
            AlertUseCase alertUseCase,
            NewHideAmountUseCase hideAmountUseCase,
            GetMessagesUseCase getMessagesUseCase,
            Loop2PayAffiliateBankUseCase loop2PayAffiliateBankUseCase,
            FeatureEnrollmentMessageUseCase featureEnrollmentMessageUseCase,
            FeatureEnrollmentCloseUseCase featureEnrollmentCloseUseCase,
            NotificationVinculationUseCase enableNotificationsUseCase,
            NotificationSharedPreferences notificationSharedPreferences,
            NewProductsAnalyticModel newProductsAnalyticModel,
            LotteryUseCase lotteryUseCase,
            AnalyticsDataGateway analyticsDataGateway,
            @Named("systemDataFactorySession") SystemDataFactory systemDataFactory,
            DispatcherProvider dispatcherProvider,
            ObjectMapper objectMapper
    ) {

        this.appModel = appModel;
        this.weakAppContext = new WeakReference<>(appContext);
        this.productUseCase = productUseCase;
        this.productDetailUseCase = productDetailUseCase;
        this.gatesUseCase = gatesUseCase;
        this.gatesBffModel = gatesBffModel;
        this.alertUseCase = alertUseCase;
        this.hideAmountUseCase = hideAmountUseCase;
        this.getMessagesUseCase = getMessagesUseCase;
        this.loop2PayAffiliateBankUseCase = loop2PayAffiliateBankUseCase;
        this.featureEnrollmentMessageUseCase = featureEnrollmentMessageUseCase;
        this.featureEnrollmentCloseUseCase = featureEnrollmentCloseUseCase;
        this.enableNotificationsUseCase = enableNotificationsUseCase;
        this.notificationSharedPreferences = notificationSharedPreferences;
        this.newProductsAnalyticModel = newProductsAnalyticModel;
        this.lotteryUseCase = lotteryUseCase;
        this.analyticsDataGateway = analyticsDataGateway;
        this.systemDataFactory = systemDataFactory;
        this.dispatcherProvider = dispatcherProvider;
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProductsViewModel.class)) {
            return (T) new ProductsViewModel(
                appModel,
                weakAppContext,
                productUseCase,
                productDetailUseCase,
                gatesUseCase,
                alertUseCase,
                hideAmountUseCase,
                featureEnrollmentMessageUseCase,
                featureEnrollmentCloseUseCase,
                enableNotificationsUseCase,
                notificationSharedPreferences,
                newProductsAnalyticModel,
                createMyProductsAnalyticModel(),
                createAllProductsAnalyticModel(),
                lotteryUseCase,
                createCollectionsModel(),
                createCollectionsMapper(),
                isEnabledDebtRefinanceToCall(),
                new PushOtpFlowChecker(appModel),
                new FactoryOfFeatureEnrollment()
            );
        }
        if (modelClass.isAssignableFrom(NewDashboardViewModel.class)) {
            return (T) new NewDashboardViewModel(
                appModel,
                loop2PayAffiliateBankUseCase,
                getMessagesUseCase,
                createNotificationsAnalyticModel()
            );
        }
        if (modelClass.isAssignableFrom(DashboardGateViewModel.class)) {
            return (T) new DashboardGateViewModel(
                appModel,
                gatesBffModel,
                newProductsAnalyticModel
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }

    private MyProductsAnalyticModel createMyProductsAnalyticModel() {
        MyProductsFactory analyticFactory = new MyProductsFactory(systemDataFactory);
        return new MyProductsAnalyticModel(analyticsDataGateway, analyticFactory);
    }

    private AllProductsAnalyticModel createAllProductsAnalyticModel() {
        AllMyProductsFactory analyticFactory = new AllMyProductsFactory(systemDataFactory);
        return new AllProductsAnalyticModel(analyticsDataGateway, analyticFactory);
    }

    private AnalyticModel createNotificationsAnalyticModel() {
        ActivateFactory analyticFactory = new ActivateFactory(systemDataFactory);
        return new AnalyticModel(analyticsDataGateway, analyticFactory);
    }

    private CollectionsMapper createCollectionsMapper() {
        return new CollectionsMapper();
    }

    private boolean isEnabledDebtRefinanceToCall() {
        OptionTemplate operation = TemplatesUtil.getOperation(
            appModel.getNavigationTemplate(),
            TemplatesUtil.PAYMENTS_AND_RECHARGE_KEY,
            TemplatesUtil.COLLECTIONS_REFINANCE_LOANS_KEY
        );
        return operation.isVisible();
    }

    private CollectionsModel createCollectionsModel() {
        return new CollectionsModel(
            dispatcherProvider,
            createCollectionsMapper(),
            createCollectionsRepository()
        );
    }

    private CollectionsRepository createCollectionsRepository() {
        RestCollectionsApiService api = appModel.getSessionRetrofit().create(RestCollectionsApiService.class);
        return new CollectionsRepository(api, objectMapper);
    }
}