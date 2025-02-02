package top.sacz.xphelper.util

import android.content.Context
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.TypeReference
import io.fastkv.FastKV
import io.fastkv.interfaces.FastCipher


/**
 * 简单数据存储类
 * 完整构造方法 默认会生成无密码文件名为default的数据库
 * 如果需要加密传入密码
 */
class ConfigUtils(val id: String = "default", password: String = globalPassword) {

    /**
     * 默认构造方法 会生成无密码文件名为default的数据库
     */
    constructor() : this("default")

    /**
     * 构造方法 传入数据库名称
     */
    constructor(key: String) : this(key, globalPassword)

    private var kv: FastKV


    init {
        if (storePath.isEmpty()) {
            throw RuntimeException("storePath is empty(请使用KvHelper.initialize(String path)初始化")
        }
        kv = if (globalPassword.isEmpty()) {
            FastKV.Builder(storePath, id)
                .build()
        } else {
            FastKV.Builder(storePath, id)
                .cipher(Cipher(password))
                .build()
        }
    }


    companion object {
        private var storePath = ""

        private var globalPassword = ""
        /**
         * 初始化 传入文件夹路径
         */
        @JvmStatic
        fun initialize(path: String) {
            storePath = path
        }

        @JvmStatic
        fun setGlobalPassword(password: String) {
            globalPassword = password
        }
        @JvmStatic
        fun initialize(context: Context) {
            storePath = context.filesDir.absolutePath + "/XpHelper"
        }
    }

    /**
     * 保存数据的方法
     * @param key
     * @param value
     */
    fun put(key: String, value: Any) {
        when (value) {
            is String -> {
                kv.putString(key, value)
            }

            is Int -> {
                kv.putInt(key, value)
            }

            is Boolean -> {
                kv.putBoolean(key, value)
            }

            is Float -> {
                kv.putFloat(key, value)
            }

            is Long -> {
                kv.putLong(key, value)
            }

            is Double -> {
                kv.putDouble(key, value)
            }

            is ByteArray -> {
                kv.putArray(key, value)
            }

            else -> {
                kv.putString(key, JSON.toJSONString(value))
            }
        }
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    fun getInt(key: String, def: Int = 0): Int {
        return kv.getInt(key, def)
    }

    fun getDouble(key: String, def: Double = 0.00): Double {
        return kv.getDouble(key, def)
    }

    fun getLong(key: String, def: Long = 0L): Long {
        return kv.getLong(key, def)
    }

    fun getBoolean(key: String, def: Boolean = false): Boolean {
        return kv.getBoolean(key, def)
    }

    fun getFloat(key: String, def: Float = 0f): Float {
        return kv.getFloat(key, def)
    }

    fun getBytes(key: String, def: ByteArray = byteArrayOf()): ByteArray {
        return kv.getArray(key, def)
    }

    fun getString(key: String, def: String = ""): String {
        return kv.getString(key, def) ?: ""
    }


    fun <T> getObject(key: String, type: Class<T>): T? {
        val data = kv.getString(key)
        if (data.isNullOrEmpty()) {
            return null
        }
        return JSON.parseObject(data, type)
    }


    fun <T> getObject(key: String, type: TypeReference<T>): T? {
        val data = kv.getString(key)
        if (data.isNullOrEmpty()) {
            return null
        }
        return JSON.parseObject(data, type)
    }

    /**
     * 清除所有key
     */
    fun clearAll() {
        kv.clear()
    }

    fun remove(key: String) {
        kv.remove(key)
    }

    /**
     * 获取所有key
     */
    fun getAllKeys(): MutableSet<String> {
        return kv.all.keys
    }

    /**
     * 是否包含某个key
     */
    fun containKey(key: String): Boolean {
        return kv.contains(key)
    }

    /**
     * fast kv的加密实现接口
     */
    private class Cipher(val key: String) : FastCipher {
        override fun encrypt(src: ByteArray): ByteArray {
            return AESHelper.encrypt(src, key)
        }

        override fun decrypt(dst: ByteArray): ByteArray {
            return AESHelper.decrypt(dst, key)
        }

        override fun encrypt(src: Int): Int {
            return src
        }

        override fun encrypt(src: Long): Long {
            return src
        }

        override fun decrypt(dst: Int): Int {
            return dst
        }

        override fun decrypt(dst: Long): Long {
            return dst
        }

    }
}
