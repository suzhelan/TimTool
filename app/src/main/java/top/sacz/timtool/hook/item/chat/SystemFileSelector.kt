package top.sacz.timtool.hook.item.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.Intent.ACTION_PICK
import android.content.Intent.EXTRA_ALLOW_MULTIPLE
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.kongzue.dialogx.dialogs.MessageMenu
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.sacz.timtool.R
import top.sacz.timtool.hook.HookEnv
import top.sacz.timtool.hook.base.AbstractChooseActivity
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import java.util.Timer
import kotlin.concurrent.schedule

@HookItem("辅助功能/文件/可选系统文件选择器")
class SystemFileSelector : BaseSwitchFunctionHookItem() {

    fun onStartActivityIntent(intent: Intent, param: XC_MethodHook.MethodHookParam): Boolean {
        if (intent.component?.className?.contains("filemanager.activity.FMActivity") == true &&
            (!intent.getBooleanExtra("is_decorated", false))
        ) {
            val context = param.thisObject as Context
            val targetUin: Long = try {
                intent.getStringExtra("targetUin")?.toLong() ?: -1L
            } catch (e: NumberFormatException) {
                -1L
            }
            // note that old version of FMActivity has no targetUin
            if (targetUin in 1..9999) {
                // reserved for special usage
                return false
            }
            if ("guild" in android.util.Log.getStackTraceString(Throwable())) {
                // Filter out calls in the guild
                return false
            }
            val activityMap = mapOf(
                "系统文档" to Intent(context, ChooseAgentActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtras(intent)
                    type = "*/*"
                },
                "QQ 文件" to intent.apply {
                    putExtra("is_decorated", true)
                }
            )
            MessageMenu.build()
                .setTitle("文件选择器")
                .setMenuList(activityMap.keys.toTypedArray())
                .setOnMenuItemClickListener { dialog, text, index ->
                    context.startActivity(activityMap[text])
                    false
                }.show()
            param.result = null
            return true
        }
        return false
    }

    override fun loadHook(loader: ClassLoader) {
        val hook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val intent: Intent = if (param.args[0] is Intent) {
                    param.args[0] as Intent
                } else {
                    param.args[1] as Intent
                }
                if (isEnabled) {
                    onStartActivityIntent(intent, param)
                }
            }
        }
        XposedBridge.hookAllMethods(ContextWrapper::class.java, "startActivity", hook)
        XposedBridge.hookAllMethods(ContextWrapper::class.java, "startActivityForResult", hook)
        XposedBridge.hookAllMethods(Activity::class.java, "startActivity", hook)
        XposedBridge.hookAllMethods(Activity::class.java, "startActivityForResult", hook)
    }
}

class ChooseAgentActivity : AbstractChooseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.NoDisplay)
        super.onCreate(savedInstanceState)
        bundle = intent.extras
        val intent = if (intent.getBooleanExtra("use_ACTION_PICK", false))
            Intent(ACTION_PICK).apply {
                type = "image/*"
                putExtra(EXTRA_ALLOW_MULTIPLE, true)
            }
        else Intent(ACTION_GET_CONTENT).apply {
            type = intent.type ?: "*/*"
            putExtra(EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK &&
                        (data?.data != null || data?.clipData != null)
                    ) {
                        initSendCacheDir()
                        val uri = data.data
                        val clip = data.clipData
                        if (uri != null) {
                            // Only one file chosen
                            convertUriToPath(uri)?.let {
                                sendAFile(it, data)
                            }
                        } else if (clip != null) {
                            // multiple file chosen
                            convertClipDataToPath(clip).let {
                                var delayTime: Long = 0
                                for (i in it) {
                                    Timer().schedule(delayTime) {
                                        sendAFile(i, data)
                                    }
                                    delayTime += 1000
                                }
                            }
                        }
                    }
                    finish()
                } catch (e: Exception) {

                }
            }
        }
    }

    @SuppressLint("WrongConstant")
    fun sendAFile(filePath: String, data: Intent) {
        val intent = Intent().apply {
            component =
                packageManager.getLaunchIntentForPackage(HookEnv.getCurrentHostAppPackageName())!!.component
            flags = 0x14000000
            putExtras(data)
            putExtra("forward_from_jump", true)
            putExtra("preAct", "JumpActivity")
            putExtra("miniAppShareFrom", 0)
            putExtra("system_share", true)
            putExtra("leftBackText", "消息")
            putExtra("task_launched_for_result", true)
            putExtra("isFromShare", true)
            putExtra("needShareCallBack", false)
            putExtra("key_forward_ability_type", 0)
            putExtra("moInputType", 2)
            putExtra("chooseFriendFrom", 1)
            putExtra("forward_source_business_type", -1)
            if (intent.type == "image/*") {
                putExtra("forward_type", 1)
            } else {
                putExtra("forward_type", 0)
            }
            bundle?.let {
                val uin = it.getString("targetUin")
                if (uin != null) {
                    putExtra("uin", uin)
                }
                val type = it.getInt("peerType", -1)
                if (type != -1) {
                    putExtra("uintype", type)
                }
                putExtras(it)
            }
            putExtra("selection_mode", 2)
            putExtra("sendMultiple", false)
            putExtra("isBack2Root", true)
            putExtra("open_chatfragment", true)
            putExtra("forward_filepath", filePath)
        }
        startActivity(intent)
    }
}