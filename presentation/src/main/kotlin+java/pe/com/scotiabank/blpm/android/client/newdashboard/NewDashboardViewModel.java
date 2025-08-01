package pe.com.scotiabank.blpm.android.client.newdashboard;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.scotiabank.enhancements.handling.InstanceReceiver;
import com.scotiabank.enhancements.uuid.UtilitiesKt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import pe.com.scotiabank.blpm.android.analytics.factories.commercialnotification.management.ActivateFactory;
import pe.com.scotiabank.blpm.android.analytics.factories.commercialnotification.otherdevicefound.OtherDeviceFactory;
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant;
import pe.com.scotiabank.blpm.android.client.app.AppModel;
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel;
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData;
import pe.com.scotiabank.blpm.android.client.base.subscribers.BaseSubscriber;
import pe.com.scotiabank.blpm.android.client.messaging.activate.analytic.AnalyticModel;
import pe.com.scotiabank.blpm.android.client.messaging.activate.analytic.AnalyticEvent;
import pe.com.scotiabank.blpm.android.client.model.GateWrapperModel;
import pe.com.scotiabank.blpm.android.client.p2p.setting.group.P2pSuccessfulSettingEvent;
import pe.com.scotiabank.blpm.android.client.templates.FeatureTemplate;
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate;
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil;
import pe.com.scotiabank.blpm.android.client.util.super_account.SuperAccountConstant;
import pe.com.scotiabank.blpm.android.data.domain.interactor.GetMessagesUseCase;
import pe.com.scotiabank.blpm.android.data.domain.interactor.loop2pay.Loop2PayAffiliateBankUseCase;
import pe.com.scotiabank.blpm.android.data.entity.MessageEntity;
import pe.com.scotiabank.blpm.android.data.entity.loop2pay.Loop2PayAffiliateBankRequestEntity;
import pe.com.scotiabank.blpm.android.data.entity.loop2pay.Loop2PayUpdateSettingEntity;
import pe.com.scotiabank.blpm.android.ui.list.composite.CompositeOfAppBarAndMain;
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound;
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.PortableViewModel;

public class NewDashboardViewModel extends NewBaseViewModel implements PortableViewModel {

    @NonNull
    private final AppModel appModel;

    private final GetMessagesUseCase getMessagesUseCase;
    private final Loop2PayAffiliateBankUseCase loop2PayAffiliateBankUseCase;

    public final MutableLiveData<GateWrapperModel> gateWrapperModelMutableLiveData;
    public final MutableLiveData<List<MessageEntity>> messagesSuperAccount;
    public final MutableLiveData<Boolean> showDashboardBanner;
    private final MutableLiveData<Boolean> scotiabankDefault;
    private final AnalyticModel notificationAnalyticModel;

    private final long id;

    private InstanceReceiver receiverOfViewModelEvents;

    public NewDashboardViewModel(
            @NonNull AppModel appModel,
            Loop2PayAffiliateBankUseCase loop2PayAffiliateBankUseCase,
            GetMessagesUseCase getMessagesUseCase,
            AnalyticModel notificationAnalyticModel
    ) {
        this.appModel = appModel;
        this.loop2PayAffiliateBankUseCase = loop2PayAffiliateBankUseCase;
        this.gateWrapperModelMutableLiveData = new MutableLiveData<>();
        this.getMessagesUseCase = getMessagesUseCase;
        this.messagesSuperAccount = new MutableLiveData<>();
        this.scotiabankDefault = new MutableLiveData<>();
        this.showDashboardBanner = new MutableLiveData<>(false);
        this.notificationAnalyticModel = notificationAnalyticModel;
        this.id = UtilitiesKt.randomLong();
        registerObserver(appModel);
    }

    private void registerObserver(@NonNull AppModel appModel) {
        appModel.addChild(this);
    }

    public void setUp(@NonNull InstanceReceiver receiverOfViewModelEvents) {
        this.receiverOfViewModelEvents = receiverOfViewModelEvents;
    }

    public LiveData<Boolean> getScotiabankDefault() {
        return scotiabankDefault;
    }

    public void affiliateBank(String scotiabankId) {
        setLoadingV2(true);
        Loop2PayAffiliateBankUseCase.Params params = Loop2PayAffiliateBankUseCase.Static.createParams(new Loop2PayAffiliateBankRequestEntity(scotiabankId));
        loop2PayAffiliateBankUseCase.execute(getBaseSubscriber(this::onSuccessAffiliateBankUseCase), params);
    }

    private void onSuccessAffiliateBankUseCase(Loop2PayUpdateSettingEntity loop2PayUpdateSettingEntity) {
        setLoadingV2(false);
        scotiabankDefault.setValue(true);
    }

    public Boolean isCampaignsVisible() {
        FeatureTemplate featureTemplate = TemplatesUtil.getFeature(appModel.getNavigationTemplate(), TemplatesUtil.OFFERS_KEY);
        OptionTemplate campaignsTemplate = TemplatesUtil.getOperation(featureTemplate, TemplatesUtil.CAMPAIGNS_DASH_KEY);
        if (campaignsTemplate.getName().isEmpty()) return true;
        return campaignsTemplate.isVisible();
    }

    public void showEmptyBadge() {
        if (DashboardType.BUSINESS == appModel.getDashboardType()) return;
        if (!isCampaignsVisible()) showDashboardBanner.setValue(true);
    }

    public void getMessages() {
        if (DashboardType.BUSINESS == appModel.getDashboardType()) return;
        getMessagesUseCase.execute(new BaseSubscriber<>(this::onSuccessGetMessagesUseCase, error -> {/*Not required*/}));
    }

