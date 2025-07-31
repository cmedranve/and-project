package pe.com.scotiabank.blpm.android.client.host.session

import pe.com.scotiabank.blpm.android.client.base.session.HolderOfProfile
import pe.com.scotiabank.blpm.android.client.base.session.HolderOfSessionRetrofit
import pe.com.scotiabank.blpm.android.client.base.session.entities.Profile
import pe.com.scotiabank.blpm.android.client.host.user.UserModel
import retrofit2.Retrofit

class SessionOpening(
    override val sessionRetrofit: Retrofit,
    userModel: UserModel,
    override val profile: Profile,
) : HolderOfSessionRetrofit, UserModel by userModel, HolderOfProfile
