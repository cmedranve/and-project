package pe.com.scotiabank.blpm.android.client.newdashboard.gates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.com.scotiabank.blpm.android.analytics.factories.AnalyticsBaseConstant
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.features.dashboard.products.analytic.AnalyticEvent
import pe.com.scotiabank.blpm.android.client.features.dashboard.products.analytic.NewProductsAnalyticModel
import pe.com.scotiabank.blpm.android.client.medallia.setup.MedalliaFacade
import pe.com.scotiabank.blpm.android.client.medallia.util.MedalliaConstants
import pe.com.scotiabank.blpm.android.client.model.GateWrapperModel
import pe.com.scotiabank.blpm.android.client.model.OfferModel
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardType
import pe.com.scotiabank.blpm.android.data.entity.gatebff.MedalliaComponentEntity
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.Constant.EMPTY_STRING
import pe.com.scotiabank.blpm.android.client.util.DateUtil.convertDateToString
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil
import pe.com.scotiabank.blpm.android.data.entity.gatebff.GateFeedbackEntity
import pe.com.scotiabank.blpm.android.data.exception.FinishedSessionException
import pe.com.scotiabank.blpm.android.data.util.Constant.ZERO
import java.util.Date

class DashboardGateViewModel(
    private val appModel: AppModel,
    private val gatesBffModel: GatesBffModel,
    private val analyticModel: NewProductsAnalyticModel
) : NewBaseViewModel() {

    private var _dashboardGateWrapperModelMutableLiveData: MutableLiveData<GateWrapperModel> =
        MutableLiveData()
    val dashboardGateWrapperModelMutableLiveData: LiveData<GateWrapperModel>
        get() = _dashboardGateWrapperModelMutableLiveData

    private var _notificationsGateWrapperModelMutableLiveData: MutableLiveData<GateWrapperModel> =
        MutableLiveData()
    val notificationsGateWrapperModelMutableLiveData: LiveData<GateWrapperModel>
        get() = _notificationsGateWrapperModelMutableLiveData

    private var offerModel: OfferModel? = null

    fun getCampaigns() {
        if (appModel.dashboardType == DashboardType.BUSINESS) return
        val isCampaignsVisible = TemplatesUtil.getOperation(
            TemplatesUtil.getFeature(
                appModel.navigationTemplate, TemplatesUtil.OFFERS_KEY
            ), TemplatesUtil.CAMPAIGNS_DASH_KEY
        ).isVisible
        if (isCampaignsVisible) getCampaignsBff()
    }

    private fun getCampaignsBff() = viewModelScope.launch {
        val isCampaignsBffVisible = TemplatesUtil.getOperation(
            TemplatesUtil.getFeature(
                appModel.navigationTemplate, TemplatesUtil.OFFERS_KEY
            ), TemplatesUtil.CAMPAIGN_BFF_KEY
        ).isVisible
        if (isCampaignsBffVisible) {
            tryGetCampaignsBff()
        } else {
            tryGetCampaigns()
        }
    }

    private suspend fun tryGetCampaignsBff() = try {
        gatesBffModel.getCampaignsBff()
        tryGetNotificationsGate()
    } catch (throwable: Throwable) {
        tryGetCampaigns()
    }

    private suspend fun tryGetCampaigns() = try {
        gatesBffModel.getCampaigns()
        tryGetNotificationsGate()
    } catch (throwable: Throwable) {
        showErrorMessage(throwable)
    }

    fun getGateBff() = viewModelScope.launch {
        val isGateBffVisible = TemplatesUtil.getOperation(
            TemplatesUtil.getFeature(
                appModel.navigationTemplate, TemplatesUtil.OFFERS_KEY
            ), TemplatesUtil.GATE_BFF_KEY
        ).isVisible
        if (isGateBffVisible) {
            tryGetDashboardGateBff()
        } else {
            tryGetDashboardGate()
        }
    }

    private suspend fun tryGetDashboardGateBff() = try {
        val result = gatesBffModel.getGatesBff(Constant.GATE_CONTEXT_DASHBOARD)
        _dashboardGateWrapperModelMutableLiveData.postValue(result)
    } catch (throwable: Throwable) {
        tryGetDashboardGate()
    }

    private suspend fun tryGetDashboardGate() = try {
        val result = gatesBffModel.getGates(Constant.GATE_CONTEXT_DASHBOARD)
        _dashboardGateWrapperModelMutableLiveData.postValue(result)
    } catch (throwable: Throwable) {
        showErrorMessage(throwable)
    }

    private suspend fun tryGetNotificationsGate() = try {
        val result = gatesBffModel.getGates(Constant.GATE_CONTEXT_NOTIFICATIONS)
        _notificationsGateWrapperModelMutableLiveData.postValue(result)
    } catch (throwable: Throwable) {
        showErrorMessage(throwable)
    }

    fun sendGateBffFeedback(
        medalliaComponentEntity: MedalliaComponentEntity?, offerModel: OfferModel
    ) = viewModelScope.launch {
        val gateFeedback = GateFeedbackEntity(
            surveyId = medalliaComponentEntity?.surveyId,
            recommendationCode = medalliaComponentEntity?.productId,
            gateOrigin = Constant.GATE_CONTEXT_DASHBOARD,
            feedbackDateTime = convertDateToString(
                Constant.YYYY_MM_DD_H_MM_SSS,
                Date(medalliaComponentEntity?.timestamp ?: ZERO.toLong())
            ),
            reasons = medalliaComponentEntity?.value
        )
        this@DashboardGateViewModel.offerModel = offerModel
        sendAnalyticEvent(AnalyticEvent.MEDALLIA_FORM_SENT, createAnalyticInfoForForm(offerModel))
        trySendGateBffFeedback(gateFeedback)
    }

    private suspend fun trySendGateBffFeedback(gateFeedback: GateFeedbackEntity) = try {
        gatesBffModel.sendGateBffFeedback(gateFeedback)
    } catch (throwable: Throwable) {
        if (throwable is FinishedSessionException) {
            showErrorMessage(throwable)
        } else {
            setLoadingV2(false)
        }
    }

    private fun createAnalyticInfoForForm(offerModel: OfferModel) = mutableMapOf<String, Any?>(
        AnalyticsConstant.GATE_TYPE_EVENT to (offerModel.source ?: Constant.HYPHEN_STRING),
        AnalyticsConstant.PRODUCT_CODE_EVENT to (offerModel.productId ?: Constant.HYPHEN_STRING),
        AnalyticsBaseConstant.DESCRIPTION to (offerModel.description ?: Constant.HYPHEN_STRING),
        AnalyticsBaseConstant.PRODUCT_NAME to (offerModel.productDescription ?: Constant.HYPHEN_STRING)
    )

    fun setUpMedalliaParameters(offerModel: OfferModel, medalliaFacade: MedalliaFacade) {
        val parameters = HashMap<String, Any>()
        parameters[MedalliaConstants.CAMPAIGN_NAME] = offerModel.productDescription ?: EMPTY_STRING
        parameters[MedalliaConstants.CAMPAIGN_TYPE] = offerModel.productId ?: EMPTY_STRING
        medalliaFacade.sendCustomParameters(parameters)
    }

    private fun sendAnalyticEvent(event: AnalyticEvent?, data: Map<String, Any?>) {
        val eventData: AnalyticEventData<*> = AnalyticEventData<Any?>(event, data)
        analyticModel.sendEvent(eventData)
    }

}
