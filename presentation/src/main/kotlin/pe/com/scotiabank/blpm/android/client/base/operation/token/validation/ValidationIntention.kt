package pe.com.scotiabank.blpm.android.client.base.operation.token.validation

import com.scotiabank.enhancements.uuid.randomLong

enum class ValidationIntention(val id: Long) {
    GRAB_OTP_VALUE(id = randomLong()),
    SHOW_OTP_MESSAGE(id = randomLong()),
}
