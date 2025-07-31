package pe.com.scotiabank.blpm.android.ui.bottommodal

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.bottommodal.ModalCallToActionFactory.createL2PMomentsTransferInternalModal

class BottomModalCallToActionActivity : AppCompatActivity(), ModalCallToActionInteractor {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modal)
        createL2PMomentsTransferInternalModal(this).run {
            show(supportFragmentManager, getString(R.string.example_modal_tag))
        }
    }

    override fun onInterestedClick() {
        Toast.makeText(this, getString(R.string.modal_shown), Toast.LENGTH_LONG).show()
    }

    override fun onDismissClick() {
        //Not used
    }

    override fun modalAnalytics() {
        //Not used
    }
}
