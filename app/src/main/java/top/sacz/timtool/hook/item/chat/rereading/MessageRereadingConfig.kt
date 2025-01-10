package top.sacz.timtool.hook.item.chat.rereading

import top.sacz.timtool.util.KvHelper

object MessageRereadingConfig {
    // 两次点击按钮之间的点击间隔不能少于500毫秒
    private const val MIN_CLICK_DELAY_TIME: Int = 500
    private var lastClickTime: Long = 0

    private val config: KvHelper by lazy { KvHelper("消息复读") }

    fun isDoubleClickMode(): Boolean {
        return config.getBoolean("isDoubleClickMode", false)
    }

    fun setDoubleClickMode(isDoubleClickMode: Boolean) {
        config.put("isDoubleClickMode", isDoubleClickMode)
    }

    fun isShowInMenu(): Boolean {
        return config.getBoolean("isShowInMenu", false)
    }

    fun setShowInMenu(isShowInMenu: Boolean) {
        config.put("isShowInMenu", isShowInMenu)
    }

    fun getSize(): Float {
        return config.getFloat("size", 24f)
    }

    fun setSize(size: Float) {
        config.put("size", size)
    }

    /**
     * 判断是否连击
     */
    fun isFastClick(): Boolean {
        var flag = false
        val curClickTime = System.currentTimeMillis()
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true
        }
        lastClickTime = curClickTime
        return !flag
    }

}
