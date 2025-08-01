package pe.com.scotiabank.blpm.android.client.newdashboard;

import static pe.com.scotiabank.blpm.android.analytics.factories.news.NewsFactory.PREVIOUS_SECTION_NOTIFICATIONS;
import static pe.com.scotiabank.blpm.android.client.messaging.MessagingUtilKt.getIntentNotificationsSettings;
import static pe.com.scotiabank.blpm.android.client.util.TemplatesUtil.DIGITAL_CONSENT_KEY;
import static pe.com.scotiabank.blpm.android.client.util.TemplatesUtil.OFFERS_KEY;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.scotiabank.canvascore.views.CanvasSnackbar;
import com.scotiabank.enhancements.handling.HandlingStore;
import com.scotiabank.enhancements.handling.InstanceHandler;
import com.scotiabank.enhancements.handling.InstancePredicate;
import com.scotiabank.enhancements.handling.InstanceReceiver;
import com.scotiabank.enhancements.handling.InstanceReceivingAgent;
import com.scotiabank.proofofkey.auth.utilities.BiometricUtils;
import com.scotiabank.proofofkey.auth.utilities.error.exception.PokBiometricException;
import com.scotiabank.sdk.approuting.AppRouterEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import kotlin.Unit;
import kotlin.reflect.KClass;
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway;
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent;
import pe.com.scotiabank.blpm.android.analytics.factories.biometric.fullscreen.AnalyticsBiometricFullScreenConstant;
import pe.com.scotiabank.blpm.android.analytics.factories.biometric.fullscreen.BiometricFullScreenFactory;
import pe.com.scotiabank.blpm.android.analytics.factories.newdashboard.NewDashboardFactory;
import pe.com.scotiabank.blpm.android.analytics.firebase.FirebaseAnalyticsDataGateway;
import pe.com.scotiabank.blpm.android.client.R;
import pe.com.scotiabank.blpm.android.client.app.PushOtpFlowChecker;
import pe.com.scotiabank.blpm.android.client.app.WindowExtensionsKt;
import pe.com.scotiabank.blpm.android.client.assistance.model.CoachMarkItem;
import pe.com.scotiabank.blpm.android.client.base.BaseBindingActivity;
import pe.com.scotiabank.blpm.android.client.base.BindingInflaterOfActivity;
import pe.com.scotiabank.blpm.android.client.base.approuting.NavigationEvent;
import pe.com.scotiabank.blpm.android.client.base.approuting.WebViewEvent;
import pe.com.scotiabank.blpm.android.client.base.launcheditor.EnablerOfScreenshotControl;
import pe.com.scotiabank.blpm.android.client.base.session.FilterOfSessionEvent;
import pe.com.scotiabank.blpm.android.client.base.session.SessionEvent;
import pe.com.scotiabank.blpm.android.client.biometric.enrollment.EnrollmentBiometricView;
import pe.com.scotiabank.blpm.android.client.biometric.enrollment.EnrollmentBiometricViewModel;
import pe.com.scotiabank.blpm.android.client.biometric.enrollment.EnrollmentBiometricViewModelFactory;
import pe.com.scotiabank.blpm.android.client.biometric.enrollmentV2.configurationV2.BiometricConfigurationActivityV2;
import pe.com.scotiabank.blpm.android.client.biometric.enrollmentV2.digitalkeybiometricV2.PreRegisterResponseModelV2;
import pe.com.scotiabank.blpm.android.client.biometric.otp.OtpBottomSheetDialogFragment;
import pe.com.scotiabank.blpm.android.client.databinding.ActivityNewDashboard2Binding;
import pe.com.scotiabank.blpm.android.client.eraser.confirmationbynotification.EraserConfirmationNotificationActivity;
import pe.com.scotiabank.blpm.android.client.loop2pay.selectbank.Loop2PaySelectBankSuccessDialogFragment;
import pe.com.scotiabank.blpm.android.client.medallia.setup.MedalliaFacade;
import pe.com.scotiabank.blpm.android.client.message.superaccount.MessageSuperAccountActivity;
import pe.com.scotiabank.blpm.android.client.model.FactorModel;
import pe.com.scotiabank.blpm.android.client.model.GateModel;
import pe.com.scotiabank.blpm.android.client.model.GateWrapperModel;
import pe.com.scotiabank.blpm.android.client.model.biometric.BiometricConfigurationModel;
import pe.com.scotiabank.blpm.android.client.newdashboard.finishresult.FinishResult;
import pe.com.scotiabank.blpm.android.client.newdashboard.finishresult.FinishResultViewModel;
import pe.com.scotiabank.blpm.android.client.newdashboard.gates.DashboardGateViewModel;
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.MyListViewModel;
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.MyListViewModelFactory;
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.NewMyListFragment;
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.Intention;
import pe.com.scotiabank.blpm.android.client.newdashboard.plin.IntentionPlin;
import pe.com.scotiabank.blpm.android.client.newdashboard.plin.explanation.ExplanationActivity;
import pe.com.scotiabank.blpm.android.client.newdashboard.plin.navhost.NavHostPlinFragment;
import pe.com.scotiabank.blpm.android.client.newdashboard.plin.navhost.NavHostPlinViewModel;
import pe.com.scotiabank.blpm.android.client.newdashboard.plin.navhost.NavHostPlinViewModelFactory;
import pe.com.scotiabank.blpm.android.client.newwebview.NewWebViewActivity;
import pe.com.scotiabank.blpm.android.client.p2p.setting.group.P2pSuccessfulSettingEvent;
import pe.com.scotiabank.blpm.android.client.p2p.setting.group.TextProvider;
import pe.com.scotiabank.blpm.android.client.products.dashboard.HomeFragment;
import pe.com.scotiabank.blpm.android.client.products.notice.NoticeFragment;
import pe.com.scotiabank.blpm.android.client.profilesettings.myaccount.IntentionAccount;
import pe.com.scotiabank.blpm.android.client.profilesettings.myaccount.IntentionAnalyticEvent;
import pe.com.scotiabank.blpm.android.client.profilesettings.myaccount.MyAccountFragment;
import pe.com.scotiabank.blpm.android.client.profilesettings.myaccount.MyAccountViewModel;
import pe.com.scotiabank.blpm.android.client.profilesettings.myaccount.MyAccountViewModelFactory;
import pe.com.scotiabank.blpm.android.client.tasknav.TaskNavUtilitiesKt;
import pe.com.scotiabank.blpm.android.client.templates.FeatureTemplate;
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate;
import pe.com.scotiabank.blpm.android.client.util.CoachMarkUtil;
import pe.com.scotiabank.blpm.android.client.util.Constant;
import pe.com.scotiabank.blpm.android.client.util.ContextualAssistanceConstant;
import pe.com.scotiabank.blpm.android.client.util.DateUtil;
import pe.com.scotiabank.blpm.android.client.util.GatesUtil;
import pe.com.scotiabank.blpm.android.client.util.JvmUtil;
import pe.com.scotiabank.blpm.android.client.util.ProfileTypeUtil;
import pe.com.scotiabank.blpm.android.client.util.SharedPreferencesUtil;
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil;
import pe.com.scotiabank.blpm.android.client.util.biometric.BiometricExtensions;
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.InstanceEventsKt;
import pe.com.scotiabank.blpm.android.client.util.lifecycle.LifecycleUtil;
import pe.com.scotiabank.blpm.android.client.util.loop2pay.Loop2PayConstant;
import pe.com.scotiabank.blpm.android.client.util.webview.WebViewConstant;
import pe.com.scotiabank.blpm.android.data.entity.MessageEntity;
import pe.com.scotiabank.blpm.android.ui.information.BubbleCoachMark;
import pe.com.scotiabank.blpm.android.ui.information.CoachMark;

