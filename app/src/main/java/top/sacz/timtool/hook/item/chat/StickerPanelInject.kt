package top.sacz.timtool.hook.item.chat

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import top.sacz.timtool.R
import top.sacz.timtool.hook.HookEnv
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.item.chat.stickerpanel.BottomStickerPanelDialog
import top.sacz.timtool.hook.util.PathTool
import top.sacz.timtool.hook.util.ToastTool
import top.sacz.timtool.util.DrawableUtil
import top.sacz.xphelper.reflect.FieldUtils
import top.sacz.xphelper.reflect.MethodUtils
import java.io.File


@HookItem("辅助功能/聊天/表情面板")
class StickerPanelInject : BaseSwitchFunctionHookItem() {

    override fun loadHook(loader: ClassLoader) {

        // 注入表情面板入口图标
        val onCreate =
            MethodUtils.create("com.tencent.tim.aio.inputbar.simpleui.TimAIOInputSimpleUIVBDelegate")
                .methodName("C")
                .returnType(Void.TYPE)
                .first()
        hookAfter(onCreate) { param ->
            val targetObj = param.thisObject

            val emoBtnView = FieldUtils.create(targetObj)
                .fieldName("h")
                .fieldType(ImageButton::class.java)
                .firstValue<ImageButton>(targetObj)
            emoBtnView.setOnLongClickListener {
                BottomStickerPanelDialog().show()
                return@setOnLongClickListener true
            }
        }
    }

    /**
     * lazy 懒加载 被使用时才会加载
     */
    private val iconSrc: Drawable by lazy {
        val context = HookEnv.getHostAppContext()
        val path = PathTool.getModuleDataPath() + "/sticker.png"
        if (!File(path).exists()) {
            val icon = ResourcesCompat.getDrawable(
                HookEnv.getHostAppContext().resources,
                R.drawable.sticker,
                null
            )
            DrawableUtil.drawableToFile(icon, path, Bitmap.CompressFormat.PNG)
            ToastTool.show("表情图标已生成")
        }
        DrawableUtil.readDrawableFromFile(context, path)
    }

    private fun createStickerPanelIcon(context: Context): ImageView {
        val iconImageview = ImageView(context)
        iconImageview.setImageDrawable(iconSrc)
        iconImageview.contentDescription = "表情面板"
        iconImageview.setOnClickListener { BottomStickerPanelDialog().show() }
        return iconImageview
    }

    override fun getOnClickListener(): View.OnClickListener {
        return View.OnClickListener {
            BottomStickerPanelDialog()
                .show()
        }
    }

}