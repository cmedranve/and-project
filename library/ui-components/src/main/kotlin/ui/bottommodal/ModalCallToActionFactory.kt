package pe.com.scotiabank.blpm.android.ui.bottommodal

import pe.com.scotiabank.blpm.android.ui.R

object ModalCallToActionFactory {
    @JvmStatic
    fun createL2PMomentsTransferInternalModal(interactor: ModalCallToActionInteractor): BottomModalCallToAction {
        return BottomModalCallToAction.Builder()
            .setIcon(R.drawable.il_p2p_logo_42)
            .setTitle(R.string.l2p_title_transfer_modal)
            .setMessage(R.string.l2p_message_transfer_modal)
            .setAction { interactor.onInterestedClick() }
            .setDismiss { interactor.onDismissClick() }
            .setOnCreateRunnable { interactor.modalAnalytics() }
            .setActionMessage(R.string.l2p_im_interested)
            .setDismissMessage(R.string.simple_not_now)
            .build()
    }

    @JvmStatic
    fun createL2PMomentsTransferOtherBankModal(interactor: ModalCallToActionInteractor): BottomModalCallToAction {
        return BottomModalCallToAction.Builder()
            .setIcon(R.drawable.il_p2p_logo_42)
            .setTitle(R.string.l2p_title_transfer_banks_modal)
            .setMessage(R.string.l2p_message_transfer_banks_modal)
            .setAction { interactor.onInterestedClick() }
            .setDismiss { interactor.onDismissClick() }
            .setOnCreateRunnable { interactor.modalAnalytics() }
            .setActionMessage(R.string.l2p_im_interested)
            .setDismissMessage(R.string.simple_not_now)
            .build()
    }

    @JvmStatic
    fun createL2PMomentsFrequentsModal(interactor: ModalCallToActionInteractor): BottomModalCallToAction {
        return BottomModalCallToAction.Builder()
            .setIcon(R.drawable.il_p2p_logo_42)
            .setTitle(R.string.l2p_title_transfer_banks_modal)
            .setMessage(R.string.l2p_message_transfer_banks_modal)
            .setAction { interactor.onInterestedClick() }
            .setDismiss { interactor.onDismissClick() }
            .setOnCreateRunnable { interactor.modalAnalytics() }
            .setActionMessage(R.string.l2p_im_interested)
            .setDismissMessage(R.string.simple_not_now)
            .build()
    }

    @JvmStatic
    fun createL2PMomentsNewAccountModal(interactor: ModalCallToActionInteractor): BottomModalCallToAction {
        return BottomModalCallToAction.Builder()
            .setIcon(R.drawable.il_p2p_logo_42)
            .setTitle(R.string.l2p_title_new_account_modal)
            .setMessage(R.string.l2p_message_new_account_modal)
            .setAction { interactor.onInterestedClick() }
            .setDismiss { interactor.onDismissClick() }
            .setOnCreateRunnable { interactor.modalAnalytics() }
            .setActionMessage(R.string.l2p_im_interested)
            .setDismissMessage(R.string.simple_not_now)
            .build()
    }
}
