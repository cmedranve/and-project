package pe.com.scotiabank.blpm.android.client.assistance.model

import pe.com.scotiabank.blpm.android.client.util.Constant

sealed interface AssistanceItem {
    val itemClass: String
    var id: String
    var displayTimes: Int
    var type: String
    var isNewUser: Boolean
    var profileList: List<String>
}

class FullScreenItem(
    override var id: String,
    override var displayTimes: Int,
    override var type: String,
    override var isNewUser: Boolean,
    override var profileList: List<String>
): AssistanceItem {

    override val itemClass: String
        get() = CLASS

    companion object {

        const val CLASS = "FULL_SCREEN"
    }
}

interface ViewItem: AssistanceItem {
    var viewId: String
}

class BulletItem(
    override var viewId: String,
    override var id: String,
    override var displayTimes: Int,
    override var type: String,
    override var isNewUser: Boolean,
    override var profileList: List<String>
): ViewItem {

    override val itemClass: String
        get() = CLASS

    companion object {

        const val CLASS = "BULLET"
    }
}

class CoachMarkItem @JvmOverloads constructor(
    var title: String,
    var description: String,
    var actionUri: String = Constant.EMPTY_STRING,
    var buttonType: Int,
    override var viewId: String = Constant.EMPTY_STRING,
    override var id: String,
    override var displayTimes: Int = 0,
    override var type: String = Constant.EMPTY_STRING,
    override var isNewUser: Boolean = false,
    override var profileList: List<String> = emptyList()
): ViewItem {

    override val itemClass: String
        get() = CLASS

    companion object {

        const val CLASS = "COACHMARK"
    }
}
