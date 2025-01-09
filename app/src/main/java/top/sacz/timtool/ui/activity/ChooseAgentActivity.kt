package top.sacz.timtool.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.sacz.timtool.R
import top.sacz.timtool.hook.HookEnv
import top.sacz.timtool.hook.base.AbstractChooseActivity
import java.util.Timer
import kotlin.concurrent.schedule

class ChooseAgentActivity : AbstractChooseActivity() {

    companion object {
        fun start(context: Context, intent: Intent) {
            intent.putExtra(
                "proxy_target_activity",
                "cooperation.qlink.QlinkStandardDialogActivity"
            )
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.NoDisplay)
        super.onCreate(savedInstanceState)
        bundle = intent.extras
        val intent = if (intent.getBooleanExtra("use_ACTION_PICK", false))
            Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
        else Intent(Intent.ACTION_GET_CONTENT).apply {
            type = intent.type ?: "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK &&
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
                var uin = it.getString("targetUin")
                if (uin == null) {
                    uin = it.getLong("key_peerUin").toString()
                }
                putExtra("uin", uin)


                var type = it.getInt("peerType", -1)
                if (type == -1) {
                    type = it.getInt("key_chat_type") - 1
                }
                if (type != -1) {
                    putExtra("uintype", type)
                }
//                putExtras(it)
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