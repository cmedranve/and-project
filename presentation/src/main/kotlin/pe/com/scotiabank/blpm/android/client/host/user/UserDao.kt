package pe.com.scotiabank.blpm.android.client.host.user

interface UserDao {

    val user: User

    val isEmpty: Boolean

    fun saveUser(user: User)

    fun clear()
}