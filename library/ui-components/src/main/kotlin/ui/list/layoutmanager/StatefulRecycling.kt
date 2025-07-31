package pe.com.scotiabank.blpm.android.ui.list.layoutmanager

import android.os.Parcelable

class StatefulRecycling: Recycling {

    override var recyclingState: Parcelable? = null
        get() {
            val copy: Parcelable? = field
            field = null
            return copy
        }
}
