package pe.com.scotiabank.blpm.android.client.base.carrier

import android.os.Parcelable
import androidx.lifecycle.viewmodel.CreationExtras

@JvmField
val CREATION_KEY_OF_BOOLEAN_ID_NAMES = object : CreationExtras.Key<Array<out String>> {}
@JvmField
val CREATION_KEY_OF_BOOLEAN_VALUES = object : CreationExtras.Key<BooleanArray> {}
@JvmField
val CREATION_KEY_OF_INT_ID_NAMES = object : CreationExtras.Key<Array<out String>> {}
@JvmField
val CREATION_KEY_OF_INT_VALUES = object : CreationExtras.Key<IntArray> {}
@JvmField
val CREATION_KEY_OF_LONG_ID_NAMES = object : CreationExtras.Key<Array<out String>> {}
@JvmField
val CREATION_KEY_OF_LONG_VALUES = object : CreationExtras.Key<LongArray> {}
@JvmField
val CREATION_KEY_OF_STRING_ID_NAMES = object : CreationExtras.Key<Array<out String>> {}
@JvmField
val CREATION_KEY_OF_STRING_VALUES = object : CreationExtras.Key<Array<out String>> {}
@JvmField
val CREATION_KEY_OF_PARCELABLE_ID_NAMES = object : CreationExtras.Key<Array<out String>> {}
@JvmField
val CREATION_KEY_OF_PARCELABLE_VALUES = object : CreationExtras.Key<Array<out Parcelable>> {}
