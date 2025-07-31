package pe.com.scotiabank.blpm.android.client.base

import androidx.lifecycle.LiveData
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound

interface ViewModelWithSheetDialog: LegacyViewModel {

    val liveCompoundsOfSheetDialog: LiveData<List<UiCompound<*>>>
}