public class NewDashboardActivity extends BaseBindingActivity<ActivityNewDashboard2Binding>
        implements Handler.HandlerListener, EnrollmentBiometricView {

    public static final String SCREEN_TAG = "DASHBOARD";

    private final List<MessageEntity> messageSuperAccountList = new ArrayList<>();

    @Inject
    DashboardViewModelFactory dashboardViewModelFactory;
    @Inject
    PersonalDashboardViewModelFactory personalDashboardViewModelFactory;
    @Inject
    NewDashboardFactory dashboardFactory;
    @Inject
    MyListViewModelFactory myListViewModelFactory;
    @Inject
    MyAccountViewModelFactory myAccountViewModelFactory;
    @Inject
    MedalliaFacade medalliaFacade;
    @Inject
    EnrollmentBiometricViewModelFactory enrollmentBiometricViewModelFactory;
    @Inject
    BiometricFullScreenFactory biometricFullScreenFactory;

    @Inject
    NavHostPlinViewModelFactory navHostPlinViewModelFactory;

    @Inject
    PushOtpFlowChecker pushOtpFlowChecker;

    private BiometricConfigurationModel configurationModel;

    private FinishResultViewModel finishResultViewModel;

    private List<Fragment> fragmentsDashboard;
    private HomeFragment homeFragment;
    private NoticeFragment noticeFragment;

    private MyAccountFragment myAccountFragment;

    private AnalyticsDataGateway analyticsDataGateway;

    @NonNull
    private final ActivityResultLauncher<Intent> launcherOfLoop2PayExplanation = registerLauncherForActivityResult(
        this::handleResultFromLoop2PayExplanation
    );

    @NonNull
    private final ActivityResultLauncher<Intent> launcherOfMessageSuperAccount = registerLauncherForActivityResult(
        this::handleResultFromMessageSuperAccount
    );

    @NonNull
    private final KClass<String> kClassOfString = JvmUtil.toKotlinClass(String.class);

    @NonNull
    private final KClass<SessionEvent> kClassOfSessionEvent = JvmUtil.toKotlinClass(SessionEvent.class);

    @NonNull
    private final KClass<P2pSuccessfulSettingEvent> kClassOfP2pSuccessfulSettingEvent = JvmUtil.toKotlinClass(
        P2pSuccessfulSettingEvent.class
    );

    @NonNull
    private final KClass<ProfileRestrictedEvent> kClassOfProfileRestrictedEvent = JvmUtil.toKotlinClass(
            ProfileRestrictedEvent.class
    );

    @NonNull
    private final HandlingStore handlingStore = new HandlingStore.Builder()
        .add(
            kClassOfP2pSuccessfulSettingEvent,
            (InstancePredicate<P2pSuccessfulSettingEvent>) InstanceEventsKt::filterInAnySubType,
            (InstanceHandler<P2pSuccessfulSettingEvent>) this::handleDisaffiliatedFromP2p
        )
        .add(
            kClassOfString,
            (InstancePredicate<String>) InstanceEventsKt::filterInAnySubType,
            (InstanceHandler<String>) this::onBiometricError
        )
        .add(
            kClassOfSessionEvent,
            (InstancePredicate<SessionEvent>) FilterOfSessionEvent::filterInLoggedOut,
            (InstanceHandler<SessionEvent>) this::handleSessionLoggedOut
        )
        .add(
            kClassOfProfileRestrictedEvent,
            (InstancePredicate<ProfileRestrictedEvent>) ProfileRestrictedEvent::filterInShowRestricted,
            (InstanceHandler<ProfileRestrictedEvent>) this::showRestrictedProfileScreen
        )
        .add(
            kClassOfProfileRestrictedEvent,
            (InstancePredicate<ProfileRestrictedEvent>) ProfileRestrictedEvent::filterInShowOpenMarket,
            (InstanceHandler<ProfileRestrictedEvent>) this::showOpenMarketProfileScreen
        )
        .build();

    @NonNull
    private final InstanceReceiver selfReceiver = new InstanceReceivingAgent(handlingStore);

    @NonNull
    @Override
    protected BindingInflaterOfActivity<ActivityNewDashboard2Binding> getBindingInflater() {
        return ActivityNewDashboard2Binding::inflate;
    }

    @NonNull
    private MyListViewModel getMyListViewModel() {
        return new ViewModelProvider(this, myListViewModelFactory).get(MyListViewModel.class);
    }

    @NonNull
    private NavHostPlinViewModel getNavHostPlinViewModel() {
        return new ViewModelProvider(this, navHostPlinViewModelFactory).get(NavHostPlinViewModel.class);
    }

    @NonNull
    private MyAccountViewModel getMyAccountViewModel() {
        return new ViewModelProvider(this, myAccountViewModelFactory).get(MyAccountViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appModel.startGlobalTimer();

        analyticsDataGateway = new FirebaseAnalyticsDataGateway(FirebaseAnalytics.getInstance(this), this);
    }

    @Override
    public boolean getSettingToolbar() {
        return false;
    }

    @NonNull
    @Override
    public String getScreenNameTag() {
        return SCREEN_TAG;
    }

    @NonNull
    private EnrollmentBiometricViewModel getEnrollmentViewModel() {
        EnrollmentBiometricViewModel viewModel = new ViewModelProvider(this, enrollmentBiometricViewModelFactory).get(EnrollmentBiometricViewModel.class);
        viewModel.setUpUi(selfReceiver, configurationModel, new WeakReference<>(this));
        return viewModel;
    }

    @Override
    protected void onDestroy() {
        medalliaFacade.disableIntercept();
        super.onDestroy();
    }

    @Override
    protected void additionalInitializer() {
        if (!appModel.isOpenedSession()) {
            TaskNavUtilitiesKt.clearThenNavigateToHost(getApplicationContext());
            return;
        }
        getViewModel().setUp(selfReceiver);
        binding.cloCenter.setVisibility(View.VISIBLE);
        configurationModel =
                BiometricExtensions.getParcelableFrom(getIntent(), BiometricConfigurationActivityV2.ENROLLMENT_CONFIG);

        finishResultViewModel = createFinishResultViewModel();
        DateUtil.setServerDate(appModel.getServerDate());

        setUpTemplates();

        NewDashboardPagerAdapter newDashboardPagerAdapter = new NewDashboardPagerAdapter(getSupportFragmentManager(), fragmentsDashboard);
        binding.vpDashboard.setAdapter(newDashboardPagerAdapter);
        binding.vpDashboard.setOffscreenPageLimit(4);
        binding.vpDashboard.setPagingEnabled(false);

        Handler handler = new Handler(binding.vpDashboard, fragmentsDashboard, this);
        binding.bnDashboard.setOnItemSelectedListener(handler::onNavigationClick);

        getDashboardGateViewModel().getCampaigns();
        getViewModel().showEmptyBadge();
        setUpObservers();
        getPersonalViewModel().setUp(selfReceiver);

        OptionTemplate featureFlagFullsScreenSuperAccount = TemplatesUtil.getOperation(appModel.getNavigationTemplate(), TemplatesUtil.HOME_KEY, TemplatesUtil.FULLSCREEN_SUPER_ACCOUNT);

        if (featureFlagFullsScreenSuperAccount.isVisible()) {
            getViewModel().getMessages();
        }

        medalliaFacade.setUpMedallia(this);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackEvent();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
        binding.cloCenter.setVisibility(View.GONE);
    }

    @Override
    public boolean validatePokActivation() {
        boolean status = true; // always set true to biometric
        boolean wasModified = BiometricExtensions.wasTxModified(getBaseContext(), status);
        boolean pokFullScreenWasShowed = SharedPreferencesUtil.getIsPOKFullscreenShowed(this);
        try {
            boolean existEnrollment = BiometricUtils.isDeviceWithEnrollment(this);
            if (wasModified && !pokFullScreenWasShowed && !existEnrollment) {
                return BiometricUtils.hasAvailableBiometric(this);
            }
        } catch (PokBiometricException e) {
            return false;
        }
        return false;
    }

    private FinishResultViewModel createFinishResultViewModel() {
        return new ViewModelProvider(this).get(FinishResultViewModel.class);
    }

    private void handleDisaffiliatedFromP2p(P2pSuccessfulSettingEvent event) {
        CanvasSnackbar canvasSnackBar = CanvasSnackbar.Companion.make(binding.vpDashboard);
        WeakReference<Resources> weakResources = new WeakReference<>(
            getApplicationContext().getResources()
        );
        TextProvider textProvider = new TextProvider(getAppModel(), weakResources);
        SpannableStringBuilder message = textProvider.getSnackbarMessageForDisaffiliation();
        canvasSnackBar.setMessage(message);
        canvasSnackBar.setIcon(com.scotiabank.icons.functional.R.drawable.ic_checkmark_default_white_18);
        canvasSnackBar.show();
    }

    private void setUpObservers() {
        getPersonalViewModel().getLoadingV2().observe(this, this::observeShowHideLoading);
        getPersonalViewModel().getErrorMessageV2().observe(this, this::observeErrorMessage);
        getViewModel().messagesSuperAccount.observe(this, this::observeGetMessages);
        getViewModel().getScotiabankDefault().observe(this, this::observeScotiabankDefault);
        getViewModel().getErrorMessageV2().observe(this, this::observeErrorMessage);
        getViewModel().getLoadingV2().observe(this, this::observeShowHideLoading);
        getEnrollmentViewModel().getLoadingV2().observe(this, this::observeShowHideLoading);
        finishResultViewModel.getFinishResultLiveData().observe(this, this::observeFinishResult);
        getEnrollmentViewModel().getOtpSent().observe(this, this::showOtpPrompt);
        getEnrollmentViewModel().getSynchronizeEnrollment().observe(this, this::synchronizeResult);
        getViewModel().showDashboardBanner.observe(this, this::showEmptyBadge);
        getDashboardGateViewModel().getNotificationsGateWrapperModelMutableLiveData().observe(this, this::observeGateWrapperModel);
    }

    private void showEmptyBadge(boolean showBanner) {
        int indexNews = getFragmentIndexOfNews();
        if (indexNews != Constant.INVALID_INDEX && noticeFragment != null && !getViewModel().isCampaignsVisible()) {
            binding.bnDashboard.setBadge(getFragmentIndexOfNews(), Constant.ZERO);
        }
    }

    private void showOtpPrompt(boolean otpSent) {
        getViewModel().setLoadingV2(false);
        if (!otpSent) return;
        getEnrollmentViewModel().setLoadingV2(false);
        boolean status = true;// always set true to biometric
        configurationModel.setLoginEnabled(status);
        configurationModel.setTxEnabled(status);
        showBottomSheet();
    }

    public void enrollBiometricTx() {
        getViewModel().setLoadingV2(true);
        getEnrollmentViewModel().sendOtp();
    }

    private void onBiometricError(String message) {
        getEnrollmentViewModel().cleanViewModel();
        getEnrollmentViewModel().onDismissOtpDialog(true);
    }

    @Override
    public void showBiometricPrompt(@Nullable PreRegisterResponseModelV2 model) {
        if (model != null) {
            getEnrollmentViewModel().showBiometricPrompt(model);
        }
    }

    private void showBottomSheet() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(OtpBottomSheetDialogFragment.CONFIG_MODEL, configurationModel);
        bundle.putString(OtpBottomSheetDialogFragment.FUNC_TYPE, AnalyticsBiometricFullScreenConstant.FunctionType.CONFIRM_OPERATIONS);
        bundle.putString(OtpBottomSheetDialogFragment.PREVIOUS_SECTION, OtpBottomSheetDialogFragment.DARK);
        OtpBottomSheetDialogFragment bottomSheetDialogFragment = OtpBottomSheetDialogFragment.Companion.newInstance(bundle);
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    private void synchronizeResult(Boolean status) {
        getEnrollmentViewModel().setLoadingV2(false);
        if (status) {
            BiometricExtensions.saveCurrentEnrollment(this, configurationModel);
            SharedPreferencesUtil.setEnrollWithNewPOKVersion(this, true);
            String message = getString(R.string.message_fingerprint_enabled_tx);
            String functionalType = AnalyticsBiometricFullScreenConstant.FunctionType.CONFIRM_OPERATIONS;
            String analyticsMessage = AnalyticsBiometricFullScreenConstant.Message.CONFIRM_OPERATIONS;
            String previousSection = OtpBottomSheetDialogFragment.DARK;
            youActivatedTxBiometricCorrectly(message, functionalType, analyticsMessage, previousSection);
        } else {
            showMessageDialog(
                    getString(R.string.biometric_prompt_enrollment_synchronize_error),
                    getString(R.string.biometric_title)
            );
            getEnrollmentViewModel().clearBiometric();
        }
        getEnrollmentViewModel().updateProfile();
    }

    private void observeGateWrapperModel(@NonNull GateWrapperModel gateWrapperModel) {
        FeatureTemplate offersFeatureTemplate = TemplatesUtil.getFeature(appModel.getNavigationTemplate(), OFFERS_KEY);
        boolean isDigitalConsentVisible = TemplatesUtil.getOperation(offersFeatureTemplate, DIGITAL_CONSENT_KEY).isVisible();
        if (appModel.getProfile().getClient() != null) {
            if (isDigitalConsentVisible && !appModel.getProfile().getClient().isAcceptedDataProtection()) return;
        }
        GateModel notificationsGate = GatesUtil.getGate(gateWrapperModel, Constant.GATE_CONTEXT_NOTIFICATIONS);
        if (noticeFragment == null || notificationsGate == null) return;
        int badgeCount = getNotificationsGateModelSize(notificationsGate);
        badgeCount += emailFactorVerified() ? 0 : 1;
        int indexNews = getFragmentIndexOfNews();
        if (indexNews != Constant.INVALID_INDEX && noticeFragment != null) {
            binding.bnDashboard.setBadge(getFragmentIndexOfNews(), badgeCount);
        }
    }

    private void showRestrictedProfileScreen(@NonNull ProfileRestrictedEvent event) {
        SharedPreferencesUtil.setRestrictedAlertViewed(this, true);
        showDisabledAlert(TemplatesUtil.DISABLED_RP);
    }

    private void showOpenMarketProfileScreen(@NonNull ProfileRestrictedEvent event) {
        SharedPreferencesUtil.setOpenMarketAlertViewed(this,
                true);
        showDisabledAlert(TemplatesUtil.DISABLED_OM);
    }

    private void setUpTemplates() {
        FeatureTemplate featureTemplate = TemplatesUtil.getFeature(appModel.getNavigationTemplate(), TemplatesUtil.NAVIGATION_KEY);

        fragmentsDashboard = new ArrayList<>();
        if (TemplatesUtil.getOperation(featureTemplate, TemplatesUtil.HOME_KEY).isVisible()) {
            boolean isQrDeepLink = getIntent().getBooleanExtra(Constant.QR_DEEPLINK, false);
            homeFragment = HomeFragment.newInstance(isQrDeepLink);
            fragmentsDashboard.add(homeFragment);
            addBottomNavigationOption(R.id.navigation_home, R.string.tab_home, R.drawable.ic_tab_home);
        }
        if (TemplatesUtil.getOperation(featureTemplate, TemplatesUtil.MY_LIST_KEY).isVisible()) {
            NewMyListFragment myListFragment = new NewMyListFragment();
            fragmentsDashboard.add(myListFragment);
            addBottomNavigationOption(R.id.navigation_my_list, R.string.tab_my_list, R.drawable.ic_tab_my_list);
        }
        if (TemplatesUtil.getOperation(featureTemplate, TemplatesUtil.CONTACTS_KEY).isVisible()) {
            fragmentsDashboard.add(new NavHostPlinFragment());
            addBottomNavigationOption(R.id.navigation_contacts, R.string.tab_plin, R.drawable.ic_tab_plin);
        }
        if (TemplatesUtil.getOperation(featureTemplate, TemplatesUtil.NOTIFICATIONS_KEY).isVisible()) {
            noticeFragment = NoticeFragment.newInstance();
            fragmentsDashboard.add(noticeFragment);
            addBottomNavigationOption(R.id.navigation_notifications, R.string.tab_notices, R.drawable.ic_tab_notices);
        }
        if (TemplatesUtil.getOperation(featureTemplate, TemplatesUtil.MORE_KEY).isVisible()) {
            myAccountFragment = new MyAccountFragment();
            fragmentsDashboard.add(myAccountFragment);
            addBottomNavigationOption(R.id.navigation_profile, R.string.tab_my_account, R.drawable.ic_tab_my_account);
        }
    }

    private void addBottomNavigationOption(@IdRes int itemId, @StringRes int titleRes, @DrawableRes int iconRes) {
        binding.bnDashboard.getMenu().add(Menu.NONE, itemId, Menu.NONE, titleRes).setIcon(iconRes);
    }

    private int getNotificationsGateModelSize(GateModel gateModel) {
        if (gateModel == null || gateModel.getOffers() == null) {
            return Constant.ZERO;
        }
        return gateModel.getOffers().size();
    }

    private boolean emailFactorVerified() {
        List<FactorModel> factors = appModel.getProfile().getFactorModels();

        for (FactorModel factor : factors) {
            if (factor == null) continue;

            String type = factor.getType();
            if (type == null) continue;

            if (Constant.EMAIL_OTP.equals(type)) {
                String emailStatus = factor.getStatus();
                return Constant.CAMS_CHANNEL_ACTIVE.equalsIgnoreCase(emailStatus);
            }
        }
        return false;
    }

    private int getFragmentIndexOfNews() {
        for (int i = 0; fragmentsDashboard != null && i < fragmentsDashboard.size(); i++) {
            if (fragmentsDashboard.get(i) instanceof NoticeFragment) {
                return i;
            }
        }

        return Constant.INVALID_INDEX;
    }

    public void validateAppearLoop2Pay() {
        AppRouterEvent nullableEvent = appModel.attemptFindRoutingEvent();
        boolean isRouting = nullableEvent != null;
        if (isRouting) return;

        final FeatureTemplate featureTemplate = TemplatesUtil.getFeature(appModel.getNavigationTemplate(), TemplatesUtil.NAVIGATION_KEY);
        final boolean isL2pVisible = TemplatesUtil.getOperation(featureTemplate, TemplatesUtil.CONTACTS_KEY).isVisible();
        final boolean isGoingToExplanation = isL2pAvailable() && isL2pVisible;
        if (isGoingToExplanation) {
            Intent intent = new Intent(this, ExplanationActivity.class)
                    .putExtra(Loop2PayConstant.NAVIGATED_FROM_LOGIN_KEY, true)
                    .putExtra(ContextualAssistanceConstant.SEND_ACTIVITY_FOR_RESULT, true);
            launcherOfLoop2PayExplanation.launch(intent);
        } else {
            getVersionCodeForWalkThrough();
        }
    }

    private boolean isL2pAvailable() {
        String loop2PayStatus = appModel.getProfile().getLoop2payStatus();

        return Loop2PayConstant.SUITABLE.equalsIgnoreCase(loop2PayStatus)
                || Loop2PayConstant.UNSUITABLE.equalsIgnoreCase(loop2PayStatus)
                || Loop2PayConstant.TRUNCATED.equalsIgnoreCase(loop2PayStatus);
    }

    private void handleResultFromLoop2PayExplanation(ActivityResult result) {
        int resultCode = result.getResultCode();
        boolean isOk = Activity.RESULT_OK == resultCode;
        if (isOk) showCoachMarkPlin();
    }

    private void getVersionCodeForWalkThrough() {
        boolean isRestricted = ProfileTypeUtil.checkRestrictedProfile(appModel.getProfile());
        if (isRestricted) return;

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName.substring(Constant.ZERO, Constant.THREE);
            validateAppearTypeWalkThrough(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            //Do Nothing
        }
    }

    private void validateAppearTypeWalkThrough(String versionName) {
        if (Constant.WHATS_NEW_WALKTROUGH_VERSION_NAME.equalsIgnoreCase(versionName) &&
                !SharedPreferencesUtil.getAppUpdated(this)) {
            SharedPreferencesUtil.setAppUpdated(this, true);
            SharedPreferencesUtil.setWhatsNewWalkthrough(this, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyOrClearSecureSurface();
        if (appModel.isRefreshNeeded() || appModel.getProfile().isAddedMyList()) {
            getDashboardGateViewModel().getCampaigns();
            getViewModel().showEmptyBadge();
            binding.bnDashboard.setSelected(Constant.ZERO);
        }
    }

    private void applyOrClearSecureSurface() {
        boolean isEnabler = appModel.getEnvironmentHolder() instanceof EnablerOfScreenshotControl;
        if (!isEnabler) return;

        EnablerOfScreenshotControl enabler = (EnablerOfScreenshotControl) appModel.getEnvironmentHolder();
        if (enabler.isScreenshotEnabled()) {
            WindowExtensionsKt.clearWindowFromSecureSurface(getWindow());
            return;
        }
        WindowExtensionsKt.applySecureSurfaceOnWindow(getWindow());
    }

    private void showCoachMarkPlin() {
        View view = findViewById(R.id.navigation_contacts);
        CoachMarkItem coachMarkItem = CoachMarkUtil.buildCoachMarkItem(
                CoachMarkUtil.LOOP_2_PAY_COACH_MARK,
                getString(R.string.l2p_txt_new_contacts),
                getString(R.string.l2p_txt_new_contacts_description),
                BubbleCoachMark.TYPE_TRY
        );
        CoachMark bubbleCoachMark = CoachMarkUtil.buildCoachMark(view,
                coachMarkItem,
                this,
                AppCompatResources.getDrawable(this, pe.com.scotiabank.blpm.android.ui.R.drawable.il_p2p_logo_42),
                view1 -> view.callOnClick()
        );
        if (bubbleCoachMark != null) {
            getWindow().getDecorView().getRootView().post(bubbleCoachMark::show);
        }
    }

    private void observeFinishResult(FinishResult finishResult) {
        boolean isLoop2PayGoToHome = finishResult.isLoop2PayGoToHome();
        boolean isCardSettingsSelected = finishResult.isCardSettingsSelected();
        navigateToMenu(isCardSettingsSelected, isLoop2PayGoToHome);
    }

    private void navigateToLoop2PayFinish(Boolean isLoop2PayGoToHome) {
        if (isLoop2PayGoToHome) navigateToHome();
        else navigateToPlin();
    }

    private void navigateToMenu(boolean isCardSettingsSelected, boolean isLoop2PayGoToHome) {
        final int myAccountFragmentIndex = fragmentsDashboard.indexOf(myAccountFragment);
        binding.bnDashboard.setSelected(Math.max(myAccountFragmentIndex, Constant.ZERO));
        if (isCardSettingsSelected) getMyAccountViewModel().receiveEvent(IntentionAccount.CARD_SETTING_SELECTED);
        navigateToLoop2PayFinish(isLoop2PayGoToHome);
    }

    public void navigateToHome() {
        final int index = fragmentsDashboard.indexOf(homeFragment);
        binding.bnDashboard.setSelected(Math.max(index, Constant.ZERO));
    }

    @NonNull
    private NewDashboardViewModel getViewModel() {
        return new ViewModelProvider(this, dashboardViewModelFactory).get(NewDashboardViewModel.class);
    }

    @NonNull
    private PersonalDashboardViewModel getPersonalViewModel() {
        return new ViewModelProvider(this, personalDashboardViewModelFactory).get(PersonalDashboardViewModel.class);
    }

    @NonNull
    private DashboardGateViewModel getDashboardGateViewModel() {
        return new ViewModelProvider(this, dashboardViewModelFactory).get(DashboardGateViewModel.class);
    }

    @Override
    protected String getToolbarTitle() {
        return Constant.EMPTY_STRING;
    }

    private void handleBackEvent() {
        showLogoutMessage();
    }

    public void showLogoutMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.action_log_out);
        builder.setCancelable(false);
        builder.setMessage(R.string.logout_message);
        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            appModel.receiveEvent(IntentionAnalyticEvent.POP_UP_YES);
            getPersonalViewModel().logout();
        });
        builder.setNegativeButton(getString(R.string.no), (dialogInterface, which) ->
                appModel.receiveEvent(IntentionAnalyticEvent.POP_UP_NOT)
        );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.hasExtra(Constant.PAGE)) {
            int resourceId = intent.getIntExtra(Constant.PAGE, Constant.ZERO);
            if (resourceId != Constant.ZERO) binding.bnDashboard.setSelectedItemId(resourceId);
        }
    }

    private void shouldNavigateToHome() {
        if (binding.bnDashboard.getSelectedItemId() != R.id.navigation_home) {
            binding.bnDashboard.setSelectedItemId(R.id.navigation_home);
        }
    }

    public void openGate(Uri uri, String url) {
        Intent intent = new Intent(this, NewWebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(WebViewConstant.URL_KEY, url);
        String previousSection = uri.getQueryParameter(WebViewConstant.ANALYTICS_PREV_SECTION_VALUE_DEEPLINK);
        String previousSectionDetail = uri.getQueryParameter(WebViewConstant.ANALYTICS_PREV_SECTION_DETAIL_VALUE_DEEPLINK);
        bundle.putString(WebViewConstant.PARAM_PREVIOUS_SECTION, previousSection);
        bundle.putString(WebViewConstant.PARAM_PREVIOUS_SECTION_DETAIL, previousSectionDetail);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void handleSessionLoggedOut(@NonNull SessionEvent event) {
        LifecycleUtil.runOnResume(this, this::navigateToHost);
    }

    private void navigateToHost() {
        TaskNavUtilitiesKt.clearThenNavigateToHost(getApplicationContext());
    }

    @Override
    public void onNavigationItemSelected(int idLabel) {

        String menuName = pickNameForMenu(idLabel);

        if (R.id.navigation_my_list == idLabel) {
            getMyListViewModel().receiveEvent(Intention.NOTIFY_CLICK_ON_MY_LIST_TAB);
        }

        if (idLabel == R.id.navigation_contacts) {
            getNavHostPlinViewModel().receiveEvent(IntentionPlin.NOTIFY_CLICK_ON_PLIN_TAB);
        }

        if (R.id.navigation_notifications == idLabel) {
            setupNavigationToNotifications();
        }

        if (R.id.navigation_profile == idLabel) {
            getMyAccountViewModel().receiveEvent(IntentionAccount.NOTIFY_CLICK_ON_MY_ACCOUNT_TAB);
        }

        sendMenuAnalytics(menuName);
        if (idLabel == R.id.navigation_notifications) {
            noticeFragment.whenLoadingTheScreen();
            if (appModel.getFbEventLogger() != null) {
                appModel.getFbEventLogger().logEvent(FacebookEventName.NOTICES.getEventValue());
            }
        }
    }

    private String pickNameForMenu(int idLabel) {
        if (R.id.navigation_home == idLabel) return NewDashboardFactory.START_MENU;
        if (R.id.navigation_my_list == idLabel) return NewDashboardFactory.MY_LIST_MENU;
        if (R.id.navigation_notifications == idLabel) return NewDashboardFactory.NEWS_MENU;
        if (R.id.navigation_profile == idLabel) return NewDashboardFactory.MY_ACCOUNT_MENU;
        return Constant.HYPHEN_STRING;
    }

    private void setupNavigationToNotifications() {
        if (getFragmentIndexOfNews() != Constant.INVALID_INDEX) {
            binding.bnDashboard.removeBadge(getFragmentIndexOfNews());
        }
        if (noticeFragment == null) {
            return;
        }
        noticeFragment.menuItemClickListener();
    }

    private void sendMenuAnalytics(@NonNull String name) {
        analyticsDataGateway.setCurrentScreen(NewDashboardFactory.SCREEN_NAME);
        AnalyticsEvent event = dashboardFactory.createBottomMenuEvent(name);
        analyticsDataGateway.sendEventV2(event);
    }

    public boolean validateAppearLoop2PayCoachmark() {
        return binding.bnDashboard.getSelectedItemId() != R.id.navigation_contacts;
    }

    private void observeGetMessages(List<MessageEntity> messageEntityList) {
        messageSuperAccountList.addAll(messageEntityList);
        validateMessagesSuperAccount();
    }

    private void validateMessagesSuperAccount() {
        if (!messageSuperAccountList.isEmpty()) {
            MessageEntity messageEntity = messageSuperAccountList.get(Constant.ZERO);
            messageSuperAccountList.remove(messageEntity);
            showFullScreenWinnerSuperAccount(messageEntity);
        }
    }

    private void showFullScreenWinnerSuperAccount(MessageEntity messageEntity) {
        Intent intent = new Intent(this, MessageSuperAccountActivity.class)
                .putExtra(MessageSuperAccountActivity.EXTRA_KEY_MESSAGE, messageEntity);
        launcherOfMessageSuperAccount.launch(intent);
    }

    private void handleResultFromMessageSuperAccount(ActivityResult result) {
        int resultCode = result.getResultCode();
        boolean isOk = Activity.RESULT_OK == resultCode;
        if (isOk) validateMessagesSuperAccount();
    }

    public void youActivatedTxBiometricCorrectly(String message, String functionalType, String analyticsMessage, String previousSection) {
        sendOnSuccessEvent(functionalType, analyticsMessage, previousSection);
        showSnackbar(message);
    }

    private void sendOnSuccessEvent(String functionalType, String analyticsMessage, String previousSection) {
        AnalyticsEvent event = biometricFullScreenFactory.createOnSuccessBiometricFullscreenEvent(functionalType, analyticsMessage, previousSection);
        analyticsDataGateway.sendEventV2(event);
    }

    private void showSnackbar(String message) {
        View view = binding.getRoot();
        CanvasSnackbar canvasSnackbar = CanvasSnackbar.Companion.make(view);
        canvasSnackbar.setMessage(message);
        canvasSnackbar.show();
    }

    private void observeScotiabankDefault(boolean isDefault) {
        if (isDefault) {
            Loop2PaySelectBankSuccessDialogFragment dialogFragment = Loop2PaySelectBankSuccessDialogFragment.newInstance(false);
            dialogFragment.show(getSupportFragmentManager(), dialogFragment.getTag());
        }
    }

    @Override
    public void handleRoutingEvent(@NonNull AppRouterEvent event) {
        shouldNavigateToHome();
        if (event instanceof WebViewEvent webViewEvent) {
            handleWebViewEvent(webViewEvent);
            return;
        }

        if (event instanceof NavigationEvent navEvent) {
            handleNavigationEvent(navEvent);
        }
    }

    private void handleWebViewEvent(@NonNull WebViewEvent event) {
        Uri uri = Uri.parse(event.getUrl());
        String path = uri.getQueryParameter(Constant.QUERY_PARAM_WEB_VIEW_PATH);
        String gateUrl = GatesUtil.getGateUrl(getAppModel(), path);
        openGate(uri, gateUrl);
    }

    private void handleNavigationEvent(@NonNull NavigationEvent event) {
        NavigationEvent.Destination destination = event.getDestination();

        boolean isTargetingHome = isTargetingHome(destination);
        if (isTargetingHome) {
            navigateToHome();
            return;
        }

        if (NavigationEvent.Destination.PLIN == destination) {
            navigateToPlin();
            return;
        }

        if (NavigationEvent.Destination.MY_LIST == destination) {
            navigateToMyList();
            return;
        }

        if (NavigationEvent.Destination.PURCHASE_ERASER == destination) {
            navigateToPurchaseEraser(event.getUri());
            return;
        }

        if (NavigationEvent.Destination.NOTICES == destination) {
            navigateToNotices(event.getUri());
            return;
        }

        boolean isTargetingMoreOptions = isTargetingMoreOptions(destination);
        if (isTargetingMoreOptions) {
            navigateToMoreOptions();
        }
    }

    private boolean isTargetingHome(@NonNull NavigationEvent.Destination destination) {
        return isTargetingProducts(destination) || isTargetingWant(destination);
    }

    private boolean isTargetingProducts(@NonNull NavigationEvent.Destination destination) {
        return NavigationEvent.Destination.PRODUCTS == destination
                || NavigationEvent.Destination.PRODUCTS_EDIT == destination
                || NavigationEvent.Destination.INSTALLMENT == destination;
    }

    private boolean isTargetingWant(@NonNull NavigationEvent.Destination destination) {
        return NavigationEvent.Destination.GOALS == destination ||
                NavigationEvent.Destination.EXCHANGE_MONEY == destination ||
                NavigationEvent.Destination.CARDLESS_WITHDRAWAL == destination ||
                NavigationEvent.Destination.TRANSFER_BETWEEN_MY_ACCOUNTS == destination ||
                NavigationEvent.Destination.SERVICES_INSTITUTION == destination ||
                NavigationEvent.Destination.WALLET == destination;
    }

    private boolean isTargetingMoreOptions(@NonNull NavigationEvent.Destination destination) {
        return NavigationEvent.Destination.MY_ACCOUNT == destination ||
                NavigationEvent.Destination.CARD_CONFIGURATION == destination ||
                NavigationEvent.Destination.LIMITS == destination ||
                NavigationEvent.Destination.ADVISOR == destination ||
                NavigationEvent.Destination.BIOMETRIC == destination ||
                NavigationEvent.Destination.HUB_SCOTIA_POINTS == destination ||
                NavigationEvent.Destination.NOTIFICATIONS == destination;
    }

    private void navigateToPlin() {
        binding.bnDashboard.setSelected(Constant.TAB_POSITION_PLIN);
        appModel.clearRoutingEvent();
    }

    private void navigateToMyList() {
        binding.bnDashboard.setSelected(Constant.TAB_POSITION_MY_LIST);
        appModel.clearRoutingEvent();
    }

    private void navigateToPurchaseEraser(@NonNull Uri uri) {
        String transactionKey = uri.getQueryParameter(EraserConfirmationNotificationActivity.TRANSACTION_KEY);
        if (transactionKey != null) {
            Intent intent = EraserConfirmationNotificationActivity.getCallingIntent(this, transactionKey);
            startActivity(intent);
        }
        appModel.clearRoutingEvent();
    }

    private void navigateToMoreOptions() {
        binding.bnDashboard.setSelected(Constant.TAB_POSITION_MORE_OPTIONS);
    }

    private void navigateToNotices(Uri uri) {
        String source = uri.getQueryParameter(FirebaseAnalytics.Param.SOURCE);
        noticeFragment.setPreviousSectionAnalytics(source != null ? source : PREVIOUS_SECTION_NOTIFICATIONS);
        binding.bnDashboard.setSelected(Constant.TAB_POSITION_NOTICES);
        appModel.clearRoutingEvent();
    }

    public void youActivatedNotificationsCorrectly(Boolean isFromActivate) {
        View view = binding.getRoot();
        CanvasSnackbar canvasSnackbar = CanvasSnackbar.Companion.make(view);
        canvasSnackbar.setMessage(getString(R.string.you_have_activated_notifications));
        Drawable icon = AppCompatResources.getDrawable(
                this, com.scotiabank.canvascore.R.drawable.canvascore_icon_check
        );
        canvasSnackbar.setIcon(icon);
        canvasSnackbar.show();
        getViewModel().onActivateSnackBarEvent(isFromActivate);
    }

    public void activateNotificationsInPhoneConfigs(Boolean isFromActivate) {
        String message = getString(R.string.activate_notifications_from_settings);
        String actionText = getString(R.string.fullscreen_disabled_notifications_go_configurations);
        CanvasSnackbar canvasSnackbar = CanvasSnackbar.Companion.make(binding.getRoot());
        canvasSnackbar.setMessage(message);
        canvasSnackbar.setAction(
                actionText,
                com.scotiabank.icons.illustrative.R.drawable.ic_settings_outlined_multicoloured_24,
                true,
                false);
        canvasSnackbar.setActionButtonEvent(this::goToAppNotificationSettings);
        canvasSnackbar.show();
        getViewModel().onDeactivateSnackBarEvent(isFromActivate);
    }

    private Unit goToAppNotificationSettings() {
        Intent intent = getIntentNotificationsSettings(this);
        startActivity(intent);
        return Unit.INSTANCE;
    }
}