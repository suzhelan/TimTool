package top.sacz.timtool.app.activity

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import top.sacz.timtool.app.ui.MainScreen

class NewMainActivity : ComponentActivity() {

    private val hideActivityName: String
        get() = "$packageName.app.activity.HideMainActivity"

    private val showActivityName: String
        get() = javaClass.name

    private val prefs by lazy { getSharedPreferences("timtool_prefs", Context.MODE_PRIVATE) }

    var isHideActivity: Boolean
        get() {
            return prefs.getBoolean("hide_icon", false)
        }
        set(enabled) {
            val hide = ComponentName(this, hideActivityName)
            hide.setEnable(this, enabled)
            val show = ComponentName(this, showActivityName)
            show.setEnable(this, !enabled)
            prefs.edit().putBoolean("hide_icon", enabled).apply()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
        )
        setContent {
            var isHideIcon by remember { mutableStateOf(isHideActivity) }
            MainScreen(
                isHideIcon = isHideIcon,
                onHideIconChange = { enabled ->
                    isHideActivity = enabled
                    isHideIcon = enabled
                }
            )
        }
    }
}

fun ComponentName.getEnable(ctx: Context): Boolean {
    val packageManager: PackageManager = ctx.packageManager
    val list = packageManager.queryIntentActivities(
        android.content.Intent().setComponent(this), PackageManager.MATCH_DEFAULT_ONLY
    )
    return list.isNotEmpty()
}

fun ComponentName.setEnable(ctx: Context, enabled: Boolean) {
    val packageManager: PackageManager = ctx.packageManager
    if (this.getEnable(ctx) == enabled) return
    packageManager.setComponentEnabledSetting(
        this,
        if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
    )
}
