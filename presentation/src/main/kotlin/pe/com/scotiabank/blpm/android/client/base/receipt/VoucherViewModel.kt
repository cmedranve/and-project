package pe.com.scotiabank.blpm.android.client.base.receipt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.client.model.BannerModel
import pe.com.scotiabank.blpm.android.client.model.VoucherModel

class VoucherViewModel : NewBaseViewModel() {

    private val _voucherModel = MutableLiveData<VoucherModel?>()
    val voucherModel: LiveData<VoucherModel?> = _voucherModel

    private val _bannerModel = MutableLiveData<BannerModel?>()
    val bannerModel: LiveData<BannerModel?> = _bannerModel

    private val _hideEmail = MutableLiveData<Boolean>(false)
    val hideEmail: LiveData<Boolean> = _hideEmail

    fun setupInformation(voucherModel: VoucherModel?, hideEmailSection: Boolean, bannerModel: BannerModel?) {
        _voucherModel.value = voucherModel
        _hideEmail.value = hideEmailSection
        _bannerModel.value = bannerModel
    }
}
