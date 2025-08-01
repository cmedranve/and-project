package pe.com.scotiabank.blpm.android.client.newdashboard.mylist

import androidx.lifecycle.LiveData
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.base.BaseAppearErrorMessage
import pe.com.scotiabank.blpm.android.ui.list.composite.LiveHolder
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.client.base.ViewModelWithSheetDialog
import pe.com.scotiabank.blpm.android.client.util.EventWrapper
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling

class MyListViewModel(
    private val viewModelForEnabling: ViewModelWithSheetDialog,
    override val id: Long = randomLong(),
) : NewBaseViewModel(),
    ViewModelWithSheetDialog,
    LiveHolder by viewModelForEnabling,
    Recycling by viewModelForEnabling
{

    override val liveCompoundsOfSheetDialog: LiveData<List<UiCompound<*>>> by viewModelForEnabling::liveCompoundsOfSheetDialog

    override fun getLoadingV2(): LiveData<EventWrapper<Boolean>> {
        return viewModelForEnabling.getLoadingV2()
    }

    override fun getErrorMessageV2(): LiveData<EventWrapper<BaseAppearErrorMessage>> {
        return viewModelForEnabling.getErrorMessageV2()
    }

    override fun setUpUi(receiverOfViewModelEvents: InstanceReceiver) {
        viewModelForEnabling.setUpUi(receiverOfViewModelEvents)
    }

    override fun receiveEvent(event: Any): Boolean = viewModelForEnabling.receiveEvent(event)
}