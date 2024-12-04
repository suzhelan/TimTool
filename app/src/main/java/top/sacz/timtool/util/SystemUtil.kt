package top.sacz.timtool.util

import android.os.Build


/**
 * 系统工具类
 */
object SystemUtil {


    /**
     * 设备名称
     *
     * @return 设备名称
     */
    val deviceName: String = Build.DEVICE

    /**
     * 设备型号
     *
     * @return 设备型号
     */
    val systemModel: String = Build.MODEL

    /**
     * 系统版本
     *
     * @return 系统版本
     */
    val systemVersion: String = Build.VERSION.RELEASE

    /**
     * 设备品牌
     *
     * @return 设备品牌
     */
    val deviceBrand: String = Build.BRAND


}