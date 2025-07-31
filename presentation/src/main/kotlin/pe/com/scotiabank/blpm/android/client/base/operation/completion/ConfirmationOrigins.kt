package pe.com.scotiabank.blpm.android.client.base.operation.completion

import pe.com.scotiabank.blpm.android.client.model.ProductModel

sealed interface ConfirmationOrigin

class OwnAccount(val originAccount: ProductModel,val destinyAccount: ProductModel): ConfirmationOrigin

class OtherAccount(val originAccount: ProductModel): ConfirmationOrigin

object TransferOtherBank: ConfirmationOrigin

class Payment(val institutionId: String, val serviceCode: String, val zonalId: String): ConfirmationOrigin

object Recharge: ConfirmationOrigin

object AppraisalPayment: ConfirmationOrigin
