package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel

fun FrequentOperationModel.copyFrom(
    other: FrequentOperationModel,
): FrequentOperationModel = apply {
    type = other.type
    id = other.id
    isEnabled = other.isEnabled
    isSuccess = other.isSuccess
    isChecked = other.isChecked
    title = other.title
    subTitle1 = other.subTitle1
    subTitle2 = other.subTitle2
    currency = other.currency
    amount = other.amount
    isShowMore = other.isShowMore
    isImmediate = other.isImmediate
    institutionId = other.institutionId
    institutionName = other.institutionName
    serviceCode = other.serviceCode
    serviceName = other.serviceName
    paymentCode = other.paymentCode
    zonal = other.zonal
    transferType = other.transferType
    availableTransferType = other.availableTransferType
    isPayableWithCreditCard = other.isPayableWithCreditCard
    receipt = other.receipt
    dueDate = other.dueDate
    issueDate = other.issueDate
    installmentsNumber = other.installmentsNumber
    isPendingReceipt = other.isPendingReceipt
    isMaskAmount = other.isMaskAmount
    isAllowMultiplePayments = other.isAllowMultiplePayments
}