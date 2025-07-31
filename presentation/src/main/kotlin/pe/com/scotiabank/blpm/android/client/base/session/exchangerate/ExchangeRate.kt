package pe.com.scotiabank.blpm.android.client.base.session.exchangerate

import android.os.Parcel
import android.os.Parcelable
import pe.com.scotiabank.blpm.android.client.util.Constant

class ExchangeRate(
    var type: String = Constant.NONE,
    var value: Double = 0.0,
): Parcelable {

    constructor(source: Parcel): this(
        type = source.readString() ?: Constant.NONE,
        value = source.readDouble(),
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(type)
        dest.writeDouble(value)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ExchangeRate> {

        override fun createFromParcel(source: Parcel): ExchangeRate {
            return ExchangeRate(source)
        }

        override fun newArray(size: Int): Array<ExchangeRate?> {
            return arrayOfNulls(size)
        }
    }
}
