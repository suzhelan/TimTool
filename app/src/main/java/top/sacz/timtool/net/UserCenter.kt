package top.sacz.timtool.net

import top.sacz.timtool.net.entity.TokenInfo
import top.sacz.timtool.net.entity.User
import top.sacz.xphelper.util.ConfigUtils


object UserCenter {

    fun setUserInfo(user: User) {
        getConfig().put("user_info", user)
    }

    fun getUserInfo(): User {
        val defaultUser = User(uin = "0", nickname = "未同步")
        return try {
            getConfig().getObject("user_info", User::class.java) ?: defaultUser
        } catch (e: Exception) {
            defaultUser
        }
    }

    fun setUpdateToken(token: TokenInfo) {
        getConfig().put("token", token)
    }

    fun removeAll() {
        getConfig().remove("token")
    }

    fun getTokenInfo(): TokenInfo? {
        return try {
            getConfig().getObject("token", TokenInfo::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private fun getConfig() = ConfigUtils("user")

}
