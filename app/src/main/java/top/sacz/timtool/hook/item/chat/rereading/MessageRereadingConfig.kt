package top.sacz.timtool.hook.item.chat.rereading

import top.sacz.xphelper.util.ConfigUtils

object MessageRereadingConfig {
    // 两次点击按钮之间的点击间隔不能少于500毫秒
    private const val MIN_CLICK_DELAY_TIME: Int = 500
    private var lastClickTime: Long = 0

    private val config: ConfigUtils by lazy { ConfigUtils("消息复读") }

    fun isDoubleClickMode(): Boolean {
        return config.getBoolean("isDoubleClickMode", false)
    }

    fun setDoubleClickMode(isDoubleClickMode: Boolean) {
        config.put("isDoubleClickMode", isDoubleClickMode)
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
