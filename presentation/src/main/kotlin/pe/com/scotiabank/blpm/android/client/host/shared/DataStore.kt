package pe.com.scotiabank.blpm.android.client.host.shared

import android.content.SharedPreferences
import com.fasterxml.jackson.databind.ObjectMapper
import pe.com.scotiabank.blpm.android.client.host.user.User
import pe.com.scotiabank.blpm.android.client.host.user.UserDao
import pe.com.scotiabank.blpm.android.client.util.Constant

class DataStore(
    private val objectMapper: ObjectMapper,
    private val sharedPref: SharedPreferences,
) : UserDao {

    private val edit: SharedPreferences.Editor
        get() = sharedPref.edit()

    private val emptyUser: User by lazy {
        val empty: CharArray = Constant.EMPTY_STRING.toCharArray()
        User(true, false, empty, empty, empty)
    }

    private val emptyUserAsString: String by lazy {
        objectMapper.writeValueAsString(emptyUser)
    }

    override val user: User
        get() {
            val userAsString: String = sharedPref
                .getString(AVATAR_DATA, emptyUserAsString)
                ?: return emptyUser
            return objectMapper.readValue(userAsString, User::class.java)
        }

    override val isEmpty: Boolean
        get() = String(user.userId).isBlank()

    override fun saveUser(user: User) {
        val userAsString: String = objectMapper.writeValueAsString(user)
        edit.putString(AVATAR_DATA, userAsString).apply()
    }

    override fun clear() {
        edit.clear().commit()
    }

    companion object {

        private val AVATAR_DATA: String
            get() = "AVATAR_DATA"
    }
}
