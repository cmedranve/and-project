package pe.com.scotiabank.blpm.android.client.base


/**
 * Compat version of {@link java.util.function.Function} for coroutines
 * @param <I> the type of the input to the operation
 * @param <O>: the type of the output of the function
 */
fun interface SuspendingFunction<I: Any, O: Any> {

    /**
     * Applies the function to the argument parameter.
     *
     * @param input <I> the argument for the function
     * @return <O> the result after applying function
     */
    suspend fun apply(input: I): O
}
