package pe.com.scotiabank.blpm.android.client.base.session

import pe.com.scotiabank.blpm.android.client.loop2pay.seed.Loop2PaySeed
import pe.com.scotiabank.blpm.android.client.qrpayment.seed.QRSeed

class SeedHolderForPeerToPeer(
    override var loop2PaySeed: Loop2PaySeed = Loop2PaySeed(),
    override var qrSeed: QRSeed = QRSeed()
): HolderOfLoop2PaySeed, HolderOfQRSeed
