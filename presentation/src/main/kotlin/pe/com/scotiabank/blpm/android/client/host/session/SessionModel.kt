package pe.com.scotiabank.blpm.android.client.host.session

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.session.entities.Profile
import pe.com.scotiabank.blpm.android.client.base.session.entities.ProfileMapper
import pe.com.scotiabank.blpm.android.client.host.user.UserModel
import pe.com.scotiabank.blpm.android.data.entity.MainSeedEntity
import pe.com.scotiabank.blpm.android.data.entity.ProfileEntity
import pe.com.scotiabank.blpm.android.data.repository.UserDataRepository
import retrofit2.Retrofit

class SessionModel(
    dispatcherProvider: DispatcherProvider,
    private val appModel: AppModel,
    private val userModel: UserModel,
    private val retrofit: Retrofit,
    private val userDataRepository: UserDataRepository,
): DispatcherProvider by dispatcherProvider {

    suspend fun openSession(): Boolean = withContext(ioDispatcher) {
        val profile: Profile = getProfile()
        appModel.serverDate = getServerDate()
        val sessionOpening = SessionOpening(retrofit, userModel, profile)
        appModel.receive(sessionOpening)
    }

    private fun getProfile(): Profile {
        val entity: ProfileEntity = userDataRepository.profile.blockingSingle()
        return ProfileMapper.transformProfile(entity)
    }

    private fun getServerDate(): String {
        val entity: MainSeedEntity = userDataRepository.mainSeed.blockingSingle()
        return entity.serverDate.orEmpty()
    }
}
