package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType

enum class DeferredEvent(val type: FrequentOperationType) {

    TRANSFER_PERFORMED(type = FrequentOperationType.TRANSFER),
    PAYMENT_PERFORMED(type = FrequentOperationType.PAYMENT),
}