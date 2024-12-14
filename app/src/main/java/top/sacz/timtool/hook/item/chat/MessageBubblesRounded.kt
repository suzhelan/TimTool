package top.sacz.timtool.hook.item.chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnBindView
import top.sacz.timtool.R
import top.sacz.timtool.hook.HookEnv
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.util.ToastTool
import top.sacz.timtool.hook.util.call
import top.sacz.timtool.util.KvHelper
import top.sacz.timtool.util.ScreenParamUtils
import top.sacz.xphelper.XpHelper.context
import top.sacz.xphelper.reflect.MethodUtils

@HookItem("辅助功能/聊天/消息气泡圆度优化")
class MessageBubblesRounded : BaseSwitchFunctionHookItem() {

    override fun getTip(): String {
        return "点击修改(别忘了开启此功能)"
    }

    private fun getConfig(): KvHelper {
        return KvHelper("修改气泡圆角")
    }

    private fun getRadiiDp(): Int {
        return getConfig().getInt("radii", 0)
    }

    private fun setRadiiDp(radii: Int) {
        getConfig().put("radii", radii)
    }

    private fun onCustomColorUI(view: View, dialog: MessageDialog) {
        val editMeDaytime = view.findViewById<EditText>(R.id.edit_me_daytime_bubble)
        val editMeNighttime = view.findViewById<EditText>(R.id.edit_me_dark_bubble)
        val editTargetDaytime = view.findViewById<EditText>(R.id.edit_target_daytime_bubble)
        val editTargetNighttime = view.findViewById<EditText>(R.id.edit_target_dark_bubble)
        val bubbleDrawable = GenerateBubbleDrawable().apply {
            editMeDaytime.setText(
                String.format(
                    "#%08X",
                    getConfigColor(editMeDaytime.tag.toString())
                )
            )
            editMeNighttime.setText(
                String.format(
                    "#%08X",
                    getConfigColor(editMeNighttime.tag.toString())
                )
            )
            editTargetDaytime.setText(
                String.format(
                    "#%08X",
                    getConfigColor(editTargetDaytime.tag.toString())
                )
            )
            editTargetNighttime.setText(
                String.format(
                    "#%08X",
                    getConfigColor(editTargetNighttime.tag.toString())
                )
            )
        }
        dialog.setOkButton { dialog, v ->
            bubbleDrawable.run {
                setConfigColor(editMeDaytime.tag.toString(), editMeDaytime.text.toString())
                setConfigColor(editMeNighttime.tag.toString(), editMeNighttime.text.toString())
                setConfigColor(editTargetDaytime.tag.toString(), editTargetDaytime.text.toString())
                setConfigColor(
                    editTargetNighttime.tag.toString(),
                    editTargetNighttime.text.toString()
                )
            }
            ToastTool.show("已尝试保存")
            false
        }
        dialog.setOtherButton("恢复默认") { _, _ ->
            getConfig().clearAll()
            false
        }
    }
    @SuppressLint("SetTextI18n")
    override fun getOnClickListener(): View.OnClickListener? {
        return View.OnClickListener { view ->
            MessageDialog.build()
                .setTitle(R.string.message_bubbles_rounded_title)
                .setCustomView(object :
                    OnBindView<MessageDialog>(R.layout.layout_message_bubbles_rounded_change) {
                    override fun onBind(
                        dialog: MessageDialog,
                        v: View
                    ) {
                        onCustomColorUI(v, dialog)
                        val tvText = v.findViewById<TextView>(R.id.tv_message_demo)
                        val tvRadii = v.findViewById<TextView>(R.id.tv_radii)
                        val radii = v.findViewById<SeekBar>(R.id.sb_radii)
                        tvRadii.text = "${getRadiiDp()}dp"
                        radii.progress = getRadiiDp()
                        tvText.background = GenerateBubbleDrawable().getBubbleDrawable(
                            context = context,
                            true,
                            ScreenParamUtils.dpToPx(context, getRadiiDp().toFloat()).toFloat()
                        )
                        radii.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(
                                seekBar: SeekBar,
                                progress: Int,
                                fromUser: Boolean
                            ) {
                                setRadiiDp(progress)
                                tvRadii.text = "${getRadiiDp()}dp"
                                tvText.background = GenerateBubbleDrawable().getBubbleDrawable(
                                    context = context,
                                    true,
                                    ScreenParamUtils.dpToPx(context, getRadiiDp().toFloat())
                                        .toFloat()
                                )
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                            }

                            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                            }
                        })
                    }
                })
                .setOkButton("确定")
                .show()

        }
    }


    override fun loadHook(loader: ClassLoader) {

        //生成背景的方法
        val buildDrawableMethod = MethodUtils.create("com.tencent.mobileqq.aio.utils.ai")
            .params(
                Context::class.java,
                loader.loadClass("com.tencent.mobileqq.aio.msglist.holder.skin.TimBubbleStyle"),
                Float::class.java
            )
            .returnType(Drawable::class.java)

        //第一个是对方的生成气泡方法 方法名 e
        hookBefore(buildDrawableMethod.first()) { param ->
            val thisObject = param.thisObject
            val context = param.args[0] as Context
            val timBubbleStyle = param.args[1]
            val radii = ScreenParamUtils.dpToPx(context, getRadiiDp().toFloat()).toFloat()
            param.result = GenerateBubbleDrawable().getBubbleDrawable(
                HookEnv.getHostAppContext(),
                false,
                radii,
                thisObject.call("c", timBubbleStyle),
                thisObject.call("a", timBubbleStyle)
            )
        }

        //第二个是自己的生成气泡方法 方法名 g
        hookBefore(buildDrawableMethod.last()) { param ->
            val thisObject = param.thisObject
            val context = param.args[0] as Context
            val timBubbleStyle = param.args[1]
            val radii = ScreenParamUtils.dpToPx(context, getRadiiDp().toFloat()).toFloat()
            param.result = GenerateBubbleDrawable().getBubbleDrawable(
                HookEnv.getHostAppContext(),
                true,
                radii,
                thisObject.call("c", timBubbleStyle),
                thisObject.call("a", timBubbleStyle)
            )
        }
    }

    /**
     * 其实可以考虑自己构建背景 但是那样侵入性太高 不好控制边距 会显得很怪
     */
    internal class GenerateBubbleDrawable {

        private fun getConfig(): KvHelper {
            return KvHelper("修改气泡圆角")
        }

        fun setConfigColor(tag: String, colorHex: String): Boolean {
            return try {
                val color = Color.parseColor(colorHex)
                getConfig().put(tag, color)
                true
            } catch (e: Exception) {
                ToastTool.show("$tag 数据格式不正确${e.message}")
                false
            }
        }

        fun getConfigColor(tag: String): Int {
            val context = HookEnv.getHostAppContext()
            if (getConfig().getInt(tag, 0) != 0) {
                return getConfig().getInt(tag)
            }
            when (tag) {
                context.getString(R.string.me_daytime_bubble) -> return Color.parseColor("#508FFF")
                context.getString(R.string.me_dark_bubble) -> return Color.parseColor("#314A77")
                context.getString(R.string.target_daytime_bubble) -> return Color.parseColor("#FFFFFF")
                context.getString(R.string.target_dark_bubble) -> return Color.parseColor("#333333")
            }
            return 0
        }

        private fun selfColor(isNight: Boolean): Int {
            if (isNight) {
                return getConfigColor(context.getString(R.string.me_dark_bubble))
            }
            return getConfigColor(context.getString(R.string.me_daytime_bubble))
        }

        private fun guestColor(isNight: Boolean): Int {
            if (isNight) {
                return getConfigColor(context.getString(R.string.target_dark_bubble))
            }
            return getConfigColor(context.getString(R.string.target_daytime_bubble))
        }

        private fun isDarkMode(context: Context): Boolean {
            val currentNightMode =
                context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return currentNightMode == Configuration.UI_MODE_NIGHT_YES
        }

        private fun getDrawableColor(context: Context, isSelf: Boolean): Int {
            val isNight = isDarkMode(context)
            return if (isSelf) {
                selfColor(isNight)
            } else {
                guestColor(isNight)
            }
        }

        /**
         * 调用此方法构造气泡背景
         *
         */
        fun getBubbleDrawable(
            context: Context,
            isSelf: Boolean,
            radii: Float,
            top: Int = 0,
            bottom: Int = 0
        ): Drawable {
            //使用的qq生成方法
            // com.tencent.mobileqq.aio.utils.ai.g(android.content.Context, com.tencent.mobileqq.aio.msglist.holder.skin.TimBubbleStyle, float)
            //摘选QQ生成方式
            val gradientDrawable = GradientDrawable()
            gradientDrawable.setShape(GradientDrawable.RECTANGLE)
            gradientDrawable.setColor(getDrawableColor(context, isSelf))
            //设置图片四个角圆形半径：1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
            gradientDrawable.cornerRadii = floatArrayOf(
                radii, radii,
                radii, radii,
                radii, radii,
                radii, radii
            )
            val leftAndRight = ScreenParamUtils.dpToPx(context, 10f)
            val layerDrawable = LayerDrawable(arrayOf(gradientDrawable))
            layerDrawable.setLayerInset(0, leftAndRight, top, leftAndRight, bottom)
            return layerDrawable
        }
    }
}