package caribouapp.caribou.com.cariboucoffee.common

import android.graphics.Rect
import android.text.method.TransformationMethod
import android.view.View
import caribouapp.caribou.com.cariboucoffee.util.StringUtils

class MaskEmailTransformationMethod : TransformationMethod {
    override fun getTransformation(charSequence: CharSequence, view: View): CharSequence {
        return StringUtils.maskEmail(charSequence.toString())
    }

    override fun onFocusChanged(view: View, charSequence: CharSequence, b: Boolean, i: Int, rect: Rect) {
        // noop
    }
}
