package dev.idm.vkp.dialog

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.idm.vkp.util.Utils

open class LandscapeExpandBottomSheetDialog : BottomSheetDialog {
    constructor(context: Context) : super(context) {
        if (Utils.isLandscape(context)) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    constructor(context: Context, theme: Int) : super(context, theme) {
        if (Utils.isLandscape(context)) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    protected constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener) {
        if (Utils.isLandscape(context)) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }
}
