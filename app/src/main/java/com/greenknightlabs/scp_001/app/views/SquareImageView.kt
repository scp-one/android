package com.greenknightlabs.scp_001.app.views

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet

class SquareImageView : androidx.appcompat.widget.AppCompatImageView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {}

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    ) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setMeasuredDimension(measuredWidth, measuredWidth)
        } else {
            setMeasuredDimension(measuredHeight, measuredHeight)
        }
    }
}