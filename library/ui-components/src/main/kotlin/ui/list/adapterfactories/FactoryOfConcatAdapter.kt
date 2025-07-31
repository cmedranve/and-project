package pe.com.scotiabank.blpm.android.ui.list.adapterfactories

import androidx.recyclerview.widget.ConcatAdapter

object FactoryOfConcatAdapter {

    @JvmStatic
    fun create(): ConcatAdapter {
        val adapterConfig: ConcatAdapter.Config = ConcatAdapter.Config.Builder()
            .setIsolateViewTypes(false)
            .setStableIdMode(ConcatAdapter.Config.StableIdMode.SHARED_STABLE_IDS)
            .build()
        return ConcatAdapter(adapterConfig)
    }
}
