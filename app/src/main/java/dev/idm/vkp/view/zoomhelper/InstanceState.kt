package dev.idm.vkp.view.zoomhelper

internal object InstanceState {
    private var helper: ZoomHelper? = null

    fun getZoomHelper(): ZoomHelper {
        if (helper == null) helper = ZoomHelper()
        return helper!!
    }

    fun release() {
        helper = null
    }
}