    private void onSuccessGetMessagesUseCase(List<MessageEntity> messageEntities) {
        messagesSuperAccount.setValue(filterMessagesSuperAccount(messageEntities));
    }

    private List<MessageEntity> filterMessagesSuperAccount(List<MessageEntity> messagesSuperAccount) {
        List<MessageEntity> messageSuperAccountList = new ArrayList<>();
        for (MessageEntity messageEntity : messagesSuperAccount) {
            if (messageEntity != null && messageEntity.getType().equalsIgnoreCase(SuperAccountConstant.FULLSCREEN_FEATURE) && messageEntity.getSubtype().equalsIgnoreCase(SuperAccountConstant.SUBTYPE_SUPER_ACCOUNT)) {
                messageSuperAccountList.add(messageEntity);
            }
        }
        return messageSuperAccountList;
    }

    public void onActivateSnackBarEvent(Boolean isFromActivate) {
        Map<String, Object> data = new HashMap<>();
        if (isFromActivate) {
            data.put(AnalyticsConstant.ORIGIN_SECTION, AnalyticsConstant.SETUP);
            data.put(AnalyticsConstant.STEP, ActivateFactory.STEP);
            data.put(AnalyticsConstant.SCREEN_NAME, AnalyticsConstant.SETUP);
            data.put(AnalyticsConstant.SCREEN_CLASS_VIEW, AnalyticsConstant.SCREEN_CLASS_VIEW);
        } else {
            data.put(AnalyticsConstant.ORIGIN_SECTION, OtherDeviceFactory.ORIGIN_SECTION);
            data.put(AnalyticsConstant.STEP, OtherDeviceFactory.STEP);
            data.put(AnalyticsConstant.SCREEN_NAME, OtherDeviceFactory.ORIGIN_SECTION);
            data.put(AnalyticsConstant.SCREEN_CLASS_VIEW, OtherDeviceFactory.SCREEN_CLASS_VIEW);
        }
        data.put(AnalyticsConstant.POPUP_NAME, ActivateFactory.POPUP_ACTIVATE_NOTIFICATIONS);
        sendAnalyticEvent(AnalyticEvent.SNACK_BAR, data);
    }

    public void onDeactivateSnackBarEvent(Boolean isFromActivate) {
        Map<String, Object> data = new HashMap<>();
        if (isFromActivate) {
            data.put(AnalyticsConstant.ORIGIN_SECTION, AnalyticsConstant.SETUP);
            data.put(AnalyticsConstant.STEP, ActivateFactory.STEP);
            data.put(AnalyticsConstant.SCREEN_NAME, AnalyticsConstant.SETUP);
            data.put(AnalyticsConstant.SCREEN_CLASS_VIEW, AnalyticsConstant.SCREEN_CLASS_VIEW);
        } else {
            data.put(AnalyticsConstant.ORIGIN_SECTION, OtherDeviceFactory.ORIGIN_SECTION);
            data.put(AnalyticsConstant.STEP, OtherDeviceFactory.STEP);
            data.put(AnalyticsConstant.SCREEN_NAME, OtherDeviceFactory.ORIGIN_SECTION);
            data.put(AnalyticsConstant.SCREEN_CLASS_VIEW, OtherDeviceFactory.SCREEN_CLASS_VIEW);
        }
        data.put(AnalyticsConstant.POPUP_NAME, ActivateFactory.POPUP_DEACTIVATE_NOTIFICATIONS);
        sendAnalyticEvent(AnalyticEvent.SNACK_BAR, data);
    }

    public void sendAnalyticEvent(AnalyticEvent event, Map<String, Object> data) {
        AnalyticEventData<AnalyticEvent> eventData = new AnalyticEventData<>(event, data);
        notificationAnalyticModel.sendEvent(eventData);
    }

    @NonNull
    @Override
    public LiveData<CompositeOfAppBarAndMain> getLiveCompositeOfAppBarAndMain() {
        return new MutableLiveData<>();
    }

    @NonNull
    @Override
    public LiveData<List<UiCompound<?>>> getLiveAnchoredBottomCompounds() {
        return new MutableLiveData<>();
    }

    @NonNull
    @Override
    public LiveData<Integer> getLiveMainLoading() {
        return new MutableLiveData<>();
    }

    @NonNull
    @Override
    public LiveData<Integer> getLiveResultLoading() {
        return new MutableLiveData<>();
    }

    @Nullable
    @Override
    public Parcelable getRecyclingState() {
        return null;
    }

    @Override
    public void setRecyclingState(@Nullable Parcelable parcelable) {
        // do nothing
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean receiveEvent(@NonNull Object event) {
        boolean isDisaffiliated = P2pSuccessfulSettingEvent.ON_USER_DISAFFILIATED == event;
        if (!isDisaffiliated && !isProfileRestricted(event)) return false;
        if (receiverOfViewModelEvents == null) return false;

        return receiverOfViewModelEvents.receive(event);
    }

    private boolean isProfileRestricted(@NonNull Object event) {
        return event == ProfileRestrictedEvent.SHOW_OPEN_MARKET ||
               event == ProfileRestrictedEvent.SHOW_RESTRICTED;
    }

    @Override
    protected void onCleared() {
        appModel.removeChild(id);
        super.onCleared();
    }

    @NonNull
    @Override
    public StateFlow<Boolean> getWindowSecureFlagFlow() {
        return StateFlowKt.MutableStateFlow(false);
    }

    @NonNull
    @Override
    public StateFlow<Boolean> getMedalliaInterceptFlagFlow() {
        return StateFlowKt.MutableStateFlow(false);
    }
}