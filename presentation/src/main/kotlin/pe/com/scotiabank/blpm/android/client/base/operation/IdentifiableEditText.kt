package pe.com.scotiabank.blpm.android.client.base.operation

import com.scotiabank.enhancements.uuid.randomLong

enum class IdentifiableEditText(val id: Long) {
    AMOUNT(id = randomLong()),
    CUSTOMER_PRODUCT_NUMBER(id = randomLong()),
    EXTERNAL_PRODUCT_ID(id = randomLong()),
    RECIPIENT_NAME(id = randomLong()),
    PHONE_NUMBER(id = randomLong()),
    DESCRIPTION(id = randomLong()),
    DIGITAL_KEY(id = randomLong()),
}
