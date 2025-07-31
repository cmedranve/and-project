package pe.com.scotiabank.blpm.android.ui.list.changingstates

import com.scotiabank.enhancements.typechecking.isSameOrSubTypeOf
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.isNullableEntityTheSameAs
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType

class MutableState: ChangingState {

    override var isUnmodified: Boolean = true
        private set

    override fun <E : IdentifiableUiEntity<E>> onChangeOfEntityProperty(
        property: KProperty<E?>,
        oldValue: E?,
        newValue: E?
    ) {
        val isSameContent: Boolean = oldValue.isNullableEntityTheSameAs(newValue)
        if (isSameContent) return

        isUnmodified = false
    }

    @Suppress("UNCHECKED_CAST")
    override fun <A : Any> onChangeOfNonEntityProperty(
        property: KProperty<A?>,
        oldValue: A?,
        newValue: A?
    ) {
        if (oldValue == null && newValue == null) return

        val returnType: KType = property.returnType
        val typeUnderEvaluation: KClass<A> = returnType.classifier as? KClass<A> ?: return

        val isSameContent: Boolean = isSameContent(typeUnderEvaluation, oldValue, newValue)
        if (isSameContent) return

        isUnmodified = false
    }

    private fun <A : Any> isSameContent(
        typeUnderEvaluation: KClass<A>,
        oldValue: A?,
        newValue: A?,
    ): Boolean = when {
        typeUnderEvaluation.isSameOrSubTypeOf(ByteArray::class) -> {
            val oldArray: ByteArray? = oldValue as? ByteArray?
            val newArray: ByteArray? = newValue as? ByteArray?
            oldArray.contentEquals(newArray)
        }
        typeUnderEvaluation.isSameOrSubTypeOf(CharArray::class) -> {
            val oldArray: CharArray? = oldValue as? CharArray?
            val newArray: CharArray? = newValue as? CharArray?
            oldArray.contentEquals(newArray)
        }
        typeUnderEvaluation.isSameOrSubTypeOf(ShortArray::class) -> {
            val oldArray: ShortArray? = oldValue as? ShortArray?
            val newArray: ShortArray? = newValue as? ShortArray?
            oldArray.contentEquals(newArray)
        }
        typeUnderEvaluation.isSameOrSubTypeOf(IntArray::class) -> {
            val oldArray: IntArray? = oldValue as? IntArray?
            val newArray: IntArray? = newValue as? IntArray?
            oldArray.contentEquals(newArray)
        }
        typeUnderEvaluation.isSameOrSubTypeOf(LongArray::class) -> {
            val oldArray: LongArray? = oldValue as? LongArray?
            val newArray: LongArray? = newValue as? LongArray?
            oldArray.contentEquals(newArray)
        }
        typeUnderEvaluation.isSameOrSubTypeOf(FloatArray::class) -> {
            val oldArray: FloatArray? = oldValue as? FloatArray?
            val newArray: FloatArray? = newValue as? FloatArray?
            oldArray.contentEquals(newArray)
        }
        typeUnderEvaluation.isSameOrSubTypeOf(DoubleArray::class) -> {
            val oldArray: DoubleArray? = oldValue as? DoubleArray?
            val newArray: DoubleArray? = newValue as? DoubleArray?
            oldArray.contentEquals(newArray)
        }
        typeUnderEvaluation.isSameOrSubTypeOf(BooleanArray::class) -> {
            val oldArray: BooleanArray? = oldValue as? BooleanArray?
            val newArray: BooleanArray? = newValue as? BooleanArray?
            oldArray.contentEquals(newArray)
        }
        typeUnderEvaluation.isSameOrSubTypeOf(Array::class) -> {
            val oldArray: Array<*>? = oldValue as? Array<*>?
            val newArray: Array<*>? = newValue as? Array<*>?
            oldArray.contentEquals(newArray)
        }
        typeUnderEvaluation.isSameOrSubTypeOf(CharSequence::class) -> {
            val oldArray: CharSequence? = oldValue as? CharSequence?
            val newArray: CharSequence? = newValue as? CharSequence?
            oldArray.contentEquals(newArray)
        }
        else -> oldValue == newValue
    }

    override fun resetChangingState() {
        isUnmodified = true
    }
}
