package top.sacz.timtool.hook.util

import com.kongzue.dialogx.dialogs.PopTip
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ToastTool {

    @JvmStatic
    @OptIn(DelicateCoroutinesApi::class)
    fun show(content: Any) {
        GlobalScope.launch(Dispatchers.Main) {
            PopTip.show(content.toString())
        }
    }
}