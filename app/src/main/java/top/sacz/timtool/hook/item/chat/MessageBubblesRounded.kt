package top.sacz.timtool.hook.item.chat

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.widget.SeekBar
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnBindingView
import top.sacz.timtool.databinding.LayoutMessageBubblesRoundedChangeBinding
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.util.KvHelper
import top.sacz.timtool.util.ScreenParamUtils
import top.sacz.xphelper.XpHelper.context
import top.sacz.xphelper.reflect.Ignore
import top.sacz.xphelper.reflect.MethodUtils

@HookItem("辅助功能/聊天/消息气泡圆度优化")
class MessageBubblesRounded : BaseSwitchFunctionHookItem() {

    override fun getTip(): String {
        return "点击修改(别忘了开启此功能)"
    }

    private fun getRadiiDp(): Int {
        return KvHelper("修改气泡圆角").getInt("radii", 0)
    }

    private fun setRadiiDp(radii: Int) {
        KvHelper("修改气泡圆角").put("radii", radii)
    }

    override fun getOnClickListener(): View.OnClickListener? {
        return View.OnClickListener { view ->
            MessageDialog.build()
                .setTitle("自定义头像圆角")
                .setCustomView(object :
                    OnBindingView<MessageDialog, LayoutMessageBubblesRoundedChangeBinding>() {
                    override fun onBind(
                        dialog: MessageDialog,
                        v: View,
                        binding: LayoutMessageBubblesRoundedChangeBinding
                    ) {
                        val tvText = binding.tvMessageDemo
                        val tvRadii = binding.tvRadii
                        val radii = binding.sbRadii
                        radii.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(
                                seekBar: SeekBar,
                                progress: Int,
                                fromUser: Boolean
                            ) {
                                setRadiiDp(progress)
                                tvRadii.text = "${progress}dp"
                                tvText.background = GenerateBubbleDrawable().getBubbleDrawable(
                                    context = context,
                                    false,
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
                .setOkButton("确定") { _, _ ->
                    false
                }
                .show()

        }
    }


    override fun loadHook(loader: ClassLoader) {
        //method name w
        val textContent1 = MethodUtils.create("com.tencent.mobileqq.aio.msg.TextMsgContent")
            .params(Drawable::class.java, Int::class.java, Int::class.java, Ignore::class.java)
            .first()
        hookBefore(textContent1) {
            val backgroundLayer = it.args[0] as LayerDrawable
            val textViewBackground = backgroundLayer.getDrawable(0) as GradientDrawable
            val radii = ScreenParamUtils.dpToPx(context, getRadiiDp().toFloat()).toFloat()
            textViewBackground.cornerRadii = floatArrayOf(
                radii, radii,
                radii, radii,
                radii, radii,
                radii, radii
            )
        }

    }

    /**
     * 其实可以考虑自己构建背景 但是那样侵入性太高 不好控制边距 会显得很怪
     */
    class GenerateBubbleDrawable {
        private fun selfColor(isNight: Boolean): Int {
            if (isNight) {
                return Color.parseColor("#314A77")
            }
            return Color.parseColor("#508FFF")
        }

        private fun guestColor(isNight: Boolean): Int {
            if (isNight) {
                return Color.parseColor("#333333")
            }
            return Color.parseColor("#FFFFFF")
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
        fun getBubbleDrawable(context: Context, isSelf: Boolean, radii: Float): Drawable {
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
            val w = ScreenParamUtils.dpToPx(context, 10f)
            val layerDrawable = LayerDrawable(arrayOf(gradientDrawable))
            //到这 很难控制层级边距,所以弃用了此方案
            layerDrawable.setLayerInset(0, w, 0, w, 0)
            return layerDrawable
        }
    }
}