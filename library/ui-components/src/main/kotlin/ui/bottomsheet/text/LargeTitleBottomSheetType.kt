package pe.com.scotiabank.blpm.android.ui.bottomsheet.text

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import com.scotiabank.canvascore.R
import com.scotiabank.canvascore.bottomsheet.model.AttrsBodyTextType
import com.scotiabank.canvascore.bottomsheet.types.BodyTextBottomSheetType
import com.scotiabank.canvascore.views.CanvasTextView

class LargeTitleBottomSheetType : BodyTextBottomSheetType() {

    /** Create new instance of [ LargeTitleBottomSheetType] to create a Body Text Bottom Sheet*/
    companion object {
        private const val PARAM_ATTRS = "body_text_bottom_sheet_attrs"

        /**
         * <b>Example:</b>
         * </br>LargeTitleBottomSheetType.newInstance(
        </br>   attributes = AttrsBodyTextType(
        </br>       headline = "Enter headline text here",
        </br>       bodyText = "Enter body text here",
        </br>       primaryButtonLabel = "Enter primary button label here",
        </br>       secondaryButtonLabel = "Enter secondary button label here"
        </br>   )
        </br>).apply {
        </br>   primaryButtonEvent = {
        </br>       //Callback of primary button clicked
        </br>   }
        </br>   secondaryButtonEvent = {
        </br>       //Callback of primary button clicked
        </br>   }
        </br>}.also {
        </br>   it.show(fragmentManager)
        </br>}
         * @param attributes Is required.
         */
        fun newInstance(attributes: AttrsBodyTextType): LargeTitleBottomSheetType {
            val bundle = Bundle().apply { putParcelable(PARAM_ATTRS, attributes) }
            return LargeTitleBottomSheetType().also { it.arguments = bundle }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enlargeHeadline(view)
    }

    private fun enlargeHeadline(view: View) {
        val ctvHeadline: CanvasTextView = view.findViewById(R.id.tv_headline)
        val resources: Resources = requireContext().resources
        val headlineSize: Float = resources.getDimension(R.dimen.canvascore_font_18)
        ctvHeadline.setTextSize(TypedValue.COMPLEX_UNIT_PX, headlineSize)
    }
}
