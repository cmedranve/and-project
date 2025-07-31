package pe.com.scotiabank.blpm.android.ui.list.layoutmanager

import android.os.Parcelable

object StatelessRecycling : Recycling {

    override var recyclingState: Parcelable?
        get() = null
        set(value) {
            // do nothing as keeping recycling-state isn't required
        }
}
