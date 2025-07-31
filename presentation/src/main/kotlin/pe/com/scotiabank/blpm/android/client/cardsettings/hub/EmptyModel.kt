package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import pe.com.scotiabank.blpm.android.client.base.SuspendingFunction

class EmptyModel: SuspendingFunction<String, Any> {

    override suspend fun apply(input: String): Any = Unit
}
