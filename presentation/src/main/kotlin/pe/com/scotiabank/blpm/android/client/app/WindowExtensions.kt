package pe.com.scotiabank.blpm.android.client.app

import android.os.Build
import android.view.Window
import android.view.WindowManager

fun Window.setHideOverlayWindows() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        setHideOverlayWindows(true)
    }
}

fun Window.clearWindowFromSecureSurface() {
    clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
}

fun Window.applySecureSurfaceOnWindow() {
    setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
}
