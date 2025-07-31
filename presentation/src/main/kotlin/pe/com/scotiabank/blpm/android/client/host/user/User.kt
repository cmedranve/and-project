package pe.com.scotiabank.blpm.android.client.host.user

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class User(

    @JsonProperty("is_qr_deep_link_available")
    val isQrDeepLinkAvailable: Boolean,

    @JsonProperty("is_contact_pay_qr_available")
    val isContactPayQrAvailable: Boolean,

    @JsonProperty("user_id")
    val userId: CharArray,

    @JsonProperty("nick_name")
    val nickName: CharArray,

    @JsonProperty("avatar")
    val avatar: CharArray,
)