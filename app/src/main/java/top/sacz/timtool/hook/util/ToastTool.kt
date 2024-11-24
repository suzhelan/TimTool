package top.sacz.timtool.hook.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import top.sacz.timtool.hook.HookEnv

object ToastTool {

    fun show(content: Any) {
        Handler(Looper.getMainLooper()).post {
            var activity: Context? = ActivityTools.getActivity()
            if (activity == null) activity = HookEnv.getInstance().hostAppContext
            try {
                Toast.makeText(activity, content.toString(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                try {
                    Toast.makeText(activity, content.toString(), Toast.LENGTH_LONG).show()
                } catch (ex: Exception) {
                }
            }
        }
    }
}