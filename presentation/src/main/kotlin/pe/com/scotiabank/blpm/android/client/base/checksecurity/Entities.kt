package pe.com.scotiabank.blpm.android.client.base.checksecurity

import pe.com.scotiabank.blpm.android.client.util.string.EMPTY

object ImmediateBlockingEntity

class BlockingEntity(val title: String, val description: String)

object TrustworthyIntegrity

sealed class SealedNonBlockingEntity(
    val title: String,
    val description: String,
    val buttonText: String
) {

    val isEmpty: Boolean
        get() = title.isBlank() || description.isBlank() || buttonText.isBlank()
}

class NonBlockingEntity(
    title: String,
    description: String,
    buttonText: String
) : SealedNonBlockingEntity(title, description, buttonText)

object EmptyNonBlockingEntity : SealedNonBlockingEntity(
    String.EMPTY,
    String.EMPTY,
    String.EMPTY,
)
