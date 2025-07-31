package pe.com.scotiabank.blpm.android.ui.list

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import pe.com.scotiabank.blpm.android.ui.databinding.ActivityPortableBinding
import pe.com.scotiabank.blpm.android.ui.databinding.ActivityPortableHostBinding
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.PortableViewModel

object LoadingObserver {

    @JvmStatic
    fun observe(
        owner: LifecycleOwner,
        binding: ActivityPortableBinding,
        viewModel: PortableViewModel,
    ) {
        viewModel.liveMainLoading.observe(owner, Observer(binding.cloMain::setVisibility))
        viewModel.liveResultLoading.observe(owner, Observer(binding.cloResult::setVisibility))
    }

    @JvmStatic
    fun observe(
        owner: LifecycleOwner,
        binding: ActivityPortableHostBinding,
        viewModel: PortableViewModel,
    ) {
        viewModel.liveMainLoading.observe(owner, Observer(binding.cloMain::setVisibility))
        viewModel.liveResultLoading.observe(owner, Observer(binding.cloResult::setVisibility))
    }
}
