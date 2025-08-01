package pe.com.scotiabank.blpm.android.client.newdashboard.edit

import androidx.lifecycle.LiveData
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.base.BaseAppearErrorMessage
import pe.com.scotiabank.blpm.android.client.base.LegacyViewModel
import pe.com.scotiabank.blpm.android.ui.list.composite.LiveHolder
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.client.util.EventWrapper
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling

class EditOperationViewModel(
    private val viewModelForType: LegacyViewModel,
    override val id: Long = randomLong(),
) : NewBaseViewModel(),
    LegacyViewModel,
    LiveHolder by viewModelForType,
    Recycling by viewModelForType
{

    override fun getLoadingV2(): LiveData<EventWrapper<Boolean>> {
        return viewModelForType.getLoadingV2()
    }

    override fun getErrorMessageV2(): LiveData<EventWrapper<BaseAppearErrorMessage>> {
        return viewModelForType.getErrorMessageV2()
    }

    override fun setUpUi(receiverOfViewModelEvents: InstanceReceiver) {
        viewModelForType.setUpUi(receiverOfViewModelEvents)
    }

    override fun receiveEvent(event: Any): Boolean = viewModelForType.receiveEvent(event)
}